package com.tish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.tish.db.bases.PrefManager;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.dialogs.AddCostDialog;
import com.tish.interfaces.FragmentSendDataListener;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements FragmentSendDataListener {

    private FragmentTransaction fragmentTransaction;

    Spinner spinner;

    private int currentSortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefManager langManager = new PrefManager(getBaseContext(), "def");
        if (!getString(R.string.current_locale).equals(langManager.getLanguage())) {
            Locale locale = new Locale(langManager.getLanguage());
            Locale.setDefault(locale);
            Configuration configuration = new Configuration();
            configuration.setLocale(locale);
            getBaseContext().getResources().updateConfiguration(configuration, null);
        }
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        currentSortType = 0;
        Toolbar toolbar = findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        spinner = findViewById(R.id.toolbar_spinner_account);
        AccPhoConnector accPhoConnector = new AccPhoConnector(this);
        List<String> accountList = accPhoConnector.getAccounts();
        accountList.add(0, getResources().getString(R.string.app_name));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accountList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                CostsListFragment clf = (CostsListFragment) getSupportFragmentManager().findFragmentByTag("TAG_COSTS_FRAGMENT");
                if (position > 0) {
                    Bundle ab = new Bundle();
                    ab.putString("account", accountList.get(position));
                    clf.getArguments().clear();
                    clf.setArguments(ab);
                    clf.updateDataByDateAccount("", true);
                } else
                    initCostFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        PrefManager prefManager = new PrefManager(getApplicationContext(), "first");
        if (prefManager.isFirstLaunch()) {
            prefManager.setFirstLaunch(false);
            prefManager.setFirstDate();
            createWelcomeMessage();
        }

        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (args.getChar("fragment") == 'c') {
                initCostFragment();
            } else if (args.getChar("fragment") == 'm') {
                initMapFragment();
            }
        } else
            initCostFragment();

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent openIntent = new Intent();
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        openIntent.setClass(MainActivity.this, UserActivity.class);
                        openIntent.putExtra("fragment", 'p');
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_account_manager:
                        openIntent.setClass(MainActivity.this, UserActivity.class);
                        openIntent.putExtra("fragment", 'a');
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_list:
                        initCostFragment();
                        break;
                    case R.id.nav_map:
                        initMapFragment();
                        break;
                    case R.id.nav_statistic:
                        openIntent.setClass(MainActivity.this, StatisticsActivity.class);
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_settings:
                        openIntent.setClass(MainActivity.this, SettingsActivity.class);
                        startActivity(openIntent);
                        finish();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Nothing selected", Toast.LENGTH_LONG).show();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void initCostFragment() {
        if (getSupportActionBar() != null)
            getSupportActionBar().show();

        CostsListFragment costsListFragment = new CostsListFragment();
        Bundle accountBundle = new Bundle();
        accountBundle.putString("account", spinner.getSelectedItem().toString());
        costsListFragment.setArguments(accountBundle);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, costsListFragment, "TAG_COSTS_FRAGMENT");
        fragmentTransaction.commit();
    }

    private void initMapFragment() {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        MapsFragment mapsFragment = new MapsFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, mapsFragment, "TAG_MAP_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    public void onSendData(long data, String fragmentTag) {
        if (data > 0) {
            CostsListFragment clf = (CostsListFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (spinner.getSelectedItem().toString().equals(getResources().getString(R.string.app_name)))
                clf.updateDataByDate("", true);
            else {
                Bundle ab = new Bundle();
                ab.putString("account", spinner.getSelectedItem().toString());
                clf.setArguments(ab);
                clf.updateDataByDateAccount("", true);
            }
        } else if (data == -1)
            Toast.makeText(this, "При обробці витрати виникла помилка", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_cost_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_cost:
                AddCostDialog addCostDialog = new AddCostDialog(MainActivity.this);
                addCostDialog.show(getSupportFragmentManager(), "acd");
                break;
            case R.id.item_sort_list:
                CostsListFragment clf = (CostsListFragment) getSupportFragmentManager().findFragmentByTag("TAG_COSTS_FRAGMENT");
                if (currentSortType == 0) {
                    clf.sortCostList(-1);
                    currentSortType = -1;
                } else if (currentSortType == -1) {
                    clf.sortCostList(1);
                    currentSortType = 1;
                } else {
                    clf.sortCostList(0);
                    currentSortType = 0;
                }
                break;
            //case R.id.item_change_type:
            //describe
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void createWelcomeMessage() {
        AlertDialog welcomeDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.welcome_title)
                .setMessage(Html.fromHtml(getString(R.string.welcome_message), Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        welcomeDialog.getWindow().setBackgroundDrawableResource(R.drawable.background_nav_header);
        welcomeDialog.show();
    }

}