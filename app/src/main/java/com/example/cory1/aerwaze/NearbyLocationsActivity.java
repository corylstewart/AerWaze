package com.example.cory1.aerwaze;

import android.app.ListActivity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NearbyLocationsActivity extends ListActivity {

    List<Map> locationArray = new ArrayList<Map>();

    private static final String LOCATIONS_TAG = "locations";
    private static final String MAX_LONGITUDE_TAG = "max_longitude";
    private static final String MIN_LONGITUDE_TAG = "min_longitude";
    private static final String CENTER_LONGITUDE_TAG = "center_longitude";
    private static final String MAX_LATITUDE_TAG = "max_latitude";
    private static final String MIN_LATITUDE_TAG = "min_latitude";
    private static final String CENTER_LATITUDE_TAG = "center_latitude";
    private static final String DESCRIPTIVE_NAME_TAG = "descriptive_name";
    private static final String CAMERA_ACTIVE_TAG = "camera_active";
    private static final String CAMERA_MESSAGE_TAG = "camera_message";
    private static final String VIDEO_ACTIVE_TAG = "video_active";
    private static final String VIDEO_MESSAGE_TAG = "video_message";
    private static final String LOCATION_KEY_TAG = "key";
    private static final String LOCATION_ACTIVE_TAG = "location_active";

    private static final String TRIANGLES_TAG = "triangles";
    private static final String TRIANGLE_MIN_LATITUDE_TAG = "min_latitude";
    private static final String TRIANGLE_MAX_LATITUDE_TAG = "max_latitude";
    private static final String TRIANGLE_MIN_LONGITUDE_TAG = "min_longitude";
    private static final String TRIANGLE_MAX_LONGITUDE_TAG = "max_longitude";
    private static final String POINT_A_LATITUDE_TAG = "point_a_latitude";
    private static final String POINT_A_LONGITUDE_TAG = "point_a_longitude";
    private static final String POINT_B_LATITUDE_TAG = "point_b_latitude";
    private static final String POINT_B_LONGITUDE_TAG = "point_b_longitude";
    private static final String POINT_C_LATITUDE_TAG = "point_c_latitude";
    private static final String POINT_C_LONGITUDE_TAG = "point_c_longitude";

    private static final String DISTANCE_TAG = "distance";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_within_location);
        new HttpGetTask().execute();
    }

    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {
        public GPSTracker gps = new GPSTracker(NearbyLocationsActivity.this);
        public double latitude = gps.getLatitude();
        public double longitude = gps.getLongitude();
        String complex = "complex";
        private final String URL = "https://newprivlocdemo.appspot.com/radius?search" + complex + "&radius=5&lat=" + latitude + "&lng=" + longitude;
        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

        @Override
        protected List<String> doInBackground(Void... params) {
            Log.v("blah", URL);
            HttpGet request = new HttpGet(URL);
            myJSONResponseHandler responseHandler = new myJSONResponseHandler();
            try {
                return mClient.execute(request, responseHandler);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            //if (null != mClient)
            //    mClient.close();
            Log.v("location", locationArray.toString());
            setListAdapter(new ArrayAdapter<String>(
                    NearbyLocationsActivity.this,
                    R.layout.my_list_view, result));

            ListView lv = getListView();
            lv.setTextFilterEnabled(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //Toast.makeText(getApplicationContext(), locationArray.get(position).toString(), Toast.LENGTH_LONG).show();
                    makeLocationForDisplay(locationArray.get(position));
                }
            });
        }
    }

    private class myJSONResponseHandler implements ResponseHandler<List<String>> {

        @Override
        public List<String> handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            List<String> result = new ArrayList<String>();
            String JSONResponse = new BasicResponseHandler()
                    .handleResponse(response);

            try {

                // Get top-level JSON Object - a Map
                JSONObject responseObject = (JSONObject) new JSONTokener(
                        JSONResponse).nextValue();

                // Extract value of locations key
                JSONObject locations = responseObject
                        .getJSONObject(LOCATIONS_TAG);

                //make an iterator of the keys in location
                Iterator<?> keys = locations.keys();

                //clear all locations from locationArray
                locationArray.clear();



                // Iterate over locations
                while( keys.hasNext()) {
                    String key = (String)keys.next();
                    JSONObject location = locations.getJSONObject(key);
                    //add locations descriptive name to ActivityList
                    //result.add(location.get(DESCRIPTIVE_NAME_TAG).toString());

                    //dictionary start
                    Map locationDict = new HashMap();
                    locationDict.put(MAX_LONGITUDE_TAG, location.get(MAX_LONGITUDE_TAG).toString());
                    locationDict.put(MIN_LONGITUDE_TAG, location.get(MIN_LONGITUDE_TAG).toString());
                    locationDict.put(CENTER_LONGITUDE_TAG, location.get(CENTER_LONGITUDE_TAG).toString());
                    locationDict.put(MAX_LATITUDE_TAG, location.get(MAX_LATITUDE_TAG).toString());
                    locationDict.put(MIN_LATITUDE_TAG, location.get(MIN_LATITUDE_TAG).toString());
                    locationDict.put(CENTER_LATITUDE_TAG, location.get(CENTER_LATITUDE_TAG).toString());
                    locationDict.put(DESCRIPTIVE_NAME_TAG, location.get(DESCRIPTIVE_NAME_TAG).toString());
                    locationDict.put(CAMERA_ACTIVE_TAG, location.get(CAMERA_ACTIVE_TAG).toString());
                    locationDict.put(CAMERA_MESSAGE_TAG, location.get(CAMERA_MESSAGE_TAG).toString());
                    locationDict.put(VIDEO_ACTIVE_TAG, location.get(VIDEO_ACTIVE_TAG).toString());
                    locationDict.put(VIDEO_MESSAGE_TAG, location.get(VIDEO_MESSAGE_TAG).toString());
                    locationDict.put(LOCATION_KEY_TAG, location.get(LOCATION_KEY_TAG).toString());
                    locationDict.put(LOCATION_ACTIVE_TAG, location.get(LOCATION_ACTIVE_TAG).toString());

                    locationArray.add(locationDict);
                    //dictionary end
                    result.add(location.get(DESCRIPTIVE_NAME_TAG).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("locationArray", locationArray.toString());
            return result;
        }
    }

    private double calculate_distance(double latA, double lngA, double latB, double lngB){
        Log.v("coordinates", Double.toString(latA) + " " + Double.toString(lngA) + " " + Double.toString(latB) + " " + Double.toString(lngB));
        double theta = lngA - lngB;
        double dist = Math.sin(deg2rad(latA)) * Math.sin(deg2rad(latB)) + Math.cos(deg2rad(latA)) * Math.cos(deg2rad(latB)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_within_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeLocationForDisplay(Map locationDict){
        ArrayList<String> locationArray = new ArrayList<String>();
        locationArray.add(locationDict.get(DESCRIPTIVE_NAME_TAG).toString());
        locationArray.add(locationDict.get(CAMERA_ACTIVE_TAG).toString());
        locationArray.add(locationDict.get(VIDEO_ACTIVE_TAG).toString());
        locationArray.add(locationDict.get(CAMERA_MESSAGE_TAG).toString());
        locationArray.add(locationDict.get(VIDEO_MESSAGE_TAG).toString());
        locationArray.add(locationDict.get(LOCATION_ACTIVE_TAG).toString());

        Intent intent = new Intent(getApplicationContext(), ShowWithinLocation.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("locationArray", locationArray);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
