package com.example.kidszoid;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode = "driving";

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            Log.d("mylog", jsonData[0].toString());
            DataParser parser = new DataParser();
            Log.d("mylog", parser.toString());

            // Starts parsing data
            routes = parser.parse(jObject);
            Log.d("mylog", "Executing routes");
            Log.d("mylog", routes.toString());

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        float d_meter = 0;
        int a =0;
        Location distance = new Location("");
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
//                lat = Double.parseDouble(point.get("lat"));
//                lng = Double.parseDouble(point.get("lng"));
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                Location l1 = new Location("");
                l1.setLatitude(position.latitude);
                l1.setLongitude(position.longitude);

                if (a>0){
                d_meter += distance.distanceTo(l1);
                distance.setLatitude(l1.getLatitude());
                distance.setLongitude(l1.getLongitude());}else{
                    distance.setLatitude(l1.getLatitude());
                    distance.setLongitude(l1.getLongitude());
                    a++;
                }


                points.add(position);

            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            if (directionMode.equalsIgnoreCase("walking")) {
                lineOptions.width(5);
                lineOptions.color(Color.MAGENTA);
            } else {
                lineOptions.width(7);
                lineOptions.color(Color.parseColor("#FA7078"));
            }
            Log.d("mylog", "onPostExecute lineoptions decoded" + String.format("%.2f", d_meter * 0.000621));
        }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            //mMap.addPolyline(lineOptions);
            taskCallback.onTaskDone(lineOptions, String.format("%.2f", d_meter * 0.000621));

        } else {
            Log.d("mylog", "without Polylines drawn");
        }
    }
}