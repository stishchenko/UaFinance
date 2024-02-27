package com.tish;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.tish.interfaces.ChangeLanguageListener;
import com.tish.interfaces.FragmentSendDataListener;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity implements ChangeLanguageListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        Toolbar toolbar = findViewById(R.id.no_spinner_toolbar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings_title);

    }

    @Override
    public void onSendData() {
        Intent reIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
        startActivity(reIntent);
        finish();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private ChangeLanguageListener resetLang;

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            try {
                resetLang = (ChangeLanguageListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " должен реализовывать интерфейс OnFragmentInteractionListener");
            }
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference langList = findPreference("lang");
            langList.setValue(getString(R.string.current_locale));
            langList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!langList.getValue().equals(String.valueOf(newValue))) {
                        Locale locale = new Locale(String.valueOf(newValue));
                        Locale.setDefault(locale);
                        Configuration configuration = new Configuration();
                        configuration.setLocale(locale);
                        getContext().getResources().updateConfiguration(configuration, null);
                        resetLang.onSendData();
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent backIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(backIntent);
        return true;
    }
}