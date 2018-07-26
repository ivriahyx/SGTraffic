package com.myapplicationdev.android.traffic;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class TrafficArrayAdapter extends ArrayAdapter<TrafficDetail>{
    private Context context;
    private ArrayList<TrafficDetail> trafficDetails;

    private TextView tvTime, tvLocation;
   //private ImageView iv;
   private GoogleMap map;

    public TrafficArrayAdapter(Context context, int resource, ArrayList<TrafficDetail> trafficDetails) {
        super(context, resource, trafficDetails);
        this.trafficDetails=trafficDetails;
        // Store Context object as we would need to use it later
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrafficDetail detail = trafficDetails.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_row, parent, false);
        //map
        /*
        FragmentManager fm = ((MainActivity)context).getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                LatLng poi_sg = new LatLng(1.352083, 103.819836);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(poi_sg,
                        11));

                //setZoom
                UiSettings uiz = map.getUiSettings();
                uiz.setZoomControlsEnabled(true);
            }

        });
         LatLng poi = new LatLng( Double.parseDouble(current.getLatitude()), Double.parseDouble(current.getLongitude()));
                                final Marker cp = map.addMarker(new
                                        MarkerOptions()
                                        .position(poi)
                                        .title(""+current.getId())
                                        .snippet("snippet")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        */
        //


        //Match the UI components with Java variables
        tvTime = (TextView)rowView.findViewById(R.id.tvTime);
        tvLocation = (TextView)rowView.findViewById(R.id.tvLocation);
        //iv = (ImageView)rowView.findViewById(R.id.imageView);

        TrafficDetail current = trafficDetails.get(position);
        tvTime.setText("");
        Log.d("TrafficArray",current.getTime());
        tvLocation.setText("Latitude: "+current.getLatitude()+" , Longitude: "+current.getLongitude());



        //String imageUri = "https://images.data.gov.sg/api/traffic-images/2018/07/4319591b-ec35-4baa-b943-872c63e3ccb9.jpg";

        //Picasso.with(context).load(current.getImageurl()).into(iv);

        return rowView;
    }

}
