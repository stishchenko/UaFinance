package com.tish.adapters;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tish.R;
import com.tish.models.Cost;
import com.tish.models.StatisticsItem;

import java.util.ArrayList;
import java.util.List;

public class MapCostsListAdapter extends ArrayAdapter<Cost> {

    private Context context;
    private List<Cost> costList;

    public MapCostsListAdapter(Context context, List<Cost> list) {
        super(context, R.layout.item_map_list_cost, list);
        this.context = context;
        this.costList = list;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MapCostsViewHolder viewHolder;
        if (view == null) {
            viewHolder = new MapCostsViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_map_list_cost, parent, false);
            viewHolder.imageViewCategoryIcon = view.findViewById(R.id.iv_map_category_icon);
            viewHolder.textViewCategory = view.findViewById(R.id.tv_map_category);
            viewHolder.textViewAmount = view.findViewById(R.id.tv_map_amount);
            viewHolder.textViewMarket = view.findViewById(R.id.tv_map_market);
            viewHolder.textViewDate = view.findViewById(R.id.tv_map_date);
            view.setTag(viewHolder);
        } else {
            viewHolder = (MapCostsViewHolder) view.getTag();
        }
        Cost cost = costList.get(position);
        viewHolder.imageViewCategoryIcon.setImageResource(cost.getCategory().getIconResource());
        ShapeDrawable sd = new ShapeDrawable(new OvalShape());
        sd.setIntrinsicWidth(40);
        sd.setIntrinsicWidth(40);
        sd.getPaint().setColor(context.getResources().getColor(cost.getCategory().getColorResource(), null));
        viewHolder.imageViewCategoryIcon.setBackground(sd);
        viewHolder.textViewCategory.setText(context.getString(cost.getCategoryName()));
        viewHolder.textViewAmount.setText(String.valueOf(cost.getAmount()));
        viewHolder.textViewMarket.setText(cost.getMarketName());
        viewHolder.textViewDate.setText(cost.getDate());
        return view;
    }

    private static class MapCostsViewHolder {
        static ImageView imageViewCategoryIcon;
        static TextView textViewCategory;
        static TextView textViewDate;
        static TextView textViewAmount;
        static TextView textViewMarket;
    }
}
