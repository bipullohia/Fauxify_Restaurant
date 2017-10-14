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

public class ModifyDishIntoCategory extends AppCompatActivity {

    TextView mDishNameHeadingTextView;
    Button mSubmitButton;
    RadioButton mVegRadioButton, mNonVegRadioButton;
    EditText mDishNameEditText, mDishPriceEditText;
    String mCategory, mCategoryData, mDishNameDefault, mDishIdDefault, mDishPriceDefault;
    ArrayList<String> mCategoryList;
    int mDishTypeInt, mDishTypeDefaultInt;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modify Item");

        mCategoryList = new ArrayList<>();
        mCategory = getIntent().getStringExtra("category");
        mCategoryList = getIntent().getStringArrayListExtra("categoryList");
        mCategoryData = getIntent().getStringExtra("categoryData");

        mDishNameDefault = getIntent().getStringExtra("dishname");
        mDishIdDefault = getIntent().getStringExtra("dishid");
        mDishPriceDefault = getIntent().getStringExtra("dishprice");
        mDishTypeDefaultInt = getIntent().getIntExtra("dishtype", 2);

        mDishNameHeadingTextView.setText("Modify " + "'" + mDishNameDefault + "'");

        mDishNameEditText.setText(mDishNameDefault);
        mDishPriceEditText.setText(mDishPriceDefault);

        switch (mDishTypeDefaultInt) {

            case 0:
                mVegRadioButton.setChecked(false);
                mNonVegRadioButton.setChecked(true);
                break;

            case 1:
                mVegRadioButton.setChecked(true);
                mNonVegRadioButton.setChecked(false);
                break;

            default:
                mVegRadioButton.setChecked(false);
                mNonVegRadioButton.setChecked(false);
                break;
        }

        Log.i("checking-Category", mCategory);
        Log.i("checking-categorydata", mCategoryData);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dishname = mDishNameEditText.getText().toString();
                String dishprice = mDishPriceEditText.getText().toString();

                checkRadioButtonStatus();

                mNewDishDataJO = new JSONObject();

                try {
                    mNewDishDataJO.put("dishname", dishname);
                    mNewDishDataJO.put("dishprice", dishprice);
                    mNewDishDataJO.put("isveg", mDishTypeDefaultInt);
                    Log.e("asdfg", mNewDishDataJO.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    mCategoryDataJA = new JSONArray(mCategoryData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mFinalMenuJO = new JSONObject();
                mParticularCategoryJO = new JSONObject();

                for (int i = 0; i < mCategoryList.size(); i++) {

                    if (!mCategoryList.get(i).equals(mCategory)) {

                        try {
                            mFinalMenuJO.accumulate(mCategoryList.get(i), mCategoryDataJA.get(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (mCategoryList.get(i).equals(mCategory)) {

                        try {
                            mParticularCategoryJO = mCategoryDataJA.getJSONObject(i);
                            mParticularCategoryJO.remove(mDishIdDefault);
                            mParticularCategoryJO.accumulate(mDishIdDefault, mNewDishDataJO);

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
                Log.e("final-menu", mFinalMenuJO.toString());

                sendDishData();
            }
        });
    }

    private void sendDishData() {

        new BGTaskSendDishData().execute();
    }

    private void checkRadioButtonStatus() {

        if (mVegRadioButton.isChecked()) {
            mDishTypeInt = 1;
        } else if (mNonVegRadioButton.isChecked()) {
            mDishTypeInt = 0;
        }
    }

    private class BGTaskSendDishData extends AsyncTask<Void, Void, String>

    {
        String json_url, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.mRequestURL + "Restaurants/" + userId + "?access_token=" + userToken ;
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
