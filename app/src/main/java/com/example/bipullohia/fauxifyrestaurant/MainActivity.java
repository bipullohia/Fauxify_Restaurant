package com.example.bipullohia.fauxifyrestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar myToolbar;
    static String requestURL;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

    // 192.168.0.110 ip for use in mobile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new PendingOrdersFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Pending Orders");

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

                    case R.id.nav_option_logout:

                        Intent intent = new Intent(getApplicationContext(), PasscodeScreen.class);
                        startActivity(intent);

                }
                return true;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {

        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

}

