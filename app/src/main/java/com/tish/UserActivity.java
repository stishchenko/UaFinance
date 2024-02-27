package com.tish;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.tish.interfaces.FragmentSendAccountDataListener;
import com.tish.interfaces.FragmentSendDataListener;

public class UserActivity extends AppCompatActivity implements FragmentSendAccountDataListener {

    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
    }

    private void initView() {

        Toolbar toolbar = findViewById(R.id.no_spinner_toolbar_view);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view_user);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent openIntent = new Intent();
                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        initProfile();
                        break;
                    case R.id.nav_account_manager:
                        initAccountManager();
                        break;
                    case R.id.nav_list:
                        openIntent.setClass(UserActivity.this, MainActivity.class);
                        openIntent.putExtra("fragment", 'c');
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_map:
                        openIntent.setClass(UserActivity.this, MainActivity.class);
                        openIntent.putExtra("fragment", 'm');
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_statistic:
                        openIntent.setClass(UserActivity.this, StatisticsActivity.class);
                        startActivity(openIntent);
                        finish();
                        break;
                    case R.id.nav_settings:
                        openIntent.setClass(UserActivity.this, SettingsActivity.class);
                        startActivity(openIntent);
                        finish();
                        break;
                    default:
                        Toast.makeText(UserActivity.this, "Nothing selected", Toast.LENGTH_LONG).show();
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        Bundle args = getIntent().getExtras();
        if (args.getChar("fragment") == 'p') {
            navigationView.setCheckedItem(R.id.nav_profile);
            initProfile();
        } else if (args.getChar("fragment") == 'a') {
            navigationView.setCheckedItem(R.id.nav_account_manager);
            initAccountManager();
        }
    }

    private void initProfile() {
        getSupportActionBar().setTitle(R.string.nav_item_profile);
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.user_container, profileFragment, "TAG_PR_FRAGMENT");
        fragmentTransaction.commit();
    }

    private void initAccountManager() {
        getSupportActionBar().setTitle(R.string.nav_item_acc_manager);
        AccountManagerFragment accountManagerFragment = new AccountManagerFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.user_container, accountManagerFragment, "TAG_AM_FRAGMENT");
        fragmentTransaction.commit();
    }


    @Override
    public void onSendData(long data, String fragmentTag, char type) {
        if (data > 0) {
            AccountManagerFragment amf;
            amf = (AccountManagerFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
            if (type == 'a')
                amf.updateAccountList();
            else
                initAccountManager();
        } else if (data == -1)
            Toast.makeText(this, "Введений рахунок вже існує", Toast.LENGTH_SHORT).show();
        else if (data == 0)
            Toast.makeText(this, "При оновлені даних рахунку виникла помилка", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_user);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}