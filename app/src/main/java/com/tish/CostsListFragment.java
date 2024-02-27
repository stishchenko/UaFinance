package com.tish;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.tish.adapters.CostsExpListAdapter;
//import com.tish.db.bases.PhotoManager;
//import com.tish.db.bases.UkrainianMonth;
import com.tish.db.bases.PrefManager;
import com.tish.db.connectors.CostConnector;
import com.tish.dialogs.EditCostDialog;
import com.tish.dialogs.EditGeoDialog;
//import com.tish.dialogs.EditPhotoDialog;
//import com.tish.dialogs.GetPhotoDialog;
import com.tish.models.Cost;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CostsListFragment extends Fragment {

    ImageButton backButton;
    ImageButton forwardButton;
    TextSwitcher ts;
    List<YearMonth> dateList;
    int dateCounter = 0;
    Animation slideInRight;
    Animation slideOutLeft;
    Animation slideInLeft;
    Animation slideOutRight;

    YearMonth thisYearMonth;
    YearMonth currentYearMonth;
    String account = "";

    ExpandableListView costsListView;
    CostsExpListAdapter costsListAdapter;

    ArrayList<Cost> costsList;
    CostConnector costConnector;

    List<PieEntry> entries;
    int[] colors;
    PieChart chart;
    PieDataSet set;
    PieData data;

    boolean costsExist = false;
    ArrayList<Cost> sortedCostsList;
    private String[] months;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_costs_list, container, false);
        costsListView = view.findViewById(R.id.ex_list_costs);
        backButton = view.findViewById(R.id.ib_back);
        forwardButton = view.findViewById(R.id.ib_forward);
        ts = view.findViewById(R.id.ts_costs);
        chart = view.findViewById(R.id.chart);

        costConnector = new CostConnector(getContext());

        if (getArguments() != null)
            account = getArguments().getString("account");

        costsExist = costConnector.costsExist();

        initDates(costsExist);

        initLists();

        if (!costsExist)
            costsList = new ArrayList<>();

        costsListAdapter = new CostsExpListAdapter(getContext(), costsList);
        costsListView.setAdapter(costsListAdapter);


        set = new PieDataSet(entries, "Costs");
        set.setColors(colors);
        data = new PieData(set);
        setChartOptions(costsExist);
        chart.setData(data);
        chart.invalidate();

        initButtons();

        costsListView.setLongClickable(true);
        registerForContextMenu(costsListView);

        return view;
    }

    private void initLists() {
        if (costsExist && account.equals(getContext().getResources().getString(R.string.app_name))) {
            costsList = costConnector.getCostsByDate(thisYearMonth.toString());
            if (costsList.size() > 0) {
                entries = costConnector.getTotalAmountsForCategoriesByDate(thisYearMonth.toString());
                colors = costConnector.getCategoriesColorsByDate(thisYearMonth.toString());
                for (int i = 0; i < colors.length; i++) {
                    colors[i] = getResources().getColor(colors[i], null);
                }
            } else {
                entries = new ArrayList<>();
                entries.add(new PieEntry(1));
                colors = new int[1];
                colors[0] = getResources().getColor(R.color.bright_blue, null);
            }
        } else if (costsExist) {
            costsList = costConnector.getCostsByDateAccount(thisYearMonth.toString(), account);
            if (costsList.size() > 0) {
                entries = costConnector.getTotalAmountsForCategoriesByDateAccount(thisYearMonth.toString(), account);
                colors = costConnector.getCategoriesColorsByDateAccount(thisYearMonth.toString(), account);
                for (int i = 0; i < colors.length; i++) {
                    colors[i] = getResources().getColor(colors[i], null);
                }
            } else {
                entries = new ArrayList<>();
                entries.add(new PieEntry(1));
                colors = new int[1];
                colors[0] = getResources().getColor(R.color.bright_blue, null);
            }
        } else {
            entries = new ArrayList<>();
            entries.add(new PieEntry(1));
            colors = new int[1];
            colors[0] = getResources().getColor(R.color.bright_blue, null);
        }
    }

    private void initDates(boolean hasCosts) {
        months = getResources().getStringArray(R.array.month);
        thisYearMonth = YearMonth.now();
        currentYearMonth = thisYearMonth;
        if (hasCosts) {
            dateList = costConnector.getCostDates(0);
            dateCounter = dateList.size() - 1;
            if (!dateList.get(dateCounter).equals(thisYearMonth)) {
                dateList.add(thisYearMonth);
                dateCounter++;
            }
        } else {
            dateCounter = 0;
            dateList = new ArrayList<>();
            dateList.add(thisYearMonth);
        }
        ts.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView tv = new TextView(getContext());
                tv.setTextSize(20);
                tv.setTextColor(Color.BLUE);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                return tv;
            }
        });
        ts.setText(months[thisYearMonth.getMonthValue() - 1] + ", " + thisYearMonth.getYear());
        //ts.setText(UkrainianMonth.valueOf(thisYearMonth.getMonth().toString()).getUrkMonth() + ", " + thisYearMonth.getYear());
    }

    private void setChartOptions(boolean hasCosts) {
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(15);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getLegend().setEnabled(false);
        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.setCenterTextSize(20);
        float sum = entries.stream().map(PieEntry::getValue).reduce(0f, Float::sum);
        if (hasCosts && sum > 1) {
            chart.setCenterText("-" + sum);
        } else {
            chart.setCenterText(getResources().getString(R.string.total_amount_text));
        }

        PrefManager chartManager = new PrefManager(getContext(), "def");
        if (chartManager.isChartSetupChecked()) {
            chart.setDrawEntryLabels(chartManager.isNamesShown());
            if (chartManager.isValuesShown()) {
                set.setDrawValues(true);
                chart.setUsePercentValues(chartManager.isPercentShown());
            } else {
                set.setDrawValues(false);
                chart.setUsePercentValues(false);
            }
        } else {
            chart.setUsePercentValues(true);
            chart.setDrawEntryLabels(false);
            set.setDrawValues(false);
        }
        chart.setSelected(false);
    }

    private void initButtons() {
        slideInRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateCounter != 0) {
                    dateCounter--;
                    ts.setInAnimation(slideInRight);
                    ts.setOutAnimation(slideOutLeft);
                    YearMonth yearMonth = dateList.get(dateCounter);
                    ts.setText(months[yearMonth.getMonthValue() - 1] + ", " + yearMonth.getYear());

                    currentYearMonth = yearMonth;
                    if (account.equals(getContext().getResources().getString(R.string.app_name)))
                        updateDataByDate(yearMonth.toString(), false);
                    else
                        updateDataByDateAccount(yearMonth.toString(), false);
                }
            }
        });

        slideInLeft = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
        slideOutRight = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateCounter != dateList.size() - 1) {
                    dateCounter++;
                    ts.setInAnimation(slideInLeft);
                    ts.setOutAnimation(slideOutRight);
                    YearMonth yearMonth = dateList.get(dateCounter);
                    ts.setText(months[yearMonth.getMonthValue() - 1] + ", " + yearMonth.getYear());

                    currentYearMonth = yearMonth;
                    if (account.equals(getContext().getResources().getString(R.string.app_name)))
                        updateDataByDate(yearMonth.toString(), false);
                    else
                        updateDataByDateAccount(yearMonth.toString(), false);
                }
            }
        });
    }

    void updateDataByDate(String date, boolean useCurrent) {

        costsList.clear();
        entries.clear();
        set.clear();
        data.clearValues();

        if (useCurrent)
            date = currentYearMonth.toString();

        costsList = costConnector.getCostsByDate(date);
        costsListAdapter.setList(costsList);
        costsListView.setAdapter(costsListAdapter);
        if (costsList.size() > 0) {
            entries = costConnector.getTotalAmountsForCategoriesByDate(date);
            colors = costConnector.getCategoriesColorsByDate(date);
            for (int i = 0; i < colors.length; i++) {
                colors[i] = getResources().getColor(colors[i], null);
            }
            chart.setCenterText("-" + entries.stream().map(PieEntry::getValue).reduce(0f, Float::sum));
        } else {
            entries = new ArrayList<>();
            entries.add(new PieEntry(1));
            colors = new int[1];
            colors[0] = getResources().getColor(R.color.bright_blue, null);
            chart.setCenterText(getString(R.string.total_amount_text));
        }

        set.setValues(entries);
        set.setColors(colors);

        data.setDataSet(set);

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    void updateDataByDateAccount(String date, boolean useCurrent) {

        boolean noCosts = false;

        costsList.clear();
        entries.clear();
        set.clear();
        data.clearValues();

        if (useCurrent)
            date = currentYearMonth.toString();

        if (getArguments() != null)
            account = getArguments().getString("account");

        costsList = costConnector.getCostsByDateAccount(date, account);
        if (costsList.size() > 0) {
            entries = costConnector.getTotalAmountsForCategoriesByDateAccount(date, account);
            colors = costConnector.getCategoriesColorsByDateAccount(date, account);
            for (int i = 0; i < colors.length; i++) {
                colors[i] = getResources().getColor(colors[i], null);
            }
        } else {
            entries = new ArrayList<>();
            entries.add(new PieEntry(1));
            colors = new int[1];
            colors[0] = getResources().getColor(R.color.bright_blue, null);
            noCosts = true;
        }
        costsListAdapter.setList(costsList);
        costsListView.setAdapter(costsListAdapter);


        set.setValues(entries);
        set.setColors(colors);

        data.setDataSet(set);

        if (noCosts)
            chart.setCenterText(getResources().getString(R.string.total_amount_text));
        else
            chart.setCenterText("-" + entries.stream().map(PieEntry::getValue).reduce(0f, Float::sum));

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    void sortCostList(int type) {
        if (costsList.size() > 0)
            sortedCostsList = new ArrayList<>(costsList);
        switch (type) {
            case -1:
                Collections.sort(sortedCostsList, Collections.reverseOrder(Cost.COST_COMPARATOR));
                costsListAdapter.setList(sortedCostsList);
                costsListView.setAdapter(costsListAdapter);
                break;
            case 1:
                Collections.sort(sortedCostsList, Cost.COST_COMPARATOR);
                costsListAdapter.setList(sortedCostsList);
                costsListView.setAdapter(costsListAdapter);
                break;
            default:
                if (costsExist && account.equals(getContext().getResources().getString(R.string.app_name)))
                    costsList = costConnector.getCostsByDate(thisYearMonth.toString());
                else if (costsExist)
                    costsList = costConnector.getCostsByDateAccount(thisYearMonth.toString(), account);
                costsListAdapter.setList(costsList);
                costsListView.setAdapter(costsListAdapter);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View
            v, @Nullable ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.cost_list_context_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();
        Cost selectedCost = costsListAdapter.getGroup(ExpandableListView.getPackedPositionGroup(info.packedPosition));
        switch (item.getItemId()) {
            case R.id.context_item_edit_cost:
                EditCostDialog editCostDialog = new EditCostDialog(getContext(), selectedCost);
                editCostDialog.show(getActivity().getSupportFragmentManager(), "ecd");
                return true;
            case R.id.context_item_edit_geo:
                EditGeoDialog editGeoDialog = new EditGeoDialog(getContext(), selectedCost.getGeo(), selectedCost.getCostId());
                editGeoDialog.show(getActivity().getSupportFragmentManager(), "egd");
                return true;
            /*case R.id.context_item_edit_photo:
                if (selectedCost.isPhotoExists()) {
                    EditPhotoDialog editPhotoDialog = new EditPhotoDialog(getContext(), selectedCost.getPhotoAddress(), selectedCost.getCostId());
                    editPhotoDialog.show(getActivity().getSupportFragmentManager(), "epd");
                } else {
                    GetPhotoDialog addPhotoDialog = new GetPhotoDialog(getContext(), selectedCost.getCostId());
                    addPhotoDialog.show(getActivity().getSupportFragmentManager(), "apd");
                }
                return true;*/
            case R.id.context_item_delete_cost:
                ShapeDrawable sd = new ShapeDrawable(new OvalShape());
                sd.setIntrinsicWidth(40);
                sd.setIntrinsicWidth(40);
                sd.getPaint().setColor(getContext().getResources().getColor(selectedCost.getCategory().getColorResource(), null));
                BitmapDrawable bd = (BitmapDrawable) getContext().getResources().getDrawable(selectedCost.getCategory().getIconResource(), null);
                LayerDrawable icon = new LayerDrawable(new Drawable[]{sd, bd});
                AlertDialog deleteDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_delete_cost)
                        .setIcon(icon)
                        .setMessage(String.format(getResources().getString(R.string.message_delete_cost),
                                getString(selectedCost.getCategoryName()), selectedCost.getAmount()))
                        .setPositiveButton(R.string.button_yes_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int result = costConnector.deleteCost(selectedCost.getCostId());
                                if (result > 0) {
                                    if (account.equals(getResources().getString(R.string.app_name)))
                                        updateDataByDate("", true);
                                    else {
                                        updateDataByDateAccount("", true);
                                    }
                                    dialog.dismiss();
                                } else
                                    Toast.makeText(getContext(), "При видаленні сталась помилка", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();
                deleteDialog.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}