package com.example.fosterw1.finalprojectcalculator;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;

import java.util.logging.Level;
import java.util.logging.Logger;



import org.json.JSONObject;



import static java.text.NumberFormat.getCurrencyInstance;

public class SteelActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Button.OnClickListener {

    /*4 Advanced Concepts:
     * AsyncTask
      * HTTPURLConnection
      * Adapter
      * Parsing JSON*/


    double price;
    JSONObject jsonObjectData = new JSONObject();

    private LinearLayout PageLayout;
    private TextView txtHeader;
    private Spinner spinMaterial;
    private EditText editAmount;
    private TextView displayPrice;
    private Button btnCalculate;
    private String DisplayString;

    private SharedPreferences savedValues;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2015-11-23 23:48:04 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        PageLayout = (LinearLayout)findViewById( R.id.PageLayout );
        txtHeader = (TextView)findViewById( R.id.txtHeader );
        spinMaterial = (Spinner)findViewById( R.id.spinMaterial );
        editAmount = (EditText)findViewById( R.id.editAmount );
        displayPrice = (TextView)findViewById( R.id.displayPrice );
        btnCalculate = (Button)findViewById( R.id.btnCalculate );

        spinMaterial.setOnItemSelectedListener(this);
        btnCalculate.setOnClickListener(this);

        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    //The following code is an example of how to use the deprecated HTTPCient methods that do not work on API 23
    //I changed to HTTPURLConnection in the next method so that my program could work on API 23
    /**public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
                Log.d("StatusCode",String.valueOf(statusCode));
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }*/

    //This serves the same function as previous but with the newer, non-deprecated HTTPURLConnection methods
    //This method downloads the JSON file from the URL that is given as input.
    //Uses an HTTPGet request to ask for permission and then proceeds to load the file into a StringBuilder
        // if the status code is 200 or 201
    public String readJSONFeed(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    Log.d("Status", String.valueOf(status));
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    //This class uses an Asynchronous task to first call the previous class to download the file (doInBackground)
        // and then parse it in order to find the corresponding price to the specific material that is selected.
    //It then calculates the price based on this price and the quantity entered by the user and displays the total as well as the unit price.
    private class GetMetalPriceFromJSON extends AsyncTask
            <String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0], 60000);
        }

        protected void onPostExecute(String result) {
            try {

                int amount= Integer.parseInt(editAmount.getText().toString());
                String file = result.toString();
                String[] separated = file.split(":");
                String[] separated2 = separated[separated.length-1].split(",");
                String[] separated3 = separated2[1].split("]");
                price = Double.parseDouble(separated3[0]);

                double totalPrice = amount * price;

                NumberFormat currency = getCurrencyInstance();
                displayPrice.setText(currency.format(totalPrice) + " at a unit price of " + price + " ea." );


            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }
        }
    }

    //OnCreate finds the views and uses an adapter to load the spinner. In this case, it loads a string array
        //from strings.xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steel_calculator);

        findViews();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.steel_materials, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMaterial.setAdapter(adapter);
    }

    @Override
    public void onPause(){

        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("displayPrice", String.valueOf(displayPrice));
        editor.apply();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        DisplayString = savedValues.getString("displayPrice", String.valueOf(displayPrice));

        displayPrice.setText(DisplayString);

    }

    //This onclick listener matches the selected item of the dropdown list to a a "name" property in each JSON file.
    //Depending on which one it matches with, the URL of that JSON file is then sent to the methods above that download and parse it
    @Override
    public void onClick(View v) {
        if ( v == btnCalculate ) {
            Log.d("json", jsonObjectData.toString());
            if((spinMaterial.getSelectedItem().toString()).equals("St. Steel scrap, US, $ per gross ton")){
                new GetMetalPriceFromJSON().execute(
                        "https://www.quandl.com/api/v1/datasets/WSJ/ST_SCRP.json?api_key=ATJdzsMvokeryKVx_cnz");
            }
            if((spinMaterial.getSelectedItem().toString()).equals("Shredded Scrap, US Midwest")){
                new GetMetalPriceFromJSON().execute(
                        "https://www.quandl.com/api/v1/datasets/WSJ/SH_SCRP.json?api_key=ATJdzsMvokeryKVx_cnz");
            }
            if((spinMaterial.getSelectedItem().toString()).equals("Steel wire rod Price, $/mt")){
                new GetMetalPriceFromJSON().execute(
                        "https://www.quandl.com/api/v1/datasets/WORLDBANK/WLD_STL_JP_WIROD.json?api_key=ATJdzsMvokeryKVx_cnz");
            }
            Log.d("Selected", spinMaterial.getSelectedItem().toString());
        }
    }

    //When an item is selected, it clears the text boxes that are used
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        displayPrice.setText("");
        editAmount.setText("");

    }

    //There will always be something selected
    public void onNothingSelected(AdapterView<?> parent) {
        
    }


}
