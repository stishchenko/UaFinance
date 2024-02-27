package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.R;
import com.tish.db.bases.Category;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.db.connectors.CostConnector;
import com.tish.interfaces.FragmentSendDataListener;
import com.tish.models.Cost;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EditCostDialog extends DialogFragment {
    private FragmentSendDataListener sendUpdateResult;

    EditText amountEditText;
    EditText dateEditText;
    EditText marketNameEditText;

    Spinner categorySpinner;
    Spinner accountSpinner;

    TextView errorTextView;

    CostConnector costConnector;
    Cost editCost;
    Context context;

    String dateRegexDayYear;
    String dateRegexYearDay;

    public EditCostDialog(Context context, Cost cost) {
        this.context = context;
        costConnector = new CostConnector(context);
        editCost = cost;
        dateRegexDayYear = "[0-3][0-9][-./][0-1][0-9][-./][0-9]{4}";
        dateRegexYearDay = "[0-9]{4}[-./][0-1][0-9][-./][0-3][0-9]";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendUpdateResult = (FragmentSendDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_cost);
        View editCostView = getActivity().getLayoutInflater().inflate(R.layout.edit_cost_dialog_view, null);
        amountEditText = editCostView.findViewById(R.id.et_edit_cost_amount);
        amountEditText.setText(String.valueOf(-1 * editCost.getAmount()));
        dateEditText = editCostView.findViewById(R.id.et_edit_cost_date);
        dateEditText.setText(editCost.getDate());
        marketNameEditText = editCostView.findViewById(R.id.et_edit_cost_market);
        marketNameEditText.setText(editCost.getMarketName());
        categorySpinner = editCostView.findViewById(R.id.spinner_edit_cost_category);
        accountSpinner = editCostView.findViewById(R.id.spinner_edit_cost_account);
        fillSpinners(editCost.getCategory().ordinal(), editCost.getAccountNumber());
        errorTextView = editCostView.findViewById(R.id.tv_edit_cost_error);
        builder.setView(editCostView);
        builder.setPositiveButton(R.string.button_edit, null);
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
                        String amount = amountEditText.getText().toString();
                        String date = dateEditText.getText().toString();
                        if (amount.equals("")) {
                            errorTextView.setText(R.string.enter_cost_sum_text);
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (date.equals("")) {
                            errorTextView.setText(R.string.enter_date_text);
                            errorTextView.setVisibility(View.VISIBLE);
                        } else if (!date.matches(dateRegexDayYear) && !date.matches(dateRegexYearDay) && !date.equals("-")) {
                            errorTextView.setText(R.string.failed_date_format_text);
                            errorTextView.setVisibility(View.VISIBLE);
                        } else {
                            errorTextView.setVisibility(View.INVISIBLE);

                            double costAmount = Double.parseDouble(amount);
                            if (-costAmount != editCost.getAmount())
                                editCost.setAmount(-costAmount);
                            if (date.equals("-"))
                                editCost.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                            else {
                                date = date.replaceAll("[./]", "-");
                                if (date.matches(dateRegexDayYear)) {
                                    date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString();
                                }
                                if (!editCost.getDate().equals(date))
                                    editCost.setDate(date);
                            }
                            if (!categorySpinner.getSelectedItem().toString().equals(getString(editCost.getCategoryName())))
                                editCost.setCategory(Category.values()[categorySpinner.getSelectedItemPosition()]);
                            if (!marketNameEditText.getText().toString().equals(editCost.getMarketName()))
                                editCost.setMarketName(marketNameEditText.getText().toString());
                            boolean updateAccount = false;
                            if (!accountSpinner.getSelectedItem().toString().equals(editCost.getAccountNumber())) {
                                if (accountSpinner.getSelectedItemPosition() == 0)
                                    editCost.setAccountNumber(null);
                                else
                                    editCost.setAccountNumber(accountSpinner.getSelectedItem().toString());
                                updateAccount = true;
                            }
                            //describe photo adding
                            long updateResult = costConnector.updateCost(editCost, updateAccount);
                            sendUpdateResult.onSendData(updateResult, "TAG_COSTS_FRAGMENT");
                            thisDialog.dismiss();
                        }
                    }
                });
            }
        });

        return thisDialog;
    }

    private void fillSpinners(int categoryOrdinal, String accountNumber) {
        List<String> categoryList = new ArrayList<>();
        for (Category c : Category.values()) {
            categoryList.add(getString(c.getCategoryName()));
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setSelection(categoryOrdinal);

        AccPhoConnector accPhoConnector = new AccPhoConnector(context);
        List<String> accountList = accPhoConnector.getAccounts();
        accountList.add(0, getResources().getString(R.string.spinner_item_without_account));
        ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, accountList);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountAdapter);
        if (accountNumber == null)
            accountSpinner.setSelection(0);
        else
            accountSpinner.setSelection(accountList.indexOf(accountNumber));
    }
}
