package com.lurzapps.mapapis.weathermodel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Lurzapps on 26.03.2020
 * Copyright Lurzapps (2020)
 * Package: com.lurzapps.mapapis
 */
public class Weather {
    public String displayName;
    public LatLng location;
    public String date;
    public String sunrise, sunset;
    public String minTempAir, maxTempAir;

    public ArrayList<Tide> tides = new ArrayList<>();
    public ArrayList<Forecast> forecasts = new ArrayList<>();
}
