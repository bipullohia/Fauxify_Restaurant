package com.example.bipullohia.fauxifyrestaurant;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class DeliveredOrdersFragment extends Fragment {

    private ArrayList<Orders> mOrderList = new ArrayList<>();
    private DeliveredOrderAdapter mDeliveredOrderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_deliveredorders, container, false);

        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_deliveredorder_frag);

        mDeliveredOrderAdapter = new DeliveredOrderAdapter(mOrderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootview.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mDeliveredOrderAdapter);

        prepareOrderData();
        return rootview;
    }

    private void prepareOrderData() {
        new BGTaskPrepareOrderData().execute();
    }

    private class BGTaskPrepareOrderData extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;
        JSONArray jsonArrayAllData;
        JSONObject jobject;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = DeliveredOrdersFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);
            String restToken = sharedPref.getString("restToken", null);

            urlFinal = getString(R.string.request_url) + "restaurants/" + restId + "/fauxorders?access_token=" + restToken;
            Log.e("finalURL", urlFinal);

            progressDialog = ProgressDialog.show(getContext(), "", getString(R.string.loading_delivered_orders), false);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(urlFinal);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();
                Log.e("result", resultjson);

                jsonArrayAllData = new JSONArray(resultjson);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String totalItems, totalItemPrice, orderConfirmed, orderDelivered, dishesInfo, deliveryFee;
            if (jsonArrayAllData != null) {
                Log.e("Jsonobject length", String.valueOf(jsonArrayAllData.length()));
                for (int j = 0; j <= (jsonArrayAllData.length() - 1); j++) {

                    try {
                        jobject = jsonArrayAllData.getJSONObject(j);

                        JSONObject joDelivery;
                        joDelivery = jobject.getJSONObject("delivery");
                        JSONObject joOrderinfo;
                        joOrderinfo = jobject.getJSONObject("orderinfo");

                        totalItems = joOrderinfo.getString("totalitems");
                        totalItemPrice = joOrderinfo.getString("totalitemprice");
                        dishesInfo = joOrderinfo.getString("dishesinfo");
                        deliveryFee = joOrderinfo.getString("deliveryfee");

                        String oconfirmed = joDelivery.getString("orderconfirmed");
                        String odelivered = joDelivery.getString("orderdelivered");

                        if (oconfirmed.equals("1")) {
                            orderConfirmed = "Confirmed";
                        } else {
                            orderConfirmed = "Not Confirmed";
                        }

                        if (odelivered.equals("1")) {
                            orderDelivered = "Delivered";
                        } else {
                            orderDelivered = "Not Delivered";
                        }
                        if (orderDelivered.equals("Delivered") && orderConfirmed.equals("Confirmed")) {
                            Orders orders = new Orders(jobject.getString("orderid"), totalItems,
                                    jobject.getString("ordertotal"), jobject.getString("customername"),
                                    jobject.getString("customeremail"), jobject.getString("ordertiming"), orderConfirmed,
                                    orderDelivered, totalItemPrice, jobject.getString("customeraddress"), dishesInfo, deliveryFee);

                            mOrderList.add(orders);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mDeliveredOrderAdapter.notifyDataSetChanged();

            } else Log.e("Jsonarray length -", "zero");

            progressDialog.dismiss();
        }
    }
}