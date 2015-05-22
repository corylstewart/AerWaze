package com.example.cory1.aerwaze;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by cory1 on 5/14/2015.
 */
public class ShowRegulation extends ListActivity{
    String URL;
    List<Map> regulationsArray = new ArrayList<Map>();
    public GPSTracker gps = new GPSTracker(ShowRegulation.this);

    private static String WHITE_BLACK_TAG = "white_black";
    private static String ALLOW_TAG = "allow";
    private static String LICENSE_TYPE_TAG = "license_type";
    private static String EVERYWHERE_TAG = "everywhere";
    private static String LICENSE_REQUIREMENT_TAG = "license_requirement";
    private static String CITY_NAME_TAG = "city_name";
    private static String INSURANCE_TAG = "insurance";
    private static String ZIP_CODE_TAG = "zip_code";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String userChoice = intent.getStringExtra("selection");

        if (userChoice.equals("zip")) {
            String zipCode;
            zipCode = intent.getStringExtra("zipCode");
            URL = "https://newprivlocdemo.appspot.com/get_regulation?format=json&zipCode="+ zipCode;
        } else if (userChoice.equals("gps")){
            double lat = gps.getLatitude();
            double lng = gps.getLongitude();
            String latLng = Double.toString(lat) + "," + Double.toString(lng);
            Toast.makeText(getApplicationContext(), latLng, Toast.LENGTH_LONG).show();
            URL = "https://newprivlocdemo.appspot.com/get_regulation?format=json&=latLng" + latLng;
        }
        new HttpGetTask().execute();
    }

    private class HttpGetTask extends AsyncTask<Void, Void, List<String>> {
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
            Log.v("location", regulationsArray.toString());
            setListAdapter(new ArrayAdapter<String>(
                    ShowRegulation.this,
                    R.layout.my_list_view, result));

            ListView lv = getListView();
            lv.setTextFilterEnabled(true);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    //Toast.makeText(getApplicationContext(), locationArray.get(position).toString(), Toast.LENGTH_LONG).show();
                    //makeLocationForDisplay(locationArray.get(position));
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
                JSONArray mainObject = new JSONArray(JSONResponse);
                regulationsArray.clear();

                // Iterate over locations
                for (int i = 0; i < mainObject.length(); i++){
                    Map regulationDict = new HashMap();
                    JSONObject thisRegulation = mainObject.getJSONObject(i);
                    regulationDict.put(WHITE_BLACK_TAG, thisRegulation.get(WHITE_BLACK_TAG).toString());
                    regulationDict.put(ALLOW_TAG, thisRegulation.get(ALLOW_TAG).toString());
                    regulationDict.put(LICENSE_TYPE_TAG, thisRegulation.get(LICENSE_TYPE_TAG).toString());
                    regulationDict.put(EVERYWHERE_TAG, thisRegulation.get(EVERYWHERE_TAG).toString());
                    regulationDict.put(LICENSE_REQUIREMENT_TAG, thisRegulation.get(LICENSE_REQUIREMENT_TAG).toString());
                    regulationDict.put(CITY_NAME_TAG, thisRegulation.get(CITY_NAME_TAG).toString());
                    regulationDict.put(INSURANCE_TAG, thisRegulation.get(INSURANCE_TAG).toString());
                    regulationDict.put(ZIP_CODE_TAG, thisRegulation.get(ZIP_CODE_TAG).toString());
                    regulationsArray.add(regulationDict);
                    result.add(thisRegulation.get(CITY_NAME_TAG).toString());

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
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

}
