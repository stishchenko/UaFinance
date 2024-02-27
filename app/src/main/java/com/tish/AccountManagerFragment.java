package com.tish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tish.adapters.AccountListAdapter;
import com.tish.db.connectors.AccPhoConnector;
import com.tish.dialogs.AccountDialog;
import com.tish.interfaces.FragmentSendAccountDataListener;

import java.util.ArrayList;
import java.util.List;

public class AccountManagerFragment extends Fragment {

    private FragmentSendAccountDataListener sendDeleteResult;

    ListView accountListView;
    FloatingActionButton addAccountButton;
    ArrayList<String> accountList;
    AccPhoConnector accountConnector;
    AccountListAdapter accountAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            sendDeleteResult = (FragmentSendAccountDataListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_manager, container, false);
        accountListView = view.findViewById(R.id.lv_accounts);
        addAccountButton = view.findViewById(R.id.fab_add_account);

        accountConnector = new AccPhoConnector(getContext());
        accountList = accountConnector.getAccounts();
        accountAdapter = new AccountListAdapter(getContext(), accountList, getActivity().getSupportFragmentManager());
        accountListView.setAdapter(accountAdapter);

        accountListView.setLongClickable(true);
        accountListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog deleteDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_delete_account)
                        .setMessage(String.format(getString(R.string.message_delete_account), accountList.get(position)))
                        .setPositiveButton(R.string.button_yes_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int result = accountConnector.deleteAccount(accountList.get(position));
                                if (result > 0) {
                                    sendDeleteResult.onSendData(result, "TAG_AM_FRAGMENT", 'e');
                                } else
                                    Toast.makeText(getContext(), "При видаленні сталась помилка", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
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
        });

        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountDialog addAccountDialog = new AccountDialog(getContext());
                addAccountDialog.show(getActivity().getSupportFragmentManager(), "aad");
            }
        });
        return view;
    }

    void updateAccountList() {
        ArrayList<String> tempList = accountConnector.getAccounts();
        accountList.clear();
        accountList.addAll(tempList);
        accountAdapter.notifyDataSetChanged();
    }
}
