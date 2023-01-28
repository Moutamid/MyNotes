package com.moutamid.mynotes.models;

public class WeatherDataModel {
    public String day;
    public float degree;

    public WeatherDataModel(String day, float degree) {
        this.day = day;
        this.degree = degree;
    }

    public WeatherDataModel() {
    }
}
