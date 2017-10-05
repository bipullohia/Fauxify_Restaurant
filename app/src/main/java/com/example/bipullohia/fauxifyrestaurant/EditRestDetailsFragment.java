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


/**
 * A simple {@link Fragment} subclass.
 */
public class EditRestDetailsFragment extends Fragment {


    EditText restType, restDelFee, restDelTime, restMinOrder, restFreeDelAmount;
    TextView restName;
    Button buttonSubmitNewDetails;
    String restaurantType, restaurantDelFee, restaurantDelTime, restaurantMinOrder, restaurantName, restaurantFreeDelAmount;
    String newRestType, newRestDelFee, newRestDelTime, newRestMinOrder, newRestFreeDelAmount;


    public EditRestDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_edit_rest_details, container, false);

        restName = (TextView) rootview.findViewById(R.id.restNameTextview);
        restDelFee = (EditText) rootview.findViewById(R.id.restDelFeeEditText);
        restDelTime = (EditText) rootview.findViewById(R.id.restDelTimeEditText);
        restMinOrder = (EditText) rootview.findViewById(R.id.restMinOrderEditText);
        restType = (EditText) rootview.findViewById(R.id.restTypeEditText);
        restFreeDelAmount = (EditText) rootview.findViewById(R.id.restFreeDelAmountEditText);
        buttonSubmitNewDetails = (Button) rootview.findViewById(R.id.submitButtonEditRest);


        updateOldValues();

        buttonSubmitNewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newRestDelFee = restDelFee.getText().toString();
                newRestDelTime = restDelTime.getText().toString();
                newRestMinOrder = restMinOrder.getText().toString();
                newRestType = restType.getText().toString();
                newRestFreeDelAmount = restFreeDelAmount.getText().toString();

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

        new bgroundtask().execute();
    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = EditRestDetailsFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            String restId = sharedPref.getString("restId", null);

            json_url = MainActivity.requestURL + "Restaurants/" + restId;
            pd = ProgressDialog.show(getContext(), "", "Loading Restaurant details...", false);
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

                restaurantName = jo.getString("Restname");
                restaurantDelFee = jo.getString("Deliveryfee");
                restaurantDelTime = jo.getString("Deliversin");
                restaurantType = jo.getString("Resttype");
                restaurantMinOrder = jo.getString("Minorder");
                restaurantFreeDelAmount = jo.getString("freeDeliveryAmount");


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            restMinOrder.setText(restaurantMinOrder);
            restDelTime.setText(restaurantDelTime.substring(0, 2));
            restName.setText(restaurantName);
            restType.setText(restaurantType);
            restDelFee.setText(restaurantDelFee);
            restFreeDelAmount.setText(restaurantFreeDelAmount);

            pd.dismiss();
        }
    }

    public void submitNewDetails() {

        new backgroundTask().execute();
        Log.i("status", newRestDelFee + newRestType + newRestDelTime + newRestMinOrder + newRestFreeDelAmount);
    }

    class backgroundTask extends AsyncTask<Void, Void, String> {

        String json_url, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = EditRestDetailsFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken;
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

                jsonObject.put("Resttype", newRestType);
                jsonObject.put("Deliversin", newRestDelTime + " minutes");

                Log.i("del time", newRestDelTime + " minutes");
                jsonObject.put("Minorder", newRestMinOrder);
                jsonObject.put("Deliveryfee", newRestDelFee);
                jsonObject.put("freeDeliveryAmount", newRestFreeDelAmount);

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

                Log.e("test", json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
