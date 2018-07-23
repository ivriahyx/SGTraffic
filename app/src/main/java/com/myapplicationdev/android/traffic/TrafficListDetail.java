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

public class TrafficListDetail extends AppCompatActivity {
    String formattedDate;
    ImageView iv;
    TextView tvLocationName,tvWeather;
    String imageurl,latitude,longitude;
    String wname,wlatitude,wlongitude;
    String areaName,forecastString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_list_detail);

        tvLocationName = (TextView)findViewById(R.id.textViewLocationName);
        tvWeather = (TextView)findViewById(R.id.tvWeather);
        iv = (ImageView)findViewById(R.id.imageView);

        Intent j = getIntent();
        imageurl = j.getStringExtra("imageurl");
        latitude = j.getStringExtra("latitude");
        longitude = j.getStringExtra("longitude");

        Picasso.with(this).load(imageurl).into(iv);


//getCurrentTime
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //2018-06-25T12:09:05
        formattedDate = df.format(c);
        Log.d("CurrentTime",""+formattedDate.replace(" ","T"));
        //
        // Code for step 1 start
        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/environment/2-hour-weather-forecast?date_time="+formattedDate.replace(" ","T"));
        request.setOnHttpResponseListener(weatherHttpResponseListener);
        request.setMethod("GET");
        request.execute();
        // Code for step 1 end


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

                            wname = jsonObj_area.getString("name");
                            JSONObject jsonLocation = jsonObj_area.getJSONObject("label_location");
                            wlatitude = jsonLocation.getString("latitude");
                            wlongitude = jsonLocation.getString("longitude");
                        }

                        JSONArray jsonArrayitems = jsonObject.getJSONArray("items");
                        for (int i=0; i<jsonArrayitems.length(); i++){
                            JSONObject jsonObj_items = jsonArrayitems.getJSONObject(0);
                            Log.d("TrafficListDetail",""+jsonObj_items.toString());

                            JSONArray jsonArrayForecast = jsonObj_items.getJSONArray("forecasts");
                            Log.d("TrafficListDetail","Forecasts: "+jsonArrayForecast.toString());

                            for(int j=0; j<jsonArrayForecast.length(); j++){
                                JSONObject jsonObj_forecast = jsonArrayForecast.getJSONObject(j);
                                areaName = jsonObj_forecast.getString("area");
                                forecastString = jsonObj_forecast.getString("forecast");
                            }
                        }
                        Log.d("Line111","area: "+wname+wlatitude+","+wlongitude+" items: "+areaName+forecastString);
                      

/*
                         if((latitude.equals(wlatitude))&&(longitude.equals(wlongitude))){
                                    if(wname.equals(areaName)){
                                        tvLocationName.setText(wname);
                                        tvWeather.setText(forecastString);
                                    }

                          }
*/

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
    // Code for step 2 end

}
