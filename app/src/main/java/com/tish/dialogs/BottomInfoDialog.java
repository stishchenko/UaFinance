package com.tish.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.tish.R;
import com.tish.adapters.MapCostsListAdapter;
import com.tish.db.connectors.CostConnector;
import com.tish.models.Cost;

import java.util.List;

public class BottomInfoDialog extends DialogFragment {

    ListView mapCostsListView;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.map_bottom_sheet, null);

        TextView title = view.findViewById(R.id.tv_map_bottom_title);
        title.setText(args.getString("title"));

        mapCostsListView = view.findViewById(R.id.lv_map_bottom_costs);

        CostConnector costConnector = new CostConnector(getContext());
        List<Cost> costList = costConnector.getCostsByGeoId((Integer) args.getInt("tag"));
        MapCostsListAdapter adapter = new MapCostsListAdapter(getContext(), costList);
        mapCostsListView.setAdapter(adapter);

        builder.setView(view);


        AlertDialog thisDialog = builder.create();
        thisDialog.getWindow().setGravity(Gravity.BOTTOM);
        thisDialog.getWindow().setDimAmount(0);
        thisDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        thisDialog.getWindow().getAttributes().height = (int) (2220 / 2.3);
        return thisDialog;
    }
}
