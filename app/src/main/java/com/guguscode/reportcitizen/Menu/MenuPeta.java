package com.guguscode.reportcitizen.Menu;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guguscode.reportcitizen.Controller.AppController;
import com.guguscode.reportcitizen.Model.Memory;
import com.guguscode.reportcitizen.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MenuPeta extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mGoogleMap;
    UiSettings mUiSettings;
    SearchView searchView;

    MarkerOptions markerOptions = new MarkerOptions();
    LatLng latLng;

    public static final String TITLE = "nama_tempat";
    public static final String JK = "sub_kategori";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String TGL= "waktu_lapor";
    private static String url = "http://aplikasi-tesis.info/tesis/markers.php";

    String tag_json_obj = "json_obj_req";

    Geocoder geocoder;
    List<Address> addresses;

    ArrayList<Memory> memories;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Pemetaan Kerusakan"); //title action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Menampilkan Peta", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_menu_peta);

            //cari lokasi lain
            searchView = findViewById(R.id.sv_location);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
                    addresses = null;

                    if (location != null || !location.equals("")){
                        geocoder = new Geocoder(MenuPeta.this);
                        try {
                            addresses = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address address = addresses.get(0);
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mGoogleMap.addMarker(markerOptions);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });

            initMap();
        } else {
            //no google maps layout
        }
    }


    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Tidak dapat terhubung dengan layanan google", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mUiSettings = mGoogleMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter(){ //menampilkan info window

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView tvjk = v.findViewById(R.id.iw_jk);
                TextView tvAdd = v.findViewById(R.id.iw_add);
                TextView tvDate = v.findViewById(R.id.iw_date);

                //LatLng ll = marker.getPosition();
                //konversi alamat
                //geocoder = new Geocoder(MenuPeta.this, Locale.getDefault());
                // try {
                // addresses = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                //String address = addresses.get(0).getAddressLine(0);

                //tvTempat.setText("Nama tempat : "+marker.getTitle());
                //tvLatLng.setText("Lat: "+ll.latitude+"\nLng: "+ll.longitude);
                //tvAdd.setText("Alamat : "+address);

                // } catch (IOException e) {
                //e.printStackTrace();
                //}
                //custom infowindow mengguna model data Memory
                Memory mMemory = (Memory) marker.getTag();
                tvAdd.setText("Nama tempat : "+mMemory.getNt());
                tvjk.setText("Jenis Infrastruktur : "+mMemory.getJk());
                tvDate.setText("Waktu lapor : "+mMemory.getTgl());
                return v;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);

            getMarkers();
        }
    }

    private void getMarkers() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    memories = new ArrayList<>();
                    String getObject = jObj.getString("infrastruktur");
                    JSONArray jsonArray = new JSONArray(getObject);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        Memory memory = new Memory();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        memory.setNt(jsonObject.getString(TITLE));
                        memory.setJk(jsonObject.getString(JK));
                        memory.setLatLng(new LatLng(Double.parseDouble(jsonObject.getString(LAT)), Double.parseDouble(jsonObject.getString(LNG))));
                        memory.setTgl(jsonObject.getString(TGL));

                        // Menambah data marker custom untuk di tampilkan ke google map
                        memories.add(memory);

                        markerOptions.position(memory.getLatLng());
                        markerOptions.title(memory.getNt());
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
                        Marker m = mGoogleMap.addMarker(markerOptions);

                        //custom infowindow
                        m.setTag(memory);

                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                marker.hideInfoWindow();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
                Toast.makeText(MenuPeta.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNone:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeTerrain:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeSatellite:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeHybrid:
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    LocationRequest mLocationRequest;
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Toast.makeText(this, "Tidak dapat menampilkan lokasi terkini", Toast.LENGTH_LONG).show();
        }else {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mGoogleMap.animateCamera(update);
        }
    }

}
