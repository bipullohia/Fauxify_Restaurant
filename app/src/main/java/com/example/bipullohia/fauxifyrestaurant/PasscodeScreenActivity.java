package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class PasscodeScreenActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    private static final String TAG = "PasscodeScreenActivity";

    EditText mRestUsernameEditText, mRestPasswordEditText;
    Button mLoginButton;
    static String mResId;
    String mUsername, mPassword;
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcodescreen);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_passcodescreen);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        SharedPreferences sharedPref;
        sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String restId = sharedPref.getString("restId", null);
        String restToken = sharedPref.getString("restToken", null);

        if (restId != null && restToken != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            Log.i(TAG, "status: skipping login");
        }

        mRestUsernameEditText = (EditText) findViewById(R.id.edittext_rest_username);
        mRestPasswordEditText = (EditText) findViewById(R.id.edittext_rest_password);
        mLoginButton = (Button) findViewById(R.id.button_login);
        LinearLayout linearLayoutPasscodeScreen = (LinearLayout) findViewById(R.id.ll_passcodescreen);
        CardView logincardView = (CardView) findViewById(R.id.cardview_login);
        linearLayoutPasscodeScreen.setOnClickListener(this);
        logincardView.setOnClickListener(this);

        mRestPasswordEditText.setOnKeyListener(this); // to handle keypress on keyboard after typing the password
    }

    @Override  // to let enter button on keyboard directly press the login button
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            checkRestaurantExistence(view);
        }

        return false;
    }

    public void checkRestaurantExistence(View view) {

        mUsername = mRestUsernameEditText.getText().toString();
        mPassword = mRestPasswordEditText.getText().toString();
        mResId = mUsername;
        if (!mUsername.matches("") && !mPassword.matches("")) {
            new BGTaskIfRestExist().execute();
        } else {
            Toast.makeText(this, R.string.empty_input_field, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.ll_passcodescreen || view.getId() == R.id.cardview_login) {
            // this is to let user click anywhere on blank area to remove soft-keyboard from screen

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private class BGTaskIfRestExist extends AsyncTask<Void, Void, String> {

        String json_url;
        String json_checkurl;
        String JSON_STRING;
        String ifRestaurantExists;

        @Override
        protected void onPreExecute() {

            json_url = getString(R.string.request_url) + "restaurants/";
            json_checkurl = json_url + mUsername + "/exists";
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

                JSONObject jobject = new JSONObject(result_checkjson);
                ifRestaurantExists = jobject.getString("exists");

                //Log.d(TAG, "if rest exists (GET)DB: " + ifRestaurantExists);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (ifRestaurantExists.equals("true")) {

                Log.i(TAG, "Restaurant exists in DB: redirecting to main activity");
                LoginRestaurant();

            } else if (ifRestaurantExists.equals("false")) {
                Toast.makeText(getApplicationContext(), R.string.username_doesnt_exist, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void LoginRestaurant() {
        new BGTaskLoginUser().execute();
    }

    private class BGTaskLoginUser extends AsyncTask<Void, Void, String> {

        String jsonUrl = getString(R.string.request_url)+ "restaurants/login"; // undefined url
        boolean exceptioncaught = false;
        boolean issuccess = true;

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("username", mUsername);
                jsonObject.accumulate("password", mPassword);

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
                        sb.append(line).append("\n");
                    }

                    br.close();

                    String ss = String.valueOf(sb);
                    JSONObject jsoDetails = new JSONObject(ss);

                    String token = String.valueOf(jsoDetails.get("id"));
                    String userId = String.valueOf(jsoDetails.get("userId"));

                    // saving essential info
                    SharedPreferences sharedPref;
                    sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("restToken", token);
                    editor.putString("restId", userId);
                    editor.apply();

                    //Log.d(TAG, "details: " + token + "   " + userId);
                    System.out.println("" + sb.toString());

                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                    issuccess = false;
                }

                Log.d(TAG, "test-data: " + json);

            } catch (IOException | JSONException e) {

                exceptioncaught = true;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!exceptioncaught && issuccess) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            } else if (!issuccess) {
                Toast.makeText(PasscodeScreenActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
