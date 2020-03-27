package com.lurzapps.mapapis;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lurzapps on 26.03.2020
 * Copyright Lurzapps (2020)
 * Package: com.lurzapps.mapapis
 */
public class OSMApi {

    interface OSMCallback {
        void onOsmResult(ArrayList<OSMResult> results);
        void onOsmError(String msg);
        void onNoOSMResult();
    }

    private OSMCallback callback;
    private static final String baseURL = "https://nominatim.openstreetmap.org/search?";

    public OSMApi(OSMCallback callback0) {
        callback = callback0;
    }

    public void searchPlace(String query) {
        String httpRequest = String.format("%semail=lurzapps@gmail.com&limit=7&format=json&q=%s", baseURL, query);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpRequest)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (false == response.isSuccessful()) {
                // ... handle failed request
                //negative response
                if(callback != null)
                    callback.onOsmError("Response not successful!");
            }
            assert response.body() != null;
            String responseBody = response.body().string();
            // ... do something with response
            //format the json correctly
            JSONArray jsonRootList = new JSONArray(responseBody);
            //get the first element if has one
            if(jsonRootList.length() > 0) {
                //res list
                ArrayList<OSMResult> results = new ArrayList<>();
                //loop and get every single element
                for(int i = 0; i < jsonRootList.length(); i++) {
                    JSONObject rootDataObject = jsonRootList.getJSONObject(i);
                    //get lat and lon
                    String latStr = rootDataObject.getString("lat");
                    String lonStr = rootDataObject.getString("lon");
                    //the display name
                    String displayName = rootDataObject.getString("display_name");
                    //convert to double and insert to lat/lon
                    LatLng result = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
                    //add to result list
                    results.add(new OSMResult(result, displayName));
                }

                if(callback != null) {
                    //give callback
                    callback.onOsmResult(results);
                }
            }

        } catch (IOException | JSONException e) {
            // ... handle IO exception
            //pass negative result
            if(callback != null)
               callback.onOsmError(e.getMessage());
        }
    }
}
