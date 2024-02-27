package com.tish.models;

public class StatisticsItem {

    private String typeName;
    private String date;
    private double amount;
    private double percent;

    public StatisticsItem() {
    }

    public StatisticsItem(String typeName, double amount, double percent) {
        this.typeName = typeName;
        this.amount = amount;
        this.percent = percent;
    }

    public StatisticsItem(String typeName, String date, double amount) {
        this.typeName = typeName;
        this.date = date;
        this.amount = amount;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatisticsDate() {
        return date;
    }

    public void setStatisticsDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
