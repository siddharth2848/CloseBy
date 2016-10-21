package com.malhotra.closeby.helper;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Malhotra G on 10/21/2016.
 */

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    private static final String TAG = "PlacesDisplayTask";
    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    private SQLiteDatabase db = null;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        createOpenDb();
        //googleMap.clear();
        for (int i = 0; i < list.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = list.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            String type = googlePlace.get("type");

            ContentValues contentValues = new ContentValues();
            contentValues.put("Latitude", lat);
            contentValues.put("Longitude", lng);
            contentValues.put("Place_Name", placeName);
            contentValues.put("Vicinity", vicinity);
            contentValues.put("Type", type);
            //db.insert("NEARBY" , null , contentValues);
            Log.e(TAG, type);
                /*LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                googleMap.addMarker(markerOptions);*/
        }
    }

    private void createOpenDb() {
        StringBuilder table_query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        table_query.append("NEARBY");
        table_query.append(" (Latitude double,Longitude double,Place_Name text,Vicinity text,Type text)");
        Log.e("tb", table_query.toString());

        try {
            db = SQLiteDatabase.openOrCreateDatabase("MapData.db", null);
            //db = openOrCreateDatabase("MapData.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            db.execSQL(table_query.toString());
        } catch (Exception e) {
            Log.e("db", e.toString());
        }
    }
}
