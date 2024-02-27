package com.tish.models;

public class Geolocation {
    private int geoId;
    private double longitude;
    private double latitude;
    private String country;
    private String city;
    private String address;

    public Geolocation() {
    }

    public Geolocation(double longitude, double latitude, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }

    public Geolocation(double longitude, double latitude, String city, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.address = address;
    }

    public Geolocation(double longitude, double latitude, String country, String city, String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public Geolocation(int geoId, double longitude, double latitude, String country, String city, String address) {
        this.geoId = geoId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public int getGeoId() {
        return geoId;
    }

    public void setGeoId(int geoId) {
        this.geoId = geoId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
