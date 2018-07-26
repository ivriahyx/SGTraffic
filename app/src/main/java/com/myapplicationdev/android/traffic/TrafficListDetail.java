package com.myapplicationdev.android.traffic;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TrafficListDetail extends AppCompatActivity {
    String formattedDate;
    ImageView iv,ivWeather;
    TextView tvLocationName,tvWeather;
    String imageurl;
    double latitude,longitude;
    HashMap <String, Town> distanceMapping;
    Town currentTown;

    private class Town{
        String name;
        double lat;
        double lon;
        double distance;
        String weather;

        @Override
        public String toString() {
            return "Town{" +
                    "name='" + name + '\'' +
                    ", lat=" + lat +
                    ", lon=" + lon +
                    ", distance=" + distance +
                    ", weather='" + weather + '\'' +
                    '}';
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_list_detail);

        tvLocationName = (TextView)findViewById(R.id.textViewLocationName);
        tvWeather = (TextView)findViewById(R.id.tvWeather);
        iv = (ImageView)findViewById(R.id.imageView);
        ivWeather = (ImageView)findViewById(R.id.ivWeather);

        distanceMapping = new HashMap<>();

        Intent j = getIntent();
        imageurl = j.getStringExtra("imageurl");
        latitude = j.getDoubleExtra("lat",1.03);
        longitude = j.getDoubleExtra("lng",103.1);

        Log.d("LatLng",latitude+","+longitude);
        Picasso.with(this).load(imageurl).into(iv);


//getCurrentTime
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //2018-06-25T12:09:05
        formattedDate = df.format(c);
        Log.d("CurrentTime Weather",""+formattedDate.replace(" ","T"));
        //
        // Code for step 1 start
        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date_time="+formattedDate.replace(" ","T"));
        request.setOnHttpResponseListener(weatherHttpResponseListener);
        request.setMethod("GET");
        request.execute();
        // Code for step 1 end
    }

    public double distance (double lat_a, double lng_a, double lat_b, double lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Double(distance * meterConversion).doubleValue();
    }
    // Code for step 2 start
    private HttpRequest.OnHttpResponseListener weatherHttpResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){

                    // process response here
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArrayArea = jsonObject.getJSONArray("area_metadata");

                        for (int i=0; i<jsonArrayArea.length(); i++){
                            JSONObject jsonObj_area = jsonArrayArea.getJSONObject(i);
                            Log.d("TrafficListDetail","Area_metadata: "+jsonObj_area.toString());

                            String wname = jsonObj_area.getString("name");
                            JSONObject jsonLocation = jsonObj_area.getJSONObject("label_location");
                            String wlatitude = jsonLocation.getString("latitude");
                            String wlongitude = jsonLocation.getString("longitude");
                            Town newTown = new Town();
                            newTown.lat = Double.parseDouble(wlatitude);
                            newTown.lon = Double.parseDouble(wlongitude);
                            newTown.name = wname;
                            distanceMapping.put(wname, newTown);
                        }

                        JSONArray jsonArrayitems = jsonObject.getJSONArray("items");
                        for (int i=0; i<jsonArrayitems.length(); i++){
                            JSONObject jsonObj_items = jsonArrayitems.getJSONObject(0);
                            Log.d("TrafficListDetail",""+jsonObj_items.toString());

                            JSONArray jsonArrayForecast = jsonObj_items.getJSONArray("forecasts");
                            Log.d("TrafficListDetail","Forecasts: "+jsonArrayForecast.toString());

                            for(int j=0; j<jsonArrayForecast.length(); j++){
                                JSONObject jsonObj_forecast = jsonArrayForecast.getJSONObject(j);
                                String areaName = jsonObj_forecast.getString("area");
                                String forecastString = jsonObj_forecast.getString("forecast");
                                Town searchedTown = distanceMapping.get(areaName);
                                if (searchedTown != null){
                                    searchedTown.weather = forecastString;
                                    double calc_dist = distance(latitude, longitude, searchedTown.lat, searchedTown.lon);
                                    searchedTown.distance = calc_dist;
                                    Log.d("Township", searchedTown.toString());
                                }
                            }
                        }
                        //search
                        Town target = null;
                        double dist = 30000;
                        for ( String key : distanceMapping.keySet() ) {
                            Town curTown = distanceMapping.get(key);
                            if (curTown.distance < dist){
                                dist = curTown.distance;
                                target = curTown;
                            }
                        }
                        Log.d("Nearest Township", target.toString());

                        currentTown = target;
                        tvLocationName.setText(target.name);
                        tvWeather.setText(target.weather);
                        Log.d("Weather Now",""+target.weather);
                        if (target.weather.equals("Partly Cloudy (Night)")){
                            ivWeather.setImageResource(R.drawable.partlycloudy_night);
                        }else if (target.weather.equals("Partly Cloudy (Day)")){
                            ivWeather.setImageResource(R.drawable.partlycloudy_day);
                        } else if (target.weather.toString().equals("Showers")) {
                            ivWeather.setImageResource(R.drawable.heavyrain);
                        }else if (target.weather.toString().equals("Cloudy")){
                            ivWeather.setImageResource(R.drawable.cloudy);
                        }else if (target.weather.toString().equals("Light Rain")){
                            ivWeather.setImageResource(R.drawable.sunny_rain);
                        }else if (target.weather.toString().equals("Thundery Showers")){
                            ivWeather.setImageResource(R.drawable.thunder);
                        }else if (target.weather.toString().equals("Heavy Thundery Showers")){
                            ivWeather.setImageResource(R.drawable.thunder);
                        }else if (target.weather.toString().equals("Fair (Day)")){
                            ivWeather.setImageResource(R.drawable.sunny);
                        } else{

                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
    // Code for step 2 end

}
