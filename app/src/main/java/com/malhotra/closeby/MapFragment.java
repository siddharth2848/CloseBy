package com.malhotra.closeby;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malhotra G on 10/22/2016.
 */

public class MapFragment extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MAP-FRAGMENT";
    GoogleMap googleMap = null;
    SQLiteDatabase db;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Button btnPump, btnHos;
    LatLng latLng;
    ArrayList<String> drawableType = null;
    String place = null;
    SupportMapFragment mFragment;
    Marker currLocationMarker;
    Drawable drawable;
    Drawable circleDrawable;
    Spinner spinType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        drawableType = new ArrayList<>();
        initList(drawableType);

        List<String> spinList = new ArrayList<>();
        spinList.add("Airport");
        spinList.add("Atm");
        spinList.add("Bar");
        spinList.add("Doctor");
        spinList.add("Petrol Pump");
        spinList.add("Convenience Store");
        spinList.add("Library");
        spinList.add("Lodging");
        spinList.add("Movies");
        spinList.add("Police Station");
        spinList.add("Restaurant");
        spinList.add("Shopping");
        spinList.add("Hospital");

        spinType = (Spinner) findViewById(R.id.spinner);
        //btnPump = (Button) findViewById(R.id.btnPump);
        //btnHos = (Button) findViewById(R.id.btnHos);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinType.setAdapter(dataAdapter);

        //btnPump.setOnClickListener(this);
        //btnHos.setOnClickListener(this);
        spinType.setOnItemSelectedListener(this);

        setUpMapIfNeeded();
    }

    private void initList(ArrayList<String> placesType) {
        placesType.add("airport_71.png");
        placesType.add("bank_dollar_71.png");
        placesType.add("bar_71.png");
        placesType.add("doctor_71.png");
        placesType.add("gas_station_71.png");
        placesType.add("generic_business_71.png");
        placesType.add("library_71.png");
        placesType.add("lodging_71.png");
        placesType.add("movies_71.png");
        placesType.add("police_71.png");
        placesType.add("restaurant_71.png");
        placesType.add("shopping_71.png");

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            /*case R.id.btnHos:
                googleMap.clear();
                place = "hospital";
                drawable = R.drawable.hos;
                setUpMap();
                break;
            case R.id.btnPump:
                googleMap.clear();
                place = "gas_station";
                drawable = R.drawable.pump;
                setUpMap();
                break;*/
        }
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //setUpMap();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    private void setUpMap() {


        //ArrayList<String> data = new ArrayList<>();
        db = openOrCreateDatabase("MapData.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        StringBuilder fetch = new StringBuilder("Select * from NEARBY ");
        fetch.append("WHERE Type = \'");
        fetch.append(place + "\'");
        Log.d(TAG, "tb" + fetch.toString());
        try {

            Cursor cur = db.rawQuery(fetch.toString(), null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                //Log.e("add","marker");
                MarkerOptions markerOptions = new MarkerOptions();
                Double lat = cur.getDouble(0);
                Double lng = cur.getDouble(1);
                String placeName = cur.getString(2);
                String vicinity = cur.getString(3);
                Log.d(TAG, "type" + vicinity);
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                String type = cur.getString(4);
                //if(type.equals("hospital"))
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
                googleMap.addMarker(markerOptions)
                        .setIcon(markerIcon);
                //googleMap.moveCamera();
                cur.moveToNext();
            }
            cur.close();
        } catch (Exception e) {
            Log.d(TAG, "error-" + e.toString());
        }
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.e("lat", String.valueOf(mLastLocation.getLatitude()));
            Log.e("lng", String.valueOf(mLastLocation.getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            //currLocationMarker = googleMap.addMarker(markerOptions);
        }
        //zoom to current position:
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            Log.e("camerapos", e.toString());
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onLocationChanged(Location location) {

        //place marker at current position
        //mGoogleMap.clear();
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        //currLocationMarker = googleMap.addMarker(markerOptions);

        //Toast.makeText(this,"Location Changed",Toast.LENGTH_SHORT).show();

//zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        googleMap.clear();
        if (position == 0) {
            place = "airport";
            circleDrawable = getResources().getDrawable(R.drawable.airport_71);
        } else if (position == 1) {
            place = "atm";
            circleDrawable = getResources().getDrawable(R.drawable.bank_dollar_71);
        } else if (position == 2) {
            place = "bar";
            circleDrawable = getResources().getDrawable(R.drawable.bar_71);
        } else if (position == 3) {
            place = "doctor";
            circleDrawable = getResources().getDrawable(R.drawable.doctor_71);
        } else if (position == 4) {
            place = "gas_station";
            circleDrawable = getResources().getDrawable(R.drawable.gas_station_71);
        } else if (position == 5) {
            place = "convenience_store";
            circleDrawable = getResources().getDrawable(R.drawable.generic_business_71);
        } else if (position == 6) {
            place = "library";
            circleDrawable = getResources().getDrawable(R.drawable.library_71);
        } else if (position == 7) {
            place = "lodging";
            circleDrawable = getResources().getDrawable(R.drawable.lodging_71);
        } else if (position == 8) {
            place = "movies";
            circleDrawable = getResources().getDrawable(R.drawable.movies_71);
        } else if (position == 9) {
            place = "police";
            circleDrawable = getResources().getDrawable(R.drawable.police_71);
        } else if (position == 10) {
            place = "restaurant";
            circleDrawable = getResources().getDrawable(R.drawable.restaurant_71);
        } else if (position == 11) {
            place = "shopping";
            circleDrawable = getResources().getDrawable(R.drawable.shopping_71);
        } else if (position == 12) {
            place = "hospital";
            circleDrawable = getResources().getDrawable(R.drawable.doctor_71);
        }


        setUpMap();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(getApplicationContext(), "Please a place type", Toast.LENGTH_SHORT).show();
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
