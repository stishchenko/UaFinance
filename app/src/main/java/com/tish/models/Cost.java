package com.tish.models;

import com.tish.db.bases.Category;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class Cost {
    private int costId;
    private Category category;
    private double amount;
    private String date;
    private String marketName;
    private String accountNumber;
    private Geolocation geo;
    private boolean photoExists = false;
    private String photoAddress;

    public Cost() {
    }

    public Cost(Category category, double amount) {
        this.category = category;
        this.amount = amount;
        this.date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public Cost(Category category, double amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public Cost(Category category, double amount, String date, String marketName) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
    }

    public Cost(int costId, Category category, double amount, String date, String marketName) {
        this.costId = costId;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
    }

    public Cost(Category category, double amount, String date, String marketName, String accountNumber) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
        this.accountNumber = accountNumber;
    }

    public Cost(int costId, Category category, double amount, String date, String marketName, String accountNumber) {
        this.costId = costId;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
        this.accountNumber = accountNumber;
    }

    public Cost(Category category, double amount, String date, String marketName, String accountNumber, Geolocation geo) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
        this.accountNumber = accountNumber;
        this.geo = geo;
    }

    public Cost(Category category, double amount, String date, String marketName, String accountNumber, Geolocation geo, String photoAddress) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
        this.accountNumber = accountNumber;
        this.geo = geo;
        this.photoAddress = photoAddress;
        if (photoAddress != null)
            this.photoExists = true;
    }

    public Cost(int costId, Category category, double amount, String date, String marketName, String accountNumber, Geolocation geo, String photoAddress) {
        this.costId = costId;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.marketName = marketName;
        this.accountNumber = accountNumber;
        this.geo = geo;
        this.photoAddress = photoAddress;
        if (photoAddress != null)
            this.photoExists = true;
    }

    public int getCostId() {
        return costId;
    }

    public void setCostId(int costId) {
        this.costId = costId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCategoryName() {
        return category.getCategoryName();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Geolocation getGeo() {
        return geo;
    }

    public void setGeo(Geolocation geo) {
        this.geo = geo;
    }

    public boolean isPhotoExists() {
        return photoExists;
    }

    public void setPhotoExists(boolean photoExists) {
        this.photoExists = photoExists;
    }

    public String getPhotoAddress() {
        return photoAddress;
    }

    public void setPhotoAddress(String photoAddress) {
        this.photoAddress = photoAddress;
        if (photoAddress != null)
            setPhotoExists(true);
    }

    public static final Comparator<Cost> COST_COMPARATOR = new Comparator<Cost>() {
        @Override
        public int compare(Cost o1, Cost o2) {
            return Double.compare(o1.getAmount(), o2.getAmount());
        }
    };
}
