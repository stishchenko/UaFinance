package com.tish;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tish.db.bases.PrefManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfileFragment extends Fragment {

    EditText nameEditText;
    EditText ageEditText;
    EditText professionEditText;

    TextView startDateTextView;
    TextView periodTextView;

    RadioGroup genderRadioGroup;

    PrefManager prefManager;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        prefManager = new PrefManager(getContext(), "");

        nameEditText = view.findViewById(R.id.et_user_name);
        nameEditText.setText(prefManager.getUserName());
        ageEditText = view.findViewById(R.id.et_user_age);
        ageEditText.setText(prefManager.getUserAge());
        professionEditText = view.findViewById(R.id.et_user_profession);
        professionEditText.setText(prefManager.getUserProfession());
        genderRadioGroup = view.findViewById(R.id.rg_user_gender);
        genderRadioGroup.check(prefManager.getUserGender());

        startDateTextView = view.findViewById(R.id.tv_start_date);
        startDateTextView.setText(LocalDate.parse(prefManager.getFirstDate()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        periodTextView = view.findViewById(R.id.tv_working_period);
        periodTextView.setText(String.format(getString(R.string.using_period_days), LocalDate.now().toEpochDay() - LocalDate.parse(prefManager.getFirstDate()).toEpochDay()));

        return view;
    }

    @Override
    public void onStop() {
        saveAll();
        super.onStop();
    }

    private void saveAll() {
        prefManager.setUserName(nameEditText.getText().toString());
        prefManager.setUserAge(ageEditText.getText().toString());
        prefManager.setUserProfession(professionEditText.getText().toString());
        prefManager.setUserGender(genderRadioGroup.getCheckedRadioButtonId());
    }
}
