package com.example.bipullohia.fauxifyrestaurant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bipul Lohia on 9/27/2016.
 */

public class PasscodeScreen extends AppCompatActivity {

    EditText restaurantId;
    Button enterButton;
    static String resId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcodescreen);

        restaurantId = (EditText) findViewById(R.id.restaurantid);
        enterButton = (Button) findViewById(R.id.enterbutton);

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resId = restaurantId.getText().toString();
                Log.e("respascde44", resId);

                checkRestaurantExistence();
        }});

    }

    private void checkRestaurantExistence(){
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String json_url;
        String json_checkurl;
        String JSON_STRING;
        String ifRestaurantExists;

        @Override
        protected void onPreExecute() {

            json_url = "http://192.168.0.103:3000/api/Restaurants/";
            json_checkurl = json_url + resId + "/exists";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                URL urll = new URL(json_checkurl);
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
                String result_checkjson = stringBuilder.toString().trim();
                Log.e("result", result_checkjson);

                JSONObject jobject = new JSONObject(result_checkjson);
                ifRestaurantExists = jobject.getString("exists");

                Log.e("if exists", ifRestaurantExists);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (ifRestaurantExists.equals("true")) {

                Log.e("checkRestexistence", "Rest exists: redirecting to main activity");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            } else if (ifRestaurantExists.equals("false")) {

                Toast.makeText(getApplicationContext(), "Wrong Restaurant Id", Toast.LENGTH_LONG).show();

            }

        }
    }

}
