package com.example.bipullohia.fauxifyrestaurant;

import android.content.DialogInterface;
import android.content.Intent;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 9/28/2016.
 */

public class PendingOrderDetails extends AppCompatActivity {

    Toolbar toolbar;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    private ArrayList<CurrentOrder> dishesList = new ArrayList<>();
    private CurrentOrderAdapter currentOrderAdapter;

    String customeremail, dishesdata, orderconfirmed, orderdelivered, deliverytime;
    Button buttonSetAnotherTime, buttonConfirmDelivery, buttonsubmitDeliveryTime, buttonDeliveryConfirmed;
    private RecyclerView recyclerView;
    CardView cardviewonclickSetDeliveryTime, cardviewShowDeliveryTime, cardviewDeliveryConfirmation;
    TextView customerName, customerAddress, orderId, totalPrice, totalItemPrice, totalItems, orderTime, deliveryTime;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_details);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_orderdetails);

        currentOrderAdapter = new CurrentOrderAdapter(dishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(currentOrderAdapter);


        toolbar = (Toolbar) findViewById(R.id.toolbar_cartactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Details");

        cardviewonclickSetDeliveryTime = (CardView) findViewById(R.id.cardview_confirmdelivery);
        cardviewShowDeliveryTime = (CardView) findViewById(R.id.cardview_showDeliveryTime);
        cardviewDeliveryConfirmation = (CardView) findViewById(R.id.cardview_deliveryConfirmation);

        buttonConfirmDelivery = (Button) findViewById(R.id.button_confirmdelivery);
        buttonsubmitDeliveryTime = (Button) findViewById(R.id.button_submitdeliverytime);
        buttonDeliveryConfirmed = (Button) findViewById(R.id.button_deliveryconfirmed);
        buttonSetAnotherTime = (Button) findViewById(R.id.button_selectanothertime);
        customerName = (TextView) findViewById(R.id.orderdetails_customername);
        customerAddress = (TextView) findViewById(R.id.orderdetails_deliveryaddress);
        orderId = (TextView) findViewById(R.id.orderdetails_orderid);
        orderTime = (TextView) findViewById(R.id.orderdetails_ordertime);
        totalItemPrice = (TextView) findViewById(R.id.orderdetails_totalitemsprice);
        totalItems = (TextView) findViewById(R.id.orderdetails_totalitems);
        totalPrice = (TextView) findViewById(R.id.orderdetails_totalprice);
        deliveryTime = (TextView) findViewById(R.id.deliverytime);

        customerName.setText(getIntent().getStringExtra("customername"));
        customerAddress.setText(getIntent().getStringExtra("customeraddress"));
        orderId.setText(getIntent().getStringExtra("orderid"));
        orderTime.setText(getIntent().getStringExtra("ordertime"));
        totalItemPrice.setText(getIntent().getStringExtra("totalitemprice"));
        totalPrice.setText(getIntent().getStringExtra("totalprice"));
        totalItems.setText(getIntent().getStringExtra("totalitems"));

        customeremail = getIntent().getStringExtra("ordercustemail");
        dishesdata = getIntent().getStringExtra("customerorder");
        orderconfirmed = getIntent().getStringExtra("orderconfirmed");
        orderdelivered = getIntent().getStringExtra("orderdelivered");

        checkDeliveryStatus();

        try {
            JSONArray jsonArray = new JSONArray(dishesdata);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CurrentOrder currentOrder = new CurrentOrder(jsonObject.getString("dishname"), jsonObject.getString("dishprice"),
                        jsonObject.getInt("dishquantity"));
                dishesList.add(currentOrder);

            }

            currentOrderAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.Timing, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    buttonsubmitDeliveryTime.setVisibility(View.GONE);
                } else {
                    buttonsubmitDeliveryTime.setVisibility(View.VISIBLE);
                    String dtime = spinner.getSelectedItem().toString();
                    deliverytime = dtime.substring(0, 2);

                    Log.e("substring", deliverytime);
                }
                Log.e("Position", (String) parent.getItemAtPosition(position));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonsubmitDeliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardviewonclickSetDeliveryTime.setVisibility(View.GONE);
                deliveryTime.setText(deliverytime + " Minutes");
                cardviewShowDeliveryTime.setVisibility(View.VISIBLE);
                buttonConfirmDelivery.setVisibility(View.VISIBLE);
            }
        });

        buttonSetAnotherTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardviewShowDeliveryTime.setVisibility(View.GONE);
                cardviewonclickSetDeliveryTime.setVisibility(View.VISIBLE);
            }
        });

        buttonConfirmDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDeliveryDetails();

            }
        });


        buttonDeliveryConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDelivery();

            }
        });
    }

    private void confirmDelivery() {

        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage("Are you sure the Order is Delivered?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getApplicationContext(), "Order Delivery Confirmed", Toast.LENGTH_SHORT).show();
                                changeDeliveryStatus();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }
                )

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertbuilder.create();
        alert.setTitle("Delivery Confirmation");
        alert.show();

    }

    private void changeDeliveryStatus() {

        new Btask().execute();

    }

    class Btask extends AsyncTask<Void, Void, String> {
        String json_url, finalurl;


        @Override
        protected void onPreExecute() {
            String orderId = getIntent().getStringExtra("orderid");
            json_url = MainActivity.requestURL + "Fauxorders/";
            finalurl = json_url + orderId;
            Log.e("final url", finalurl);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                URL url = new URL(finalurl);
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
                Log.e("udshc", json);
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

                Log.e("test", json);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

    }


    private void checkDeliveryStatus() {

        if (orderconfirmed.equals("Not Confirmed") && orderdelivered.equals("Not Delivered")) {

            cardviewonclickSetDeliveryTime.setVisibility(View.VISIBLE);

        } else if (orderconfirmed.equals("Confirmed") && orderdelivered.equals("Not Delivered")) {

            cardviewonclickSetDeliveryTime.setVisibility(View.GONE);
            cardviewDeliveryConfirmation.setVisibility(View.VISIBLE);
        } else if (orderconfirmed.equals("Confirmed") && orderdelivered.equals("Delivered")) {

            cardviewDeliveryConfirmation.setVisibility(View.GONE);
            cardviewonclickSetDeliveryTime.setVisibility(View.GONE);
        }
    }

    private void sendDeliveryDetails() {
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url, finalurl;


        @Override
        protected void onPreExecute() {
            String orderId = getIntent().getStringExtra("orderid");
            json_url = MainActivity.requestURL + "Fauxorders/";
            finalurl = json_url + orderId;
            Log.e("final url", finalurl);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                URL url = new URL(finalurl);
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
                Log.e("udshc", json);
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

                Log.e("test", json);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }


    }


}
