package com.tish.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tish.R;
import com.tish.models.StatisticsItem;

import java.time.YearMonth;
import java.util.List;

public class StatisticsListAdapter extends ArrayAdapter<StatisticsItem> {

    private Context context;
    private List<StatisticsItem> statisticsList;
    private String dateType;
    String dateContent;
    private boolean withYear;

    public StatisticsListAdapter(Context context, List<StatisticsItem> statisticsList, boolean withYear) {
        super(context, R.layout.item_statistics, statisticsList);
        this.context = context;
        this.statisticsList = statisticsList;
        this.dateType = "";
        this.dateContent = "";
        this.withYear = withYear;
    }

    public StatisticsListAdapter(Context context, List<StatisticsItem> statisticsList, String dateType, String dateContent) {
        super(context, R.layout.item_statistics_with_date, statisticsList);
        this.context = context;
        this.statisticsList = statisticsList;
        this.dateType = dateType;
        this.dateContent = dateContent;
        this.withYear = false;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        StatisticsViewHolder viewHolder;
        if (view == null) {
            viewHolder = new StatisticsViewHolder();
            if (dateType.equals("")) {
                view = LayoutInflater.from(context).inflate(R.layout.item_statistics, parent, false);
                viewHolder.textViewType = view.findViewById(R.id.tv_statistics_type);
                viewHolder.textViewAmount = view.findViewById(R.id.tv_statistics_amount);
                viewHolder.textViewPercent = view.findViewById(R.id.tv_statistics_percent);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.item_statistics_with_date, parent, false);
                viewHolder.textViewType = view.findViewById(R.id.tv_statistics_type_d);
                viewHolder.textViewDate = view.findViewById(R.id.tv_statistics_date_d);
                viewHolder.textViewAmount = view.findViewById(R.id.tv_statistics_amount_d);
            }
            view.setTag(viewHolder);
        } else {
            viewHolder = (StatisticsViewHolder) view.getTag();
        }
        StatisticsItem item = statisticsList.get(position);
        if (withYear) {
            viewHolder.textViewType.setText(item.getTypeName());
            viewHolder.textViewAmount.setText(item.getStatisticsDate());
            viewHolder.textViewPercent.setText(String.valueOf(item.getAmount()));
        } else {
            if (dateType.equals("m")) {
                viewHolder.textViewType.setText(item.getTypeName());
                viewHolder.textViewDate.setText(dateContent + ", " + YearMonth.parse(item.getStatisticsDate()).getYear());
                viewHolder.textViewAmount.setText(String.valueOf(item.getAmount()));
            } else if (dateType.equals("s")) {
                viewHolder.textViewType.setText(item.getTypeName());
                viewHolder.textViewDate.setText(dateContent + ", " + item.getStatisticsDate());
                viewHolder.textViewAmount.setText(String.valueOf(item.getAmount()));
            } else {
                viewHolder.textViewType.setText(item.getTypeName());
                viewHolder.textViewAmount.setText(String.valueOf(item.getAmount()));
                viewHolder.textViewPercent.setText(item.getPercent() + "%");
            }
        }
        return view;
    }


    private static class StatisticsViewHolder {
        static TextView textViewType;
        static TextView textViewDate;
        static TextView textViewAmount;
        static TextView textViewPercent;
    }
}
