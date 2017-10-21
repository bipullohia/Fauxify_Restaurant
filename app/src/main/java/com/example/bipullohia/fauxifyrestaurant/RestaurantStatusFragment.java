package com.example.bipullohia.fauxifyrestaurant;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class RestaurantStatusFragment extends Fragment {

    public Switch mRestStatusSwitch;
    String mOppositeOfRestStatus, mNewRestStatus;

    public RestaurantStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_restaurant_status, container, false);

        mRestStatusSwitch = (Switch) rootview.findViewById(R.id.switch_rest_status);

        Log.i("MainActivity restStatus", MainActivity.RestStatus);
        if (MainActivity.RestStatus.equals("open")) {

            mRestStatusSwitch.setChecked(true);
            mOppositeOfRestStatus = "Inactive";

        } else {
            mRestStatusSwitch.setChecked(false);
            mOppositeOfRestStatus = "Active";
        }

        Log.i("oppositeRestStatus", mOppositeOfRestStatus);

        mRestStatusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                alertbuilder.setMessage("Set Restaurant as " + mOppositeOfRestStatus + "?")
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (mOppositeOfRestStatus.equals("Inactive")) {
                                    mNewRestStatus = "close";
                                } else {
                                    mNewRestStatus = "open";
                                }

                                Log.i("new RestStatus", mNewRestStatus);
                                changeRestaurantStatus();
                            }
                        })

                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (mRestStatusSwitch.isChecked()) {
                                    mRestStatusSwitch.setChecked(false);
                                } else {
                                    mRestStatusSwitch.setChecked(true);
                                }
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = alertbuilder.create();
                alertDialog.setTitle(getString(R.string.change_restaurant_status));
                alertDialog.show();
            }
        });
        return rootview;
    }

    private void changeRestaurantStatus() {

        new BGTaskChangeRestaurantStatus().execute();
    }

    private class BGTaskChangeRestaurantStatus extends AsyncTask<Void, Void, String> {

        String json_url, userId, userToken;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = RestaurantStatusFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = getString(R.string.request_url) + "Restaurants/" + userId + "?access_token=" + userToken ;
            progressDialog = ProgressDialog.show(getContext(), "", "Loading Restaurant status...", false);
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

                Log.i("newreststatus async", mNewRestStatus);
                jsonObject.accumulate("RestaurantStatus", mNewRestStatus);

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

//            Another way to restart the activity
//            getActivity().recreate();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.detach(RestaurantStatusFragment.this).attach(RestaurantStatusFragment.this).commit();

            //this code will restart this activity, thus updating the latest restaurant status
            getActivity().finish();
            startActivity(getActivity().getIntent());
            progressDialog.dismiss();
        }
    }
}
