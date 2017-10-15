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
import java.util.Random;

public class AddDishIntoCategory extends AppCompatActivity {

    TextView mDishNameHeadingTextView;
    Button mSubmitButton;
    RadioButton mVegRadioButton, mNonVegRadioButton;
    EditText mDishNameEditText, mDishPriceEditText;
    String mCategory, mCategoryData;
    ArrayList<String> mCategoryList;
    int mDishTypeInt;
    Boolean mIsVegDataPresent;
    JSONArray mCategoryDataJA;
    JSONObject mNewDishDataJO, mFinalMenuJO, mParticularCategoryJO;
    Toolbar mToolbar;

    @Override   //this is to duplicate the effect of 'back' button on 'up' button of actionbar
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

        mDishNameHeadingTextView = (TextView) findViewById(R.id.textview_adddish_heading);
        mSubmitButton = (Button) findViewById(R.id.button_submit);
        mVegRadioButton = (RadioButton) findViewById(R.id.radiobutton_veg);
        mNonVegRadioButton = (RadioButton) findViewById(R.id.radiobutton_nonveg);
        mDishNameEditText = (EditText) findViewById(R.id.edittext_dishname);
        mDishPriceEditText = (EditText) findViewById(R.id.edittext_dishprice);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_adddish);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display the up button on actionbar
        getSupportActionBar().setTitle("Add Item");

        mCategoryList = new ArrayList<>();
        mCategory = getIntent().getStringExtra("category");
        mCategoryList = getIntent().getStringArrayListExtra("categoryList");
        mCategoryData = getIntent().getStringExtra("categoryData");

        mDishNameHeadingTextView.setText("Add to " + "''" + mCategory + "''");

        Log.i("checking-Category", mCategory);
        Log.i("checking-categorydata", mCategoryData);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dishname = mDishNameEditText.getText().toString();
                String dishprice = mDishPriceEditText.getText().toString();

                checkRadioButtonStatus();

                if (!dishname.matches("") && !dishprice.matches("") && mIsVegDataPresent) {
                    //this condition is there to ensure no empty field is passed

                    String dishnameUpper = dishname.substring(0, 1).toUpperCase() + dishname.substring(1);

                    mNewDishDataJO = new JSONObject();

                    try {
                        mNewDishDataJO.put("dishname", dishnameUpper);
                        mNewDishDataJO.put("dishprice", dishprice);
                        mNewDishDataJO.put("isveg", mDishTypeInt);

                        Log.i("check-new dish data", mNewDishDataJO.toString());

                        mCategoryDataJA = new JSONArray(mCategoryData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mFinalMenuJO = new JSONObject();
                    mParticularCategoryJO = new JSONObject();

                    Toast.makeText(getApplicationContext(), dishnameUpper + " added to " + mCategory, Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < mCategoryList.size(); i++) {

                        if (!mCategoryList.get(i).equals(mCategory)) {

                            try {
                                mFinalMenuJO.accumulate(mCategoryList.get(i), mCategoryDataJA.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (mCategoryList.get(i).equals(mCategory)) {

                            Random r = new Random(); //to give a random id to the newly added dish

                            try {
                                mParticularCategoryJO = mCategoryDataJA.getJSONObject(i);
                                mParticularCategoryJO.accumulate(String.valueOf(r.nextInt(8999) + 1000), mNewDishDataJO);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                mFinalMenuJO.accumulate(mCategoryList.get(i), mParticularCategoryJO);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    //Log.i("final data to be sent", mFinalMenuJO.toString());
                    sendDishData(); // data sent to the server

                } else {
                    //this means no data was entered
                    Toast.makeText(AddDishIntoCategory.this, "Input field is empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendDishData() {

        new BGTaskSendDishData().execute();
    }

    private void checkRadioButtonStatus() {

        if((!mVegRadioButton.isChecked() && !mNonVegRadioButton.isChecked())){
            // to check if any radio button of dishtype is selected or not
            mIsVegDataPresent = false;
        }

        else {
            mIsVegDataPresent = true;
            if (mVegRadioButton.isChecked()) {
                mDishTypeInt = 1;
            } else if (mNonVegRadioButton.isChecked()) {
                mDishTypeInt = 0;
            }
        }

        Log.i("is veg data present - ", String.valueOf(mIsVegDataPresent));
    }


    private class BGTaskSendDishData extends AsyncTask<Void, Void, String>
    {
        String finalURL, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            finalURL = MainActivity.mRequestURL + "Restaurants/" + userId + "?access_token=" + userToken ;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(finalURL);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                httpConnection.setRequestMethod("PUT");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Menu", mFinalMenuJO);

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

                Log.i("test the sent data", json);

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
