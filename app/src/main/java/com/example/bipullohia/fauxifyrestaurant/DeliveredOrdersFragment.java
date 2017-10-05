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


/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveredOrdersFragment extends Fragment {

    private ArrayList<Orders> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeliveredOrderAdapter deliveredOrderAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_deliveredorders, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.deliveredorderfragment_recyclerview);

        deliveredOrderAdapter = new DeliveredOrderAdapter(orderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootview.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(deliveredOrderAdapter);

        prepareOrderData();
        return rootview;
    }

    private void prepareOrderData() {
        new btaskPrepareOrderData().execute();
    }

    class btaskPrepareOrderData extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = DeliveredOrdersFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);
            String restToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.requestURL + "restaurants/" + restId + "/fauxorders?access_token=" + restToken;
            Log.e("json_url", json_url);

            pd = ProgressDialog.show(getContext(), "", "Loading Delivered orders...", false);

        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL urll = new URL(json_url);
                HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

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

                jsonArray = new JSONArray(resultjson);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String totalitems, totalitemprice, orderconfirmed, orderdelivered, dishesinfo, deliveryfee;
            if (jsonArray != null) {
                Log.e("Jsonobject length", String.valueOf(jsonArray.length()));
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {

                    try {
                        jobject = jsonArray.getJSONObject(j);

                        JSONObject joDelivery;
                        joDelivery = jobject.getJSONObject("delivery");
                        JSONObject joOrderinfo;
                        joOrderinfo = jobject.getJSONObject("orderinfo");

                        totalitems = joOrderinfo.getString("totalitems");
                        totalitemprice = joOrderinfo.getString("totalitemprice");
                        dishesinfo = joOrderinfo.getString("dishesinfo");
                        deliveryfee = joOrderinfo.getString("deliveryfee");

                        String oconfirmed = joDelivery.getString("orderconfirmed");
                        String odelivered = joDelivery.getString("orderdelivered");

                        if (oconfirmed.equals("1")) {
                            orderconfirmed = "Confirmed";
                        } else {
                            orderconfirmed = "Not Confirmed";
                        }

                        if (odelivered.equals("1")) {
                            orderdelivered = "Delivered";
                        } else {
                            orderdelivered = "Not Delivered";
                        }
                        if (orderdelivered.equals("Delivered") && orderconfirmed.equals("Confirmed")) {
                            Orders orders = new Orders(jobject.getString("orderid"), totalitems,
                                    jobject.getString("ordertotal"), jobject.getString("customername"),
                                    jobject.getString("customeremail"), jobject.getString("ordertiming"), orderconfirmed,
                                    orderdelivered, totalitemprice, jobject.getString("customeraddress"), dishesinfo, deliveryfee);

                            orderList.add(orders);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                deliveredOrderAdapter.notifyDataSetChanged();

            } else Log.e("Jsonarray length", "is zero");

            pd.dismiss();
        }

    }

}