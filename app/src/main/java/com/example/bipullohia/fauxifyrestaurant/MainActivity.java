package com.example.bipullohia.fauxifyrestaurant;

import android.content.Intent;
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

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar myToolbar;
    static TextView restStatusTextview;
    static String requestURL;
    static String RestStatus;
    FragmentTransaction fragmentTransaction;
    TextView restTypeNavHeader, restNameNavHeader;
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


    // 192.168.0.110 ip for use in mobile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        restStatusTextview = (TextView) findViewById(R.id.RestStatusTextview);

        requestURL = "http://192.168.0.103:3000/api/";

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        restNameNavHeader = (TextView) header.findViewById(R.id.restNameNavHeader);
        restTypeNavHeader = (TextView) header.findViewById(R.id.restTypeNavHeader);

        // here will be the code for Restaurant Name and type for Navigation Header

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
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new PendingOrdersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Pending Orders");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_deliveredorders:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new DeliveredOrdersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Delivered Orders");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();

                        break;

                    case R.id.nav_option_dishesoffered:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new DishesOfferedFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Dishes Offered");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_restaurantstatus:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new RestaurantStatusFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Restaurant Status");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_edit_restDetails:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new EditRestDetailsFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Edit Restarant Details");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;


                    case R.id.nav_option_logout:

                        Intent intent = new Intent(getApplicationContext(), PasscodeScreen.class);
                        startActivity(intent);

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
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    public void checkRestaurantStatus() {

        new bgroundtask().execute();

    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONObject jobject;
        String restName, restType;

        @Override
        protected void onPreExecute() {

            json_url = MainActivity.requestURL + "Restaurants/" + PasscodeScreen.resId;

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
                Log.e("result456", resultjson);

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

                restNameNavHeader.setText(restName);
                restTypeNavHeader.setText(restType);

                if (RestStatus.equals("open")) {
                    restStatusTextview.setText("Your Restaurant is Active");
                    restStatusTextview.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                } else {
                    restStatusTextview.setText("Your Restaurant is Inactive");
                    restStatusTextview.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

    }

}

