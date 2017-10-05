package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 11/10/2016.
 */

public class ModifyDishIntoCategory extends AppCompatActivity {

    TextView dishname_heading;
    Button button_adddishsubmit;
    RadioButton adddishVeg, adddishNonveg;
    EditText adddish_dishname, adddish_dishprice;
    String category, categoryData, dishnameDefault, dishidDefault, dishpriceDefault;
    ArrayList<String> categoryList;
    int dishType, dishTypeDefault;
    JSONArray jaa;
    JSONObject dishdata, jomenu, jo;
    Toolbar toolbar;

    @Override   //this is to duplicate the effect of back button on UP button of actionbar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish_into_category);

        dishname_heading = (TextView) findViewById(R.id.textView_adddish_heading);
        button_adddishsubmit = (Button) findViewById(R.id.button_submit);
        adddishVeg = (RadioButton) findViewById(R.id.radioBtn_veg);
        adddishNonveg = (RadioButton) findViewById(R.id.radioBtn_nonveg);
        adddish_dishname = (EditText) findViewById(R.id.edittext_dishname);
        adddish_dishprice = (EditText) findViewById(R.id.edittext_dishprice);

        toolbar = (Toolbar) findViewById(R.id.toolbar_adddish);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modify Item");

        categoryList = new ArrayList<>();
        category = getIntent().getStringExtra("Category");
        categoryList = getIntent().getStringArrayListExtra("categoryList");
        categoryData = getIntent().getStringExtra("categoryData");

        dishnameDefault = getIntent().getStringExtra("dishname");
        dishidDefault = getIntent().getStringExtra("dishid");
        dishpriceDefault = getIntent().getStringExtra("dishprice");
        dishTypeDefault = getIntent().getIntExtra("dishtype", 2);


        dishname_heading.setText("Modify " + "'" + dishnameDefault + "'");

        adddish_dishname.setText(dishnameDefault);
        adddish_dishprice.setText(dishpriceDefault);

        switch (dishTypeDefault) {

            case 0:
                adddishVeg.setChecked(false);
                adddishNonveg.setChecked(true);
                break;

            case 1:
                adddishVeg.setChecked(true);
                adddishNonveg.setChecked(false);
                break;

            default:
                adddishVeg.setChecked(false);
                adddishNonveg.setChecked(false);
                break;
        }


        Log.e("category", category);
        Log.e("data", categoryList.toString());
        Log.e("categorydata", categoryData);

        button_adddishsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dishname = adddish_dishname.getText().toString();
                String dishprice = adddish_dishprice.getText().toString();

                checkRadioButtonStatus();

                dishdata = new JSONObject();

                try {
                    dishdata.put("dishname", dishname);
                    dishdata.put("dishprice", dishprice);
                    dishdata.put("isveg", dishType);
                    Log.e("asdfg", dishdata.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jaa = new JSONArray(categoryData);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jomenu = new JSONObject();
                jo = new JSONObject();


                for (int i = 0; i < categoryList.size(); i++) {

                    if (!categoryList.get(i).equals(category)) {

                        try {
                            jomenu.accumulate(categoryList.get(i), jaa.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (categoryList.get(i).equals(category)) {

                        try {
                            jo = jaa.getJSONObject(i);

                            jo.remove(dishidDefault);
                            jo.accumulate(dishidDefault, dishdata);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jomenu.accumulate(categoryList.get(i), jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                Log.e("final", jomenu.toString());

                sendDishData();

            }
        });

    }

    private void sendDishData() {

        new bTaskSendDishData().execute();
    }

    private void checkRadioButtonStatus() {

        if (adddishVeg.isChecked()) {
            dishType = 1;
        } else if (adddishNonveg.isChecked()) {
            dishType = 0;
        }
    }


    private class bTaskSendDishData extends AsyncTask<Void, Void, String>

    {

        String json_url, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken ;

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                URL urll = new URL(json_url);
                HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                httpConnection.setRequestMethod("PUT");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setRequestProperty("Content-Type", "application/json");


                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Menu", jomenu);

                String json = jsonObject.toString();

                OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
                out.write(json);
                out.flush();
                out.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = httpConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpConnection.getResponseMessage());
                }


                Log.e("test", json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            DishesOfferedFragment.shouldRefreshOnResume = true;
            Toast.makeText(getApplicationContext(), "Item Modified", Toast.LENGTH_SHORT).show();

            finish();

        }
    }

}
