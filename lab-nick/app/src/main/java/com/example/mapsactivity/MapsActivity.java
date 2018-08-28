package com.example.a36_maps;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    public static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;

    @BindView(R.id.goToDisneyland) Button goToDisneyland;
    @BindView(R.id.toggleSattelite) Button toggleSattelite;

    private boolean isSattelite;
    private float mLat;
    private float mLong;
    private float mZoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadPreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle SavedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savePreferences();
    }

    @OnClick(R.id.zoomOut)
    public void zoomOut() {
        setZoom(mMap.getCameraPosition().zoom - 1);
    }

    @OnClick(R.id.zoomIn)
    public void zoomIn() {
        setZoom(mMap.getCameraPosition().zoom + 1);
    }

    public void setZoom(float zoom) {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    @OnClick(R.id.goToDisneyland)
    public void gotoSeattle() {
        centerCamera(47.6062, -122.3321);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(6));
    }

    @OnClick(R.id.toggleSattelite)
    public void toggleSattelite() {
        isSattelite = !isSattelite;

        setMapType();
        savePreferences();
    }

    public void savePreferences() {
        SharedPreferences prefs = getSharedPreferences("com.example.moonmayor.mapsapp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSattelite", isSattelite);
        editor.putFloat("lat", (float) mMap.getCameraPosition().target.latitude);
        editor.putFloat("long", (float) mMap.getCameraPosition().target.longitude);
        editor.putFloat("zoom", mMap.getCameraPosition().zoom + 1);
        editor.commit();
    }

    public void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences("com.example.a36_maps", Context.MODE_PRIVATE);

        //dont update til map is ready
        isSattelite = prefs.getBoolean("isSattelite", false);
        mLat = prefs.getFloat("lat", 0f);
        mLong = prefs.getFloat("long", 0f);
        mZoom = prefs.getFloat("zoom", 6f);
    }

    public void setMapType() {
        int mapType = GoogleMap.MAP_TYPE_NORMAL;
        if (isSattelite) {
            mapType = GoogleMap.MAP_TYPE_HYBRID;
        }
        mMap.setMapType(mapType);
    }

    /*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setMapType();
        showLocation();
        centerCamera();
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "yes, granted");
            showLocation();
        } else {
            Log.d("PERMISSION", "not granted");
        }
    }

    public void showLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = mLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            centerCamera(location);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    },
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    public void centerCamera() {
        centerCamera(mLat, mLong);
        setZoom(mZoom);
    }

    public void centerCamera(Location loc) {
        if (loc != null) {
            centerCamera(loc.getLatitude(), loc.getLongitude());
        }
    }

    public void centerCamera(double lat, double longg) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, longg)));
    }

}
