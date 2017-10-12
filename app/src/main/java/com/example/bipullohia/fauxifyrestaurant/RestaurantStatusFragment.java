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


/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantStatusFragment extends Fragment {

    public Switch restStatusSwitch;
    String OppositerestStatus, newRestStatus;

    public RestaurantStatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_restaurant_status, container, false);

        restStatusSwitch = (Switch) rootview.findViewById(R.id.restStatusSwitch);


        Log.i("MainActivity restStatus", MainActivity.RestStatus);
        if (MainActivity.RestStatus.equals("open")) {

            restStatusSwitch.setChecked(true);
            OppositerestStatus = "Inactive";

        } else {
            restStatusSwitch.setChecked(false);
            OppositerestStatus = "Active";

        }

        Log.i("oppositeRestStatus", OppositerestStatus);

        restStatusSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                alertbuilder.setMessage("Set Restaurant as " + OppositerestStatus + "?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (OppositerestStatus.equals("Inactive")) {
                                    newRestStatus = "close";
                                } else {
                                    newRestStatus = "open";
                                }

                                Log.i("new RestStatus", newRestStatus);
                                changeRestaurantStatus();

                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (restStatusSwitch.isChecked()) {
                                    restStatusSwitch.setChecked(false);
                                } else {
                                    restStatusSwitch.setChecked(true);
                                }
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = alertbuilder.create();
                alertDialog.setTitle("Change Restaurant Status");
                alertDialog.show();

            }
        });
        return rootview;
    }

    private void changeRestaurantStatus() {

        new backgroundTask().execute();

    }

    private class backgroundTask extends AsyncTask<Void, Void, String> {

        String json_url, userId, userToken;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = RestaurantStatusFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken ;
            pd = ProgressDialog.show(getContext(), "", "Loading Restaurant status...", false);


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

                Log.i("newreststatus async", newRestStatus);
                jsonObject.accumulate("RestaurantStatus", newRestStatus);

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

//            getActivity().recreate();
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.detach(RestaurantStatusFragment.this).attach(RestaurantStatusFragment.this).commit();

            getActivity().finish();
            startActivity(getActivity().getIntent());

            pd.dismiss();
        }

    }
}
