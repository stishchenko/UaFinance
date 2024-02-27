package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.R;
import com.tish.db.bases.PrefManager;
//import com.tish.db.bases.Season;
//import com.tish.db.bases.UkrainianMonth;
import com.tish.db.connectors.StatisticsConnector;
import com.tish.interfaces.FragmentSendSettingDataListener;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetupStatisticsDialog extends DialogFragment
        implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    private FragmentSendSettingDataListener sendSettings;

    View setupView;

    RadioGroup typeRadioGroup;

    CheckBox setupDateCheckBox;
    RadioGroup dateTypeRadioGroup;
    RadioButton dateMonthRadioButton;
    RadioButton dateSeasonRadioButton;
    RadioButton dateYearRadioButton;

    Spinner dateSettingSpinner;

    LinearLayout periodAmountLayout;
    EditText periodAmountEditText;

    StatisticsConnector statisticsConnector;
    Context context;
    PrefManager prefManager;

    public SetupStatisticsDialog(Context context) {
        this.context = context;
        statisticsConnector = new StatisticsConnector(context);
        prefManager = new PrefManager(context, "statSettings");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendSettings = (FragmentSendSettingDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_statistics_settings);

        setupView = getActivity().getLayoutInflater().inflate(R.layout.setup_statistics_dialog_view, null);
        typeRadioGroup = setupView.findViewById(R.id.rg_type);
        setupDateCheckBox = setupView.findViewById(R.id.cb_setup_date);
        setupDateCheckBox.setOnCheckedChangeListener(this);
        dateTypeRadioGroup = setupView.findViewById(R.id.rg_date_type);
        dateTypeRadioGroup.setOnCheckedChangeListener(this);
        dateMonthRadioButton = setupView.findViewById(R.id.rb_date_mouth);
        dateSeasonRadioButton = setupView.findViewById(R.id.rb_date_season);
        dateYearRadioButton = setupView.findViewById(R.id.rb_date_year);
        dateSettingSpinner = setupView.findViewById(R.id.spinner_date_setting);

        if (prefManager.hasSettings()) {
            typeRadioGroup.check(prefManager.getType());
            setupDateCheckBox.setChecked(prefManager.isDateSetupChecked());
            if (prefManager.isDateSetupChecked()) {
                dateTypeRadioGroup.check(prefManager.getDateType());
                if (prefManager.getDateType() != R.id.rb_date_year) {
                    dateSettingSpinner.setSelection(prefManager.getDateSetup());
                }
            }
        } else
            prefManager.setHasSettings(false);

        periodAmountLayout = setupView.findViewById(R.id.ll_period_amount);
        periodAmountEditText = setupView.findViewById(R.id.et_period_amount);
        periodAmountEditText.setText("1");

        builder.setView(setupView);

        builder.setPositiveButton(R.string.button_save, null);
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog thisDialog = builder.create();
        thisDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button saveButton = thisDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prefManager.setHasSettings(true);
                        Bundle settings = new Bundle();
                        int type = typeRadioGroup.getCheckedRadioButtonId();
                        settings.putInt("type", type);
                        prefManager.setType(type);
                        int dateType;
                        String dateContent;
                        String periodAmount;
                        if (setupDateCheckBox.isChecked()) {
                            settings.putString("date", "is");
                            prefManager.setDateSetupChecked(true);
                            dateType = dateTypeRadioGroup.getCheckedRadioButtonId();
                            prefManager.setDateType(dateTypeRadioGroup.getCheckedRadioButtonId());
                            if (dateType == R.id.rb_date_year) {
                                //dateContent = dateSettingSpinner.getSelectedItem().toString();
                                settings.putString("dateType", "y");
                                //settings.putString("dateContent", dateContent);
                            } else {
                                periodAmount = periodAmountEditText.getText().toString();
                                if (dateType == R.id.rb_date_mouth) {
                                    settings.putString("dateType", "m");
                                    int dc = dateSettingSpinner.getSelectedItemPosition() + 1;
                                    if (dc < 10)
                                        dateContent = "0" + dc;
                                    else
                                        dateContent = String.valueOf(dc);
                                } else {
                                    settings.putString("dateType", "s");
                                    dateContent = String.valueOf(dateSettingSpinner.getSelectedItemPosition());
                                }
                                settings.putString("dateContent", dateContent);
                                prefManager.setDateSetup(dateSettingSpinner.getSelectedItemPosition());
                                if (periodAmount.equals(""))
                                    settings.putString("period", "1");
                                else
                                    settings.putString("period", periodAmount);
                            }
                        } else {
                            settings.putString("date", "isn`t");
                            prefManager.setDateSetupChecked(false);
                        }

                        sendSettings.onSendSettingData(settings);

                        thisDialog.dismiss();
                    }
                });
            }
        });
        return thisDialog;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            dateMonthRadioButton.setEnabled(true);
            dateSeasonRadioButton.setEnabled(true);
            dateYearRadioButton.setEnabled(true);
            dateSettingSpinner.setVisibility(View.VISIBLE);
            //periodAmountLayout.setVisibility(View.VISIBLE);
        } else {
            dateMonthRadioButton.setEnabled(false);
            dateSeasonRadioButton.setEnabled(false);
            dateYearRadioButton.setEnabled(false);
            dateSettingSpinner.setVisibility(View.INVISIBLE);
            //periodAmountLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dateSettingSpinner.getLayoutParams();
        switch (checkedId) {
            case R.id.rb_date_mouth:
                fillSpinners("m");
                dateSettingSpinner.setVisibility(View.VISIBLE);
                //periodAmountLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_date_season:
                fillSpinners("s");
                dateSettingSpinner.setVisibility(View.VISIBLE);
                //periodAmountLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_date_year:
                dateSettingSpinner.setVisibility(View.INVISIBLE);
                //periodAmountLayout.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void fillSpinners(String dateType) {
        List<String> spinnerList = new ArrayList<>();
        if (dateType.equals("s")) {
            spinnerList.addAll(Arrays.asList(getResources().getStringArray(R.array.season)));
            //for (Season s : Season.values()) spinnerList.add(s.getName());
        } else {
            //spinnerList = statisticsConnector.getDatesBySettingType(dateType);
            if (dateType.equals("m")) {
                spinnerList.addAll(Arrays.asList(getResources().getStringArray(R.array.month)));
                //for (UkrainianMonth um : UkrainianMonth.values()) spinnerList.add(um.getUrkMonth());
            }
        }
        ArrayAdapter<String> dateSettingAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerList);
        dateSettingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSettingSpinner.setAdapter(dateSettingAdapter);
        if (dateType.equals("m")) {
            dateSettingSpinner.setSelection(YearMonth.now().getMonthValue() - 1);
        } else if (dateType.equals("s")) {
            int thisSeasonNumber = (int) YearMonth.now().getMonthValue() / 3;
            dateSettingSpinner.setSelection(thisSeasonNumber < 4 ? thisSeasonNumber : 0);
        } else if (dateType.equals("y")) {
            dateSettingSpinner.setSelection(spinnerList.size() - 1);
        }
    }
}
