package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import static com.example.bipullohia.fauxifyrestaurant.R.id.deliverytime;


public class PendingOrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PendOrderDetailActivity";
    Toolbar mToolbar;
    Spinner mSpinner;
    ArrayAdapter<CharSequence> mArrayAdapter;

    private ArrayList<CurrentOrder> mDishesList = new ArrayList<>();

    String mCustomerEmail, mDishesData, mOrderConfirmed, mOrderDelivered, mDeliveryTime;
    Button mSetAnotherTimeButton, mConfirmDeliveryButton, mSubmitDeliveryTimeButton, mDeliveryConfirmedButton;
    CardView mSetDeliveryTimeCardView, mShowDeliveryTimeCardView, mDeliveryConfirmationCardView;
    TextView mCustomerNameTextView, mCustomerAddressTextView, mOrderIdTextView, mTotalPriceTextView, mTotalItemPriceTextView,
            mTotalItemsTextView, mOrderTimeTextView, mDeliveryTimeTextView, mDeliveryFeeTextView;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_details);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_orderdetails);

        CurrentOrderAdapter currentOrderAdapter = new CurrentOrderAdapter(mDishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(currentOrderAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_pendingorder_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to display up button on actionbar
        getSupportActionBar().setTitle(R.string.order_details);

        mSetDeliveryTimeCardView = (CardView) findViewById(R.id.cardview_confirmdelivery);
        mShowDeliveryTimeCardView = (CardView) findViewById(R.id.cardview_show_deliverytime);
        mDeliveryConfirmationCardView = (CardView) findViewById(R.id.cardview_deliveryConfirmation);

        mConfirmDeliveryButton = (Button) findViewById(R.id.button_confirmdelivery);
        mSubmitDeliveryTimeButton = (Button) findViewById(R.id.button_submit_deliverytime);
        mDeliveryConfirmedButton = (Button) findViewById(R.id.button_deliveryconfirmed);
        mSetAnotherTimeButton = (Button) findViewById(R.id.button_selectanothertime);
        mCustomerNameTextView = (TextView) findViewById(R.id.orderdetails_customername);
        mCustomerAddressTextView = (TextView) findViewById(R.id.orderdetails_deliveryaddress);
        mOrderIdTextView = (TextView) findViewById(R.id.orderdetails_orderid);
        mOrderTimeTextView = (TextView) findViewById(R.id.orderdetails_ordertime);
        mTotalItemPriceTextView = (TextView) findViewById(R.id.orderdetails_totalitemsprice);
        mTotalItemsTextView = (TextView) findViewById(R.id.orderdetails_totalitems);
        mTotalPriceTextView = (TextView) findViewById(R.id.orderdetails_totalprice);
        mDeliveryTimeTextView = (TextView) findViewById(deliverytime);
        mDeliveryFeeTextView = (TextView) findViewById(R.id.orderdetails_deliveryfee);

        mCustomerNameTextView.setText(getIntent().getStringExtra("customername"));
        mCustomerAddressTextView.setText(getIntent().getStringExtra("customeraddress"));
        mOrderIdTextView.setText(getIntent().getStringExtra("orderid"));
        mOrderTimeTextView.setText(getIntent().getStringExtra("ordertime"));
        mTotalItemPriceTextView.setText(getIntent().getStringExtra("totalitemprice"));
        mTotalPriceTextView.setText(getIntent().getStringExtra("totalprice"));
        mTotalItemsTextView.setText(getIntent().getStringExtra("totalitems"));

        mCustomerEmail = getIntent().getStringExtra("ordercustemail");
        mDishesData = getIntent().getStringExtra("customerorder");
        mOrderConfirmed = getIntent().getStringExtra("orderconfirmed");
        mOrderDelivered = getIntent().getStringExtra("orderdelivered");
        mDeliveryFeeTextView.setText(getIntent().getStringExtra("deliveryfee"));

        checkDeliveryStatus();

        try {
            JSONArray jsonArray = new JSONArray(mDishesData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CurrentOrder currentOrder = new CurrentOrder(jsonObject.getString("dishname"), jsonObject.getString("dishprice"),
                        jsonObject.getInt("dishquantity"));
                mDishesList.add(currentOrder);
            }

            currentOrderAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSpinner = (Spinner) findViewById(R.id.spinner_delivery_time);
        mArrayAdapter = ArrayAdapter.createFromResource(this, R.array.Timing, android.R.layout.simple_spinner_item);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mArrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    mSubmitDeliveryTimeButton.setVisibility(View.GONE);
                } else {
                    mSubmitDeliveryTimeButton.setVisibility(View.VISIBLE);
                    String dtime = mSpinner.getSelectedItem().toString();
                    mDeliveryTime = dtime.substring(0, 2);
                }
                Log.d(TAG, "position: " + (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSubmitDeliveryTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetDeliveryTimeCardView.setVisibility(View.GONE);
                mDeliveryTimeTextView.setText(mDeliveryTime + " Minutes");
                mShowDeliveryTimeCardView.setVisibility(View.VISIBLE);
                mConfirmDeliveryButton.setVisibility(View.VISIBLE);
            }
        });

        mSetAnotherTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShowDeliveryTimeCardView.setVisibility(View.GONE);
                mSetDeliveryTimeCardView.setVisibility(View.VISIBLE);
            }
        });

        mConfirmDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendDeliveryDetails();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mDeliveryConfirmedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDelivery();
            }
        });
    }

    private void confirmDelivery() {

        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage(R.string.are_you_sure_order_delivered)
                .setCancelable(true)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), R.string.order_delivery_confirmed, Toast.LENGTH_SHORT).show();
                                changeDeliveryStatus();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                )

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertbuilder.create();
        alert.setTitle(getString(R.string.delivery_confirmation));
        alert.show();
    }

    private void changeDeliveryStatus() {
        new BGTaskChangeDeliveryStatus().execute();
    }

    private class BGTaskChangeDeliveryStatus extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            String orderId = getIntent().getStringExtra("orderid");

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);
            String restToken = sharedPref.getString("restToken", null);

            json_url = getString(R.string.request_url) + "restaurants/" + restId + "/fauxorders/" + orderId + "?access_token=" + restToken;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject joDelivery = new JSONObject();
                joDelivery.put("orderdelivery", 1);
                joDelivery.put("ordertiming", getIntent().getStringExtra("ordertime"));
                joDelivery.put("orderconfirmed", 1);
                joDelivery.put("deliverytime", deliverytime);
                joDelivery.put("orderdelivered", 1);

                JSONObject jo = new JSONObject();
                jo.put("delivery", joDelivery);

                String json = jo.toString();
                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(json);
                out.flush();
                out.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                }

                //Log.d(TAG, "test-data: " + json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void checkDeliveryStatus() {

        if (mOrderConfirmed.equals("Not Confirmed") && mOrderDelivered.equals("Not Delivered")) {
            mSetDeliveryTimeCardView.setVisibility(View.VISIBLE);

        } else if (mOrderConfirmed.equals("Confirmed") && mOrderDelivered.equals("Not Delivered")) {
            mSetDeliveryTimeCardView.setVisibility(View.GONE);
            mDeliveryConfirmationCardView.setVisibility(View.VISIBLE);

        } else if (mOrderConfirmed.equals("Confirmed") && mOrderDelivered.equals("Delivered")) {
            mDeliveryConfirmationCardView.setVisibility(View.GONE);
            mSetDeliveryTimeCardView.setVisibility(View.GONE);
        }
    }

    private void sendDeliveryDetails() {
        new BGTaskSendDeliveryDetails().execute();
    }

    private class BGTaskSendDeliveryDetails extends AsyncTask<Void, Void, String> {

        String json_url;

        @Override
        protected void onPreExecute() {
            String orderId = getIntent().getStringExtra("orderid");

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);
            String restToken = sharedPref.getString("restToken", null);

            json_url = getString(R.string.request_url) + "restaurants/" + restId + "/fauxorders/" + orderId + "?access_token=" + restToken;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject joDelivery = new JSONObject();
                joDelivery.put("orderdelivery", 1);
                joDelivery.put("ordertiming", getIntent().getStringExtra("ordertime"));
                joDelivery.put("orderconfirmed", 1);
                joDelivery.put("deliverytime", deliverytime);
                joDelivery.put("orderdelivered", 0);

                JSONObject jo = new JSONObject();
                jo.put("delivery", joDelivery);

                String json = jo.toString();
                //Log.d(TAG, "test-data2: " + json);
                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(json);
                out.flush();
                out.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }

                    br.close();
                    System.out.println("" + sb.toString());

                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                }

                Log.d(TAG, "test-data3: " + json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
