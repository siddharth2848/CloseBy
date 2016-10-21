package com.malhotra.closeby;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.makeramen.roundedimageview.RoundedImageView;
import com.malhotra.closeby.helper.GooglePlacesReadTask;
import com.malhotra.closeby.helper.PlacesAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MAIN-ACTIVITY";
    private static final String GOOGLE_API_KEY = "AIzaSyAsgdWWcZs0iSZ8K6yb7xDqGJ3tBcCwOmE";
    private static String place = "";
    private static Double latitude = 0.0;
    private static Double longitude = 0.0;
    AutoCompleteTextView places_auto;
    GoogleMap googleMap = null;
    EditText placeText;
    Button btn_nearbyData;
    RoundedImageView btn_loc;
    private Location thislocation;
    private boolean valid = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int PROXIMITY_RADIUS = 20000;
    private SQLiteDatabase db = null;

    private static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT > 20) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        places_auto = (AutoCompleteTextView) findViewById(R.id.place_auto);
        btn_loc = (RoundedImageView) findViewById(R.id.btn_currloc);
        btn_nearbyData = (Button) findViewById(R.id.btn_data);
        places_auto.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item));

        places_auto.setOnItemClickListener(this);
        btn_loc.setOnClickListener(this);
        btn_nearbyData.setOnClickListener(this);
    }

    /*
    * Return Latitude and Longitude of the city
    * */

    private void getLatLng(String place) {
        ArrayList<Double> latlng = getLocationFromAddress(MainActivity.this, place);
        latitude = latlng.get(0);
        longitude = latlng.get(1);
        Log.d(TAG, "Lat: " + String.valueOf(latitude));
        Log.d(TAG, "Lng: " + String.valueOf(longitude));
    }

    public ArrayList<Double> getLocationFromAddress(Context context, String strAddress) {

        ArrayList<Double> latlng = new ArrayList<>();

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Toast.makeText(MainActivity.this, "after if", Toast.LENGTH_SHORT).show();

                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latlng.add(location.getLatitude());
            latlng.add(location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latlng;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_currloc:
                boolean res = isLocationEnabled(this);
                if (res) {
                    try {
                        getLocationFromGPS();
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                } else {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                }
                break;
            case R.id.btn_data:
                getData(latitude, longitude);
        }

    }

    private void getLocationFromGPS() {
        locationListener = new LocationListener() {

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }

            public void onLocationChanged(Location location) {
                gpsLocationReceived(location);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                    longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
                }

            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria locationCritera = new Criteria();
        locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCritera.setAltitudeRequired(false);
        locationCritera.setBearingRequired(false);
        locationCritera.setCostAllowed(true);
        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
        String providerName = locationManager.getBestProvider(locationCritera, true);

        if (providerName != null && locationManager.isProviderEnabled(providerName)) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            locationManager.requestLocationUpdates(providerName, 20000, 100, locationListener);
        } else {
            // Provider not enabled, prompt user to enable it
            Toast.makeText(getApplicationContext(), "please_turn_on_gps", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }

        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {

            latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
            longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();


        } else if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) != null) {
            Log.d(TAG, "Inside NETWORK");

            latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
            longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();

        } else {

            Log.d(TAG, "else +++++++ ");
            latitude = -1.0;
            longitude = -1.0;
        }
        btn_nearbyData.setVisibility(View.VISIBLE);
    }

    private void getData(Double latitude, Double longitude) {
        String[] TYPES = {"ATM", "HOSPITAL", "HOTEL"};
        for (String TYPE : TYPES) getData(latitude, longitude, TYPE);
    }

    private void getData(Double latitude, Double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + placeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
        Log.d(TAG, "url" + googlePlacesUrl.toString());
        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);
    }

    protected void gpsLocationReceived(Location location) {
        thislocation = location;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Get data associated with the specified position
        // in the list (AdapterView)
        place = (String) parent.getItemAtPosition(position);
        getLatLng(place);
        btn_nearbyData.setVisibility(View.VISIBLE);
        //Toast.makeText(getApplicationContext(), place + String.valueOf(latitude) + " " + String.valueOf(longitude)
        //     , Toast.LENGTH_SHORT).show();

    }
}
