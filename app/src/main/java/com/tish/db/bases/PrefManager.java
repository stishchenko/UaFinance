package com.tish.db.bases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.tish.R;

import java.time.LocalDate;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class PrefManager {

    private final String APP_LANG = "lang";
    private final String SETUP_CHART = "diagram";
    private final String SHOW_NAMES = "names";
    private final String SHOW_VALUES = "values";
    private final String SHOW_VALUES_AS_PERCENT = "percent";


    private final String IS_FIRST_KEY = "isFirst";
    private final String FIRST_DATE_KEY = "firstDate";

    private final String USER_NAME_KEY = "name";
    private final String USER_AGE_KEY = "age";
    private final String USER_PROFESSION_KEY = "profession";
    private final String USER_GENDER_KEY = "gender";

    private final String HAS_SETTINGS_KEY = "hasSettings";
    private final String TYPE_KEY = "type";
    private final String IS_DATE_CHECKED_KEY = "isDateSetup";
    private final String DATE_TYPE_KEY = "dateType";
    private final String DATE_SETUP_KEY = "dateSetup";

    private final String DEF_FILE = "def";
    private final String FIRST_FILE = "first";
    private final String USER_FILE = "userData";
    private final String STAT_FILE = "statSettings";

    private SharedPreferences defPrefs;
    private SharedPreferences firstPrefs;
    private SharedPreferences userPrefs;
    private SharedPreferences statPrefs;
    private SharedPreferences.Editor firstEditor;
    private SharedPreferences.Editor userEditor;
    private SharedPreferences.Editor statEditor;
    private Context context;

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context c, String fileName) {
        this.context = c;
        switch (fileName) {
            case DEF_FILE:
                defPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                break;
            case FIRST_FILE:
                firstPrefs = context.getSharedPreferences(FIRST_FILE, Context.MODE_PRIVATE);
                firstEditor = firstPrefs.edit();
                break;
            case USER_FILE:
                userPrefs = context.getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
                userEditor = userPrefs.edit();
                break;
            case STAT_FILE:
                statPrefs = context.getSharedPreferences(STAT_FILE, Context.MODE_PRIVATE);
                statEditor = statPrefs.edit();
                break;
            default:
                firstPrefs = context.getSharedPreferences(FIRST_FILE, Context.MODE_PRIVATE);
                userPrefs = context.getSharedPreferences(USER_FILE, Context.MODE_PRIVATE);
                firstEditor = firstPrefs.edit();
                userEditor = userPrefs.edit();
                break;
        }
    }

    public String getLanguage() {
        return defPrefs.getString(APP_LANG, "uk");
    }

    public boolean isChartSetupChecked() {
        return defPrefs.getBoolean(SETUP_CHART, false);
    }

    public boolean isNamesShown() {
        return defPrefs.getBoolean(SHOW_NAMES, false);
    }

    public boolean isValuesShown() {
        return defPrefs.getBoolean(SHOW_VALUES, false);
    }

    public boolean isPercentShown() {
        return defPrefs.getBoolean(SHOW_VALUES_AS_PERCENT, false);
    }

    public boolean isFirstLaunch() {
        return firstPrefs.getBoolean(IS_FIRST_KEY, true);
    }

    public void setFirstLaunch(boolean isFirst) {
        firstEditor.putBoolean(IS_FIRST_KEY, isFirst).apply();
    }

    public String getFirstDate() {
        return firstPrefs.getString(FIRST_DATE_KEY, "");
    }

    public void setFirstDate() {
        firstEditor.putString(FIRST_DATE_KEY, LocalDate.now().toString()).apply();
    }

    public String getUserName() {
        return userPrefs.getString(USER_NAME_KEY, "");
    }

    public void setUserName(String name) {
        userEditor.putString(USER_NAME_KEY, name).apply();
    }

    public String getUserAge() {
        return userPrefs.getString(USER_AGE_KEY, "");
    }

    public void setUserAge(String age) {
        userEditor.putString(USER_AGE_KEY, age).apply();
    }

    public String getUserProfession() {
        return userPrefs.getString(USER_PROFESSION_KEY, "");
    }

    public void setUserProfession(String profession) {
        userEditor.putString(USER_PROFESSION_KEY, profession).apply();
    }

    public int getUserGender() {
        return userPrefs.getInt(USER_GENDER_KEY, R.id.rb_user_gender_male);
    }

    public void setUserGender(int gender) {
        userEditor.putInt(USER_GENDER_KEY, gender).apply();
    }

    public boolean hasSettings() {
        return statPrefs.getBoolean(HAS_SETTINGS_KEY, false);
    }

    public void setHasSettings(boolean hasSetting) {
        statEditor.putBoolean(HAS_SETTINGS_KEY, hasSetting).apply();
    }

    public int getType() {
        return statPrefs.getInt(TYPE_KEY, R.id.rb_category);
    }

    public void setType(int type) {
        statEditor.putInt(TYPE_KEY, type).apply();
    }

    public boolean isDateSetupChecked() {
        return statPrefs.getBoolean(IS_DATE_CHECKED_KEY, false);
    }

    public void setDateSetupChecked(boolean isChecked) {
        statEditor.putBoolean(IS_DATE_CHECKED_KEY, isChecked).apply();
    }

    public int getDateType() {
        return statPrefs.getInt(DATE_TYPE_KEY, R.id.rb_date_mouth);
    }

    public void setDateType(int dateType) {
        statEditor.putInt(DATE_TYPE_KEY, dateType).apply();
    }

    public int getDateSetup() {
        return statPrefs.getInt(DATE_SETUP_KEY, 0);
    }

    public void setDateSetup(int dateSetup) {
        statEditor.putInt(DATE_SETUP_KEY, dateSetup).apply();
    }
}
