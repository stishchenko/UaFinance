package com.tish.adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tish.R;
import com.tish.models.Cost;

import java.util.ArrayList;
import java.util.List;

public class CostsExpListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Cost> adapterCostList;

    public CostsExpListAdapter(Context context, ArrayList<Cost> list) {
        this.context = context;
        this.adapterCostList = list;
    }

    public void setList(ArrayList<Cost> list) {
        this.adapterCostList.clear();
        this.adapterCostList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.item_cost, viewGroup, false);
        ImageView imageViewCategoryIcon = view.findViewById(R.id.iv_category_icon);
        TextView textViewCategory = view.findViewById(R.id.tv_category);
        TextView textViewAmount = view.findViewById(R.id.tv_amount);

        Cost cost = adapterCostList.get(groupPosition);

        imageViewCategoryIcon.setImageResource(cost.getCategory().getIconResource());
        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicWidth(30);
        sd.setIntrinsicWidth(30);
        sd.getPaint().setColor(context.getResources().getColor(cost.getCategory().getColorResource(), null));
        imageViewCategoryIcon.setBackground(sd);
        textViewCategory.setText(context.getString(cost.getCategoryName()));
        textViewAmount.setText(String.format("-%s", cost.getAmount()));

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isExpanded,
                             View view, ViewGroup viewGroup) {

        view = LayoutInflater.from(context).inflate(R.layout.item_cost_details, viewGroup, false);
        TextView textViewMarketName = view.findViewById(R.id.tv_market_name);
        TextView textViewDate = view.findViewById(R.id.tv_date);
        TextView textViewGeo = view.findViewById(R.id.tv_geo);
        //ImageView imageButtonPhoto = view.findViewById(R.id.ib_photo);


        Cost cost = adapterCostList.get(groupPosition);

        if (cost.getMarketName() != null)
            textViewMarketName.setText(cost.getMarketName());
        else
            textViewMarketName.setText(R.string.no_place);

        textViewDate.setText(cost.getDate());

        if (cost.getGeo() != null)
            textViewGeo.setText(String.format("%s, %s", cost.getGeo().getAddress(), cost.getGeo().getCity()));

        /*if (cost.isPhotoExists())
            imageButtonPhoto.setImageResource(R.drawable.icon_cheque_is);
        else
            imageButtonPhoto.setImageResource(R.drawable.icon_no_cheque);

        imageButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cost.isPhotoExists()) {
                    GetPhotoDialog showPhotoDialog = new GetPhotoDialog(context, cost.getCostId(), cost.getPhotoAddress());
                    showPhotoDialog.show(((MainActivity) context).getSupportFragmentManager(), "spd");
                } else {
                    GetPhotoDialog addPhotoDialog = new GetPhotoDialog(context, cost.getCostId());
                    addPhotoDialog.show(((MainActivity) context).getSupportFragmentManager(), "apd");
                }
            }
        });*/
        return view;
    }


    @Override
    public int getGroupCount() {
        return adapterCostList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Cost getGroup(int groupPosition) {
        return adapterCostList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return adapterCostList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
