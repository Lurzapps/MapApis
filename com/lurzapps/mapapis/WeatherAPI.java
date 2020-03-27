package com.lurzapps.mapapis;

import com.google.android.gms.maps.model.LatLng;
import com.lurzapps.mapapis.mapapis.Forecast;
import com.lurzapps.mapapis.weathermodel.Tide;
import com.lurzapps.mapapis.weathermodel.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lurzapps on 25.03.2020
 * Copyright Lurzapps (2020)
 * Package: com.lurzapps.mapapis
 */
public class WeatherAPI {

    interface WeatherCallback {
        void onWeatherResult(ArrayList<Weather> forecast);
        void onWeatherError(String msg);
    }

    private String apiKey;
    private WeatherCallback callback;
    private static final String baseURL = "http://api.worldweatheronline.com/premium/v1/marine.ashx?";

    public WeatherAPI(String apiKey0, WeatherCallback callback0) {
        apiKey = apiKey0;
        callback = callback0;
    }

    public void getForecast(String displayName, LatLng latLng) {
        String httpRequest = String.format("%skey=%s&q=%s,%s&fx=yes&format=json&tp=3&tide=yes", baseURL, apiKey, latLng.latitude, latLng.longitude);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpRequest)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (false == response.isSuccessful()) {
                // ... handle failed request
                //negative response
                callback.onWeatherError("Response not successful!");
            }
            assert response.body() != null;
            String responseBody = response.body().string();
            // ... do something with response
            //format the json correctly
            JSONArray weatherForecastList = new JSONObject(responseBody).getJSONObject("data").getJSONArray("weather");
            //if the size is bigger 0
            if(weatherForecastList.length() > 0) {
                //list with results
                ArrayList<Weather> results = new ArrayList<>();
                //process all items in list
                for(int i = 0; i < weatherForecastList.length(); i++) {
                    //get each item at index
                    JSONObject rootObject = weatherForecastList.getJSONObject(i);
                    //create weather object
                    Weather weather = new Weather();
                    //set lon/lat and display name
                    weather.displayName = displayName;
                    weather.location = latLng;
                    //get and set other important values
                    //astronomy list
                    JSONObject astronomy = rootObject.getJSONArray("astronomy").getJSONObject(0);
                    //sun data
                    weather.sunrise = astronomy.getString("sunrise");
                    weather.sunset = astronomy.getString("sunset");
                    //air temperatures
                    weather.maxTempAir = rootObject.getString("maxtempC")+ "°C";
                    weather.minTempAir = rootObject.getString("mintempC")+ "°C";
                    //set the date
                    weather.date = rootObject.getString("date");
                    //loop all tides: get tide list
                    JSONArray tideArray = rootObject.getJSONArray("tides").getJSONObject(0).getJSONArray("tide_data");
                    //for array
                    for(int j = 0; j < tideArray.length(); j++) {
                        //get object
                        JSONObject tideObject = tideArray.getJSONObject(j);
                        //create new tide object
                        Tide tide = new Tide();
                        //set tide values
                        tide.lowTide = tideObject.getString("tide_type").equals("LOW");
                        tide.tideHeightMt = tideObject.getString("tideHeight_mt") + "m";
                        tide.tideTime = tideObject.getString("tideTime");
                        //add to weather tide list
                        weather.tides.add(tide);
                    }
                    //get the weather and water temperatures; and also the wave height
                    //for this, get the hourly forecast object
                    //actually; this reports every 3 hrs
                    JSONArray hourlyArray = rootObject.getJSONArray("hourly");
                    //loop through array
                    for(int j = 0; j < hourlyArray.length(); j++) {
                        //get hourly object
                        JSONObject hourlyObject = hourlyArray.getJSONObject(j);
                        //create new forecast object
                        Forecast fc = new Forecast();
                        //get and set all objects
                        fc.time = String.valueOf(j * 3);
                        fc.airTemp = hourlyObject.getString("tempC") + "°C";
                        fc.windSpeed = hourlyObject.getString("windspeedKmph") + "km/h";
                        fc.windDirCompass = hourlyObject.getString("winddir16Point");
                        fc.weatherDesc = hourlyObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                        fc.rainMM = hourlyObject.getString("precipMM") + "mm";
                        fc.humidity = hourlyObject.getString("humidity") + "%";
                        fc.visibility = hourlyObject.getString("visibility") + "km";
                        fc.cloudCover = hourlyObject.getString("cloudcover") + "%";
                        fc.airTempFeelsLike = hourlyObject.getString("FeelsLikeC") + "°C";
                        fc.sigHeight = hourlyObject.getString("sigHeight_m") + "m";
                        fc.swellHeight = hourlyObject.getString("swellHeight_m") + "m";
                        fc.swellDirCompass = hourlyObject.getString("swellDir16Point");
                        fc.swellPeriod = hourlyObject.getString("swellPeriod_secs") + "secs";
                        fc.waterTemp = hourlyObject.getString("waterTemp_C") + "°C";
                        //add to forecast list
                        weather.forecasts.add(fc);
                    }
                    //add this weather to result list
                    results.add(weather);
                }

                //pass results
                if(callback != null)
                    callback.onWeatherResult(results);
            }
        } catch (Exception e) {
            // ... handle IO exception
            //pass negative result
            callback.onWeatherError(e.getMessage());
            e.printStackTrace();
        }
    }
}
