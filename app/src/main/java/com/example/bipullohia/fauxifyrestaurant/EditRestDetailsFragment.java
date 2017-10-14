package com.example.bipullohia.fauxifyrestaurant;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class EditRestDetailsFragment extends Fragment {

    EditText mRestTypeEditText, mRestDelFeeTextView, mRestDelTimeTextView, mRestMinOrderTextView, mRestFreeDelAmountTextView;
    TextView mRestNameTextView;
    Button mSubmitNewDetailsButton;
    String mRestaurantType, mRestaurantDelFee, mRestaurantDelTime, mRestaurantMinOrder, mRestaurantName, mRestaurantFreeDelAmount;
    String mNewRestType, mNewRestDelFee, mNewRestDelTime, mNewRestMinOrder, mNewRestFreeDelAmount;

    public EditRestDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_edit_rest_details, container, false);

        mRestNameTextView = (TextView) rootview.findViewById(R.id.textview_rest_name);
        mRestDelFeeTextView = (EditText) rootview.findViewById(R.id.edittext_deliveryfee_rest);
        mRestDelTimeTextView = (EditText) rootview.findViewById(R.id.edittext_deliverytime_rest);
        mRestMinOrderTextView = (EditText) rootview.findViewById(R.id.edittext_minorder_rest);
        mRestTypeEditText = (EditText) rootview.findViewById(R.id.edittext_restaurant_type);
        mRestFreeDelAmountTextView = (EditText) rootview.findViewById(R.id.edittext_free_deliveryamount_rest);
        mSubmitNewDetailsButton = (Button) rootview.findViewById(R.id.button_submit_editrestaurant);

        updateOldValues();

        mSubmitNewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mNewRestDelFee = mRestDelFeeTextView.getText().toString();
                mNewRestDelTime = mRestDelTimeTextView.getText().toString();
                mNewRestMinOrder = mRestMinOrderTextView.getText().toString();
                mNewRestType = mRestTypeEditText.getText().toString();
                mNewRestFreeDelAmount = mRestFreeDelAmountTextView.getText().toString();

                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                alertbuilder.setMessage("Do you want to update these details?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                submitNewDetails();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(EditRestDetailsFragment.this).attach(EditRestDetailsFragment.this).commit();

                                Toast.makeText(getContext(), "New details updated", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = alertbuilder.create();
                alert.show();
            }
        });

        return rootview;
    }

    private void updateOldValues() {

        new BGTaskUpdateOldValues().execute();
    }

    private class BGTaskUpdateOldValues extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = EditRestDetailsFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);

            json_url = MainActivity.mRequestURL + "Restaurants/" + restId;
            progressDialog = ProgressDialog.show(getContext(), "", "Loading Restaurant details...", false);
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
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();

                JSONObject jo = new JSONObject(resultjson);

                mRestaurantName = jo.getString("Restname");
                mRestaurantDelFee = jo.getString("Deliveryfee");
                mRestaurantDelTime = jo.getString("Deliversin");
                mRestaurantType = jo.getString("Resttype");
                mRestaurantMinOrder = jo.getString("Minorder");
                mRestaurantFreeDelAmount = jo.getString("freeDeliveryAmount");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            mRestMinOrderTextView.setText(mRestaurantMinOrder);
            mRestDelTimeTextView.setText(mRestaurantDelTime.substring(0, 2));
            mRestNameTextView.setText(mRestaurantName);
            mRestTypeEditText.setText(mRestaurantType);
            mRestDelFeeTextView.setText(mRestaurantDelFee);
            mRestFreeDelAmountTextView.setText(mRestaurantFreeDelAmount);

            progressDialog.dismiss();
        }
    }

    public void submitNewDetails() {

        new BGTaskSubmitNewDetails().execute();
        Log.i("status", mNewRestDelFee + mNewRestType + mNewRestDelTime + mNewRestMinOrder + mNewRestFreeDelAmount);
    }

    private class BGTaskSubmitNewDetails extends AsyncTask<Void, Void, String> {

        String json_url, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = EditRestDetailsFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.mRequestURL + "Restaurants/" + userId + "?access_token=" + userToken;
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

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("Resttype", mNewRestType);
                jsonObject.put("Deliversin", mNewRestDelTime + " minutes");

                Log.i("del time", mNewRestDelTime + " minutes");
                jsonObject.put("Minorder", mNewRestMinOrder);
                jsonObject.put("Deliveryfee", mNewRestDelFee);
                jsonObject.put("freeDeliveryAmount", mNewRestFreeDelAmount);

                String json = jsonObject.toString();

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

                Log.i("test-submitNewDetails", json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
