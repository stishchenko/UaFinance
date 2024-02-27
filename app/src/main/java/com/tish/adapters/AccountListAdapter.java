package com.tish.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.tish.R;
import com.tish.dialogs.AccountDialog;

import java.util.ArrayList;
import java.util.List;

public class AccountListAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> accountList;
    private FragmentManager activityFragmentManager;

    public AccountListAdapter(Context context, ArrayList<String> accountList, FragmentManager fragmentManager) {
        super(context, R.layout.item_account, accountList);
        this.context = context;
        this.accountList = accountList;
        this.activityFragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        AccountViewHolder viewHolder;
        if (view == null) {
            viewHolder = new AccountViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_account, parent, false);
            viewHolder.textViewNumber = view.findViewById(R.id.tv_account);
            viewHolder.buttonEditAccount = view.findViewById(R.id.ib_edit_account);
            view.setTag(viewHolder);
        } else
            viewHolder = (AccountViewHolder) view.getTag();

        viewHolder.textViewNumber.setText(accountList.get(position));
        viewHolder.buttonEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountDialog editAccountDialog = new AccountDialog(context);
                Bundle edit = new Bundle();
                edit.putString("editAccount", accountList.get(position));
                editAccountDialog.setArguments(edit);
                editAccountDialog.show(activityFragmentManager, "ead");
            }
        });
        return view;
    }

    private static class AccountViewHolder {
        static TextView textViewNumber;
        static ImageButton buttonEditAccount;
    }
}
