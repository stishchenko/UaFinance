package com.tish;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tish.adapters.StatisticsListAdapter;
import com.tish.db.bases.PrefManager;
import com.tish.db.bases.Season;
//import com.tish.db.bases.UkrainianMonth;
import com.tish.db.connectors.CostConnector;
import com.tish.db.connectors.StatisticsConnector;
import com.tish.dialogs.SetupStatisticsDialog;
import com.tish.interfaces.FragmentSendSettingDataListener;
import com.tish.models.StatisticsItem;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsActivity extends AppCompatActivity implements FragmentSendSettingDataListener {

    CostConnector costConnector;
    StatisticsConnector statisticsConnector;

    StatisticsListAdapter statisticsAdapter;

    ListView statisticsListView;
    Spinner simpleDateSpinner;

    PrefManager prefManager;
    int type;
    Map<String, String> dateSettingsMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        costConnector = new CostConnector(StatisticsActivity.this);
        statisticsConnector = new StatisticsConnector(StatisticsActivity.this);
        prefManager = new PrefManager(getApplicationContext(), "statSettings");

        type = R.id.rb_category;
        dateSettingsMap.put("date", "isn`t");
        initToolbar();
        initContent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.no_spinner_toolbar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nav_item_statistic);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initContent() {
        statisticsListView = findViewById(R.id.lv_statistics);
        simpleDateSpinner = findViewById(R.id.spinner_statistics_date);
        Button getStatisticsButton = findViewById(R.id.button_get_statistics);

        YearMonth thisYearMonth = YearMonth.now();
        List<YearMonth> dateList = costConnector.getCostDates(1);
        if (!dateList.contains(thisYearMonth)) {
            dateList.add(0, thisYearMonth);
        }
        List<String> spinnerDatesList = dateList.stream().map(YearMonth::toString).collect(Collectors.toList());
        spinnerDatesList.add(0, getString(R.string.spinner_item_all_dates));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerDatesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        simpleDateSpinner.setAdapter(adapter);
        simpleDateSpinner.setSelection(0);

        getStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (simpleDateSpinner.isEnabled()) {
                    int selectedId = (int) simpleDateSpinner.getSelectedItemId();
                    switch (type) {
                        case R.id.rb_category:
                            if (selectedId == 0) {
                                initList("", 'c');
                            } else
                                initList(spinnerDatesList.get(selectedId).toString(), 'c');
                            break;
                        case R.id.rb_market:
                            if (selectedId == 0) {
                                initList("", 'm');
                            } else
                                initList(spinnerDatesList.get(selectedId).toString(), 'm');
                            break;
                        case R.id.rb_geo:
                            if (selectedId == 0) {
                                initList("", 'g');
                            } else
                                initList(spinnerDatesList.get(selectedId).toString(), 'g');
                            break;
                    }
                } else {
                    List<StatisticsItem> dateStatisticsList = new ArrayList<>();
                    switch (type) {
                        case R.id.rb_category:
                            if (dateSettingsMap.get("dateType").equals("m")) {
                                dateStatisticsList = statisticsConnector
                                        .getCategoryMarketStatisticsByMonthSeason('c', 'm', dateSettingsMap.get("dateContent"));
                            } else if (dateSettingsMap.get("dateType").equals("s")) {
                                dateStatisticsList = statisticsConnector
                                        .getCategoryMarketStatisticsByMonthSeason('c', 's',
                                                Season.values()[Integer.parseInt(dateSettingsMap.get("dateContent"))].getNumbers());
                            } else if (dateSettingsMap.get("dateType").equals("y")) {
                                dateStatisticsList = statisticsConnector.getCategoryMarketStatisticsByYear('c');
                            }
                            break;
                        case R.id.rb_market:
                            if (dateSettingsMap.get("dateType").equals("m")) {
                                dateStatisticsList = statisticsConnector
                                        .getCategoryMarketStatisticsByMonthSeason('m', 'm', dateSettingsMap.get("dateContent"));
                            } else if (dateSettingsMap.get("dateType").equals("s")) {
                                dateStatisticsList = statisticsConnector
                                        .getCategoryMarketStatisticsByMonthSeason('m', 's',
                                                Season.values()[Integer.parseInt(dateSettingsMap.get("dateContent"))].getNumbers());
                            } else if (dateSettingsMap.get("dateType").equals("y")) {
                                dateStatisticsList = statisticsConnector.getCategoryMarketStatisticsByYear('m');
                            }
                            break;
                        case R.id.rb_geo:
                            if (dateSettingsMap.get("dateType").equals("m")) {
                                dateStatisticsList = statisticsConnector.getGeoStatisticsByMonthSeason('m', dateSettingsMap.get("dateContent"));
                            } else if (dateSettingsMap.get("dateType").equals("s")) {
                                dateStatisticsList = statisticsConnector
                                        .getGeoStatisticsByMonthSeason('s',
                                                Season.values()[Integer.parseInt(dateSettingsMap.get("dateContent"))].getNumbers());
                            } else if (dateSettingsMap.get("dateType").equals("y")) {
                                dateStatisticsList = statisticsConnector.getGeoStatisticsByYear();
                            }
                            break;
                    }

                    if (dateStatisticsList.size() > 0) {
                        if (dateSettingsMap.get("dateType").equals("y"))
                            statisticsAdapter = new StatisticsListAdapter(StatisticsActivity.this, dateStatisticsList, true);
                        else if (dateSettingsMap.get("dateType").equals("m"))
                            statisticsAdapter = new StatisticsListAdapter(StatisticsActivity.this, dateStatisticsList,
                                    dateSettingsMap.get("dateType"),
                                    getResources().getStringArray(R.array.month)[Integer.parseInt(dateSettingsMap.get("dateContent")) - 1]);
                        else
                            statisticsAdapter = new StatisticsListAdapter(StatisticsActivity.this, dateStatisticsList,
                                    dateSettingsMap.get("dateType"),
                                    getResources().getStringArray(R.array.season)[Integer.parseInt(dateSettingsMap.get("dateContent"))]);

                        statisticsListView.setAdapter(statisticsAdapter);
                        statisticsListView.setVisibility(View.VISIBLE);
                    } else {
                        statisticsListView.setVisibility(View.INVISIBLE);
                        Toast.makeText(StatisticsActivity.this, "Дані для отримання статистики відсутні", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initList(String currentDate, char type) {
        List<StatisticsItem> statisticsList = new ArrayList<>();
        switch (type) {
            case 'c':
            case 'm':
                if (currentDate.equals(""))
                    statisticsList = statisticsConnector.getCategoryMarketStatistics(type);
                else
                    statisticsList = statisticsConnector.getCategoryMarketStatisticsByDate(currentDate, type);
                break;
            case 'g':
                if (currentDate.equals(""))
                    statisticsList = statisticsConnector.getGeoStatistics();
                else
                    statisticsList = statisticsConnector.getGeoStatisticsByDate(currentDate);
                break;
        }

        if (statisticsList.size() > 0) {
            statisticsAdapter = new StatisticsListAdapter(StatisticsActivity.this, statisticsList, false);
            statisticsListView.setAdapter(statisticsAdapter);
            statisticsListView.setVisibility(View.VISIBLE);
        } else {
            statisticsListView.setVisibility(View.INVISIBLE);
            Toast.makeText(StatisticsActivity.this, "Дані для отримання статистики відсутні", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSendSettingData(Bundle settings) {
        if (settings.getInt("type") != type)
            type = settings.getInt("type");

        if (settings.getString("date").equals("isn`t")) {
            if (!dateSettingsMap.get("date").equals("isn`t")) {
                dateSettingsMap.clear();
                dateSettingsMap.put("date", "isn`t");
            }
            simpleDateSpinner.setEnabled(true);
        } else if (settings.getString("date").equals("is")) {
            if (dateSettingsMap.get("date").equals("isn`t")) {
                dateSettingsMap.replace("date", "isn`t", "is");
            }
            dateSettingsMap.put("dateType", settings.getString("dateType"));
            if (settings.getString("dateType").equals("y")) {
                dateSettingsMap.remove("dateContent");
                dateSettingsMap.remove("period");
            } else {
                dateSettingsMap.put("dateContent", settings.getString("dateContent"));
                dateSettingsMap.put("period", settings.getString("period"));
            }
            simpleDateSpinner.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_set_up_statistics:
                SetupStatisticsDialog setupStatisticsDialog = new SetupStatisticsDialog(StatisticsActivity.this);
                setupStatisticsDialog.show(getSupportFragmentManager(), "ssd");
                break;
            //case R.id.item_change_type_statistics:
            //describe
            // break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent backIntent = new Intent(StatisticsActivity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
        return true;
    }


}
