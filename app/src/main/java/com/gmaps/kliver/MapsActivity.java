package com.gmaps.kliver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener, View.OnClickListener, View.OnLongClickListener, GoogleMap.OnMarkerDragListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng icesi = new LatLng(3.3417939, -76.5298544);
    private Marker marker_user_pos;
    private Switch swFollowLocation;
    private ArrayList<MarkerOptions> markers;
    private TextView infoTextView;
    private TextView info2TextView;
    private String newName;
    private MarkerOptions nearMarker;
    public static final double CLOSE_DISTANCE = 100;
    public static final int MY_PERMISSION_FINE_LOCATION = 101;
    public static final int MAP_ZOOM = 17;
    private static final long MIN_TIME = 100;
    private static final float MIN_DISTANCE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        markers = new ArrayList<MarkerOptions>();
        ImageButton btnAddMarker = findViewById(R.id.imgBtnAddMarker);
        swFollowLocation = findViewById(R.id.swFollowLocation);
        infoTextView = findViewById(R.id.infoTextView);
        info2TextView = findViewById(R.id.info2TextView);
        swFollowLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        btnAddMarker.setOnClickListener(this);
        btnAddMarker.setOnLongClickListener(this);
        mapFragment.getMapAsync(this);

    }

    private MarkerOptions nearMarkerToUserPosition() {
        MarkerOptions result = null;
        double min_dist = Double.MAX_VALUE;
        for (MarkerOptions m: markers) {
            double dist = distanceBetweenTwoPoints(m.getPosition(),marker_user_pos.getPosition());
            if (min_dist>=dist) {
                min_dist = dist;
                result = m;
            }
        }
        return result;
    }

    private void setTextInfo2(String text) {
        info2TextView.setText(text);
    }

    private void addMarkerUserLocation(double lat, double lng) {
        LatLng coord = new LatLng(lat, lng);
        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coord, mMap.getCameraPosition().zoom);
        if (marker_user_pos != null) marker_user_pos.remove();
        marker_user_pos = mMap.addMarker(new MarkerOptions()
                .position(coord)
                .title("Posición del Usuario")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position))
        );

        if (swFollowLocation.isChecked())
            mMap.animateCamera(myLocation);
    }

    private void updateLocation(Location location) {
        if (location == null) return;
        addMarkerUserLocation(location.getLatitude(), location.getLongitude());
    }

    private void myLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateLocation(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME,MIN_DISTANCE,this);
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions m = new MarkerOptions().position(latLng).title("Nuevo marcador").draggable(false);
            markers.add(m);
            mMap.addMarker(m);
            if (swFollowLocation.isChecked())
                swFollowLocation.setChecked(false);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private String showInput(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newName = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newName = "";
                dialog.cancel();
            }
        });

        builder.show();

        return newName ==null ? "": newName;
    }

    private List<Address> getInfoGeolocate(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        return addresses;
    }

    private double distanceBetweenTwoPoints(LatLng l1, LatLng l2) {
        float[] results = new float[1];
        Location.distanceBetween(l1.latitude, l1.longitude,
                l2.latitude, l2.longitude,
                results);
        return  results[0];
    }

    private void changeNameToMarker(Marker marker, String nuevo, String viejo) {
        for(MarkerOptions m: markers) {
            if (m.getPosition().longitude==marker.getPosition().longitude &&
                    m.getPosition().latitude==marker.getPosition().latitude) {
                m.title(nuevo.equals("") ? viejo:nuevo);
                marker.setTitle(nuevo.equals("") ? viejo:nuevo);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myLocation();
        // Add initial marker at ICESI's University
        mMap.addMarker(new MarkerOptions()
                .position(icesi).title("Universidad ICESI")
                .snippet("Es un centro de educación superior.")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(false)
        );//.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_point)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(icesi,MAP_ZOOM));

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);

        googleMap.setOnMarkerDragListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(false);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Esta aplicación requiere servicio de gps para localización del usuario",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals(marker_user_pos.getTitle())) {
            String address = getInfoGeolocate(marker.getPosition()).get(0).getAddressLine(0);
            infoTextView.setText("Usted esta en: "+marker.getTitle()
                    +" >> "+address
            );
            return false;
        }
        String viejo = marker.getTitle();
        String nuevo = showInput("Nuevo nombre");
        changeNameToMarker(marker,nuevo,viejo);
        DecimalFormat df = new DecimalFormat("#.00");
        double dist = distanceBetweenTwoPoints(marker.getPosition(),marker_user_pos.getPosition());
        setTextInfo2(" <> La ditancia  es: "+df.format(dist)+" metros aproximadamente");
        return false;
    }



    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onClick(View v) {
        //Se agrega marcadores
        LatLng tmp = mMap.getCameraPosition().target;
        MarkerOptions m = new MarkerOptions()
                .position(tmp).title("Nuevo marcador")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true);
        markers.add(m);
        mMap.addMarker(m);
        Toast.makeText(this,"Se añadió marcador en el centro de la pantalla del dispositivo",Toast.LENGTH_LONG).show();
    }



    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);

        nearMarker = nearMarkerToUserPosition();
        if (nearMarker==null)return;
        DecimalFormat df = new DecimalFormat("#.00");
        String dist = df.format(distanceBetweenTwoPoints(
                nearMarker.getPosition(),marker_user_pos.getPosition())).toString();
        if (Double.parseDouble(dist)>CLOSE_DISTANCE) {
            infoTextView.setText("El marcador más cerca es: "+nearMarker.getTitle());
        } else {
            infoTextView.setText("Usted esta muy cerca, esta en: "+nearMarker.getTitle());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onLongClick(View v) {
        myLocation();
        Toast.makeText(this,"Localizado",Toast.LENGTH_LONG).show();
        return true;
    }
}
