package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle mToggleActionBarDrawer;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    static TextView mRestStatusTextview;
    static String mRequestURL;
    static String RestStatus;
    FragmentTransaction mFragmentTransaction;
    TextView mRestTypeNavHeader, mRestNameNavHeader;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap 'back' again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mRestStatusTextview = (TextView) findViewById(R.id.textview_rest_status);
        mRequestURL = "http://fauxify.com/api/";

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        mToggleActionBarDrawer = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggleActionBarDrawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        mRestNameNavHeader = (TextView) header.findViewById(R.id.navheader_rest_name);
        mRestTypeNavHeader = (TextView) header.findViewById(R.id.navheader_rest_type);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new PendingOrdersFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle("Pending Orders");
        }

        navigationView = (NavigationView) findViewById(R.id.navigationView);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_option_pendingorders:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.main_container, new PendingOrdersFragment());
                        mFragmentTransaction.commit();
                        getSupportActionBar().setTitle("Pending Orders");
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_deliveredorders:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.main_container, new DeliveredOrdersFragment());
                        mFragmentTransaction.commit();
                        getSupportActionBar().setTitle("Delivered Orders");
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_dishesoffered:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.main_container, new DishesOfferedFragment());
                        mFragmentTransaction.commit();
                        getSupportActionBar().setTitle("Dishes Offered");
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_restaurantstatus:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.main_container, new RestaurantStatusFragment());
                        mFragmentTransaction.commit();
                        getSupportActionBar().setTitle("Restaurant Status");
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_edit_restDetails:
                        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        mFragmentTransaction.replace(R.id.main_container, new EditRestDetailsFragment());
                        mFragmentTransaction.commit();
                        getSupportActionBar().setTitle("Edit Restarant Details");
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_logout:
                        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), PasscodeScreen.class);
                        startActivity(intent);
                        finish();
                }
                return true;
            }
        });

        checkRestaurantStatus();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkRestaurantStatus();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        View view = new View(this);
        view.setPadding(16, 0, 0, 0);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggleActionBarDrawer.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggleActionBarDrawer.syncState();
    }

    public void checkRestaurantStatus() {
        new BGTaskCheckRestStatus().execute();
    }

    private class BGTaskCheckRestStatus extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONObject jobject;
        String restName, restType, userId, userToken;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("restId", null);
            userToken = sharedPref.getString("restToken", null);

            json_url = MainActivity.mRequestURL + "Restaurants/" + userId + "?access_token=" + userToken ;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(json_url);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

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
                Log.e("result-checkRestStatus", resultjson);

                jobject = new JSONObject(resultjson);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                RestStatus = jobject.getString("RestaurantStatus");
                Log.i("new reststatus MA", RestStatus);
                restName = jobject.getString("Restname");
                restType = jobject.getString("Resttype");

                mRestNameNavHeader.setText(restName);
                mRestTypeNavHeader.setText(restType);

                if (RestStatus.equals("open")) {
                    mRestStatusTextview.setText("Restaurant is Active");
                    mRestStatusTextview.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                } else {
                    mRestStatusTextview.setText("Restaurant is Inactive");
                    mRestStatusTextview.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

