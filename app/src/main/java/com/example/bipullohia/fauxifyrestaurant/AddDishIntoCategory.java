package com.example.bipullohia.fauxifyrestaurant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Random;

public class AddDishIntoCategory extends AppCompatActivity {

    TextView dishname_heading;
    Button button_adddishsubmit;
    RadioButton adddishVeg, adddishNonveg;
    EditText adddish_dishname, adddish_dishprice;
    String category, categoryData;
    ArrayList<String> categoryList;
    int dishType;
    JSONArray jaa;
    JSONObject dishdata, jomenu, jo;
    Toolbar toolbar;



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
        toolbar.setTitle("Add Item");

        categoryList = new ArrayList<>();
        category = getIntent().getStringExtra("Category");
        categoryList = getIntent().getStringArrayListExtra("categoryList");
        categoryData = getIntent().getStringExtra("categoryData");


        dishname_heading.setText("Add to " + "''" + category + "''");


        Log.e("category", category);

        Log.e("data", categoryList.toString());
        Log.e("categorydata", categoryData);

        button_adddishsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dishname1 = adddish_dishname.getText().toString();

               String dishname = dishname1.substring(0, 1).toUpperCase() + dishname1.substring(1);

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

                Toast.makeText(getApplicationContext(), dishname + " added to " + category, Toast.LENGTH_SHORT).show();

                for (int i = 0; i < categoryList.size(); i++) {

                    if (!categoryList.get(i).equals(category)) {

                        try {
                            jomenu.accumulate(categoryList.get(i), jaa.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (categoryList.get(i).equals(category)) {
                        Random r = new Random();
                        try {

                            jo = jaa.getJSONObject(i);

                            jo.accumulate(String.valueOf(r.nextInt(8999) + 1000), dishdata);


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

        new bbTask().execute();
    }

    private void checkRadioButtonStatus() {

        if (adddishVeg.isChecked()) {
            dishType = 1;
        } else if (adddishNonveg.isChecked()) {
            dishType = 0;
        }
    }


    private class bbTask extends AsyncTask<Void, Void, String>

    {

        String json_url;

        @Override
        protected void onPreExecute() {

            json_url = MainActivity.requestURL + "Restaurants/" + PasscodeScreen.resId;
            Log.e("json_url_", json_url);

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
            finish();

        }
    }

}
