package com.malhotra.closeby;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.malhotra.closeby.helper.PlacesAutoCompleteAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN-ACTIVITY";
    private static String place = "";
    private static Double latitude = 0.0;
    private static Double longitude = 0.0;
    AutoCompleteTextView places_auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        places_auto = (AutoCompleteTextView) findViewById(R.id.place_auto);
        places_auto.setAdapter(new PlacesAutoCompleteAdapter(getApplicationContext(), R.layout.autocomplete_list_item));

        places_auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                place = (String) parent.getItemAtPosition(position);
                getLatLng(place);
                //Toast.makeText(getApplicationContext(), place + String.valueOf(latitude) + " " + String.valueOf(longitude)
                //     , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatLng(String place) {
        ArrayList<Double> latlng = getLocationFromAddress(MainActivity.this, place);
        latitude = latlng.get(0);
        longitude = latlng.get(1);
        Log.e(TAG, "Lat: " + String.valueOf(latitude));
        Log.e(TAG, "Lng: " + String.valueOf(longitude));
    }

    /*
    * Return Latitude and Longitude of the city
    * */

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
}
