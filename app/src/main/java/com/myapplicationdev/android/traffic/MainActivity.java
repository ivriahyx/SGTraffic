package com.myapplicationdev.android.traffic;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;

    //private Marker marker;
    private Marker previousMarker = null;

    private ListView listView;
    ArrayList<TrafficDetail> al = new ArrayList<TrafficDetail>();
    TrafficArrayAdapter aa;
    Spinner spinner;

    String formattedDate,image,longitude,latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv);
        aa = new TrafficArrayAdapter(this, R.layout.activity_row, al);
        listView.setAdapter(aa);

        //getCurrentTime
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time Location => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //2018-06-25T12:09:05
        formattedDate = df.format(c);

        //map
        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                }


                LatLng poi_sg = new LatLng(1.352083, 103.819836);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_sg,
                        11));

                //setZoom
                UiSettings uiz = map.getUiSettings();
                uiz.setZoomControlsEnabled(true);


                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (previousMarker != null) {
                            previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        }
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        previousMarker = marker;

                        Intent intent = new Intent(MainActivity.this,TrafficListDetail.class);
                        intent.putExtra("imageurl",marker.getSnippet());
                        LatLng t = marker.getPosition();
                        intent.putExtra("lat",t.latitude);
                        intent.putExtra("lng",t.longitude);

                        startActivity(intent);

                        return true;
                    }
                });


            }

        });
        //

    }



    @Override
    protected void onResume() {
        super.onResume();
        al.clear();
        // Code for step 1 start
        HttpRequest request = new HttpRequest
                ("https://api.data.gov.sg/v1/transport/traffic-images?date_time="+formattedDate.replace(" ","T"));
        Log.d("CurrentTime",""+formattedDate.replace(" ","T"));
        request.setOnHttpResponseListener(mHttpResponseListener);
        request.setMethod("GET");
        request.execute();
        // Code for step 1 end

    }
    // Code for step 2 start
    private HttpRequest.OnHttpResponseListener mHttpResponseListener =
            new HttpRequest.OnHttpResponseListener() {
                @Override
                public void onResponse(String response){

                    // process response here
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        for (int i=0; i<jsonArray.length(); i++){
                            Log.d("MainActivity",""+jsonArray.getJSONObject(i).toString());
                            JSONObject jsonObj = jsonArray.getJSONObject(i);

                            JSONArray camera = jsonObj.getJSONArray("cameras");
                            for (int j=0; j<camera.length(); j++) {
                                JSONObject cameraobj = camera.getJSONObject(j);
                                final int id = cameraobj.getInt("camera_id");

                                String time = cameraobj.getString("timestamp");
                                JSONObject location = cameraobj.getJSONObject("location");
                                 latitude = location.getString("latitude");
                                 longitude = location.getString("longitude");
                                 image = cameraobj.getString("image");

                                TrafficDetail trafficDetail = new TrafficDetail(id, time, latitude, longitude, image);
                                Log.d("MainActivity", "traffic detail item: " + trafficDetail.toString());
                                al.add(trafficDetail);

                                LatLng poi = new LatLng( Double.parseDouble(latitude), Double.parseDouble(longitude));
                                final Marker cp = map.addMarker(new
                                        MarkerOptions()
                                        .position(poi)
                                        .title(""+id)
                                        .snippet(image)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

/*
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        if(marker == true){
                                            Log.d("MainActivity","Marker clicked");
                                            Toast.makeText(MainActivity.this, "Marker clicked", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MainActivity.this,TrafficListDetail.class);
                                            intent.putExtra("imageurl",image);
                                            intent.putExtra("latitude",latitude);
                                            intent.putExtra("longitude",longitude);
                                            startActivity(intent);

                                        }

                                        return true;
                                    }
                                });
         */


                            }
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    aa.notifyDataSetChanged();
                }
            };
    // Code for step 2 end

}
