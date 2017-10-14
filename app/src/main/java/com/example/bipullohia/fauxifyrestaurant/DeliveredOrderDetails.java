package com.example.bipullohia.fauxifyrestaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DeliveredOrderDetails extends AppCompatActivity {

    Toolbar mToolbar;
    private ArrayList<CurrentOrder> mDishesList = new ArrayList<>();

    String mCustomerEmail, mDishesData, mOrderConfirmed, mOrderDelivered;
    TextView mCustomerNameTextView, mCustomerAddressTextView, mOrderIdTextView, mTotalPriceTextView,
             mTotalItemPriceTextView, mTotalItemsTextView, mOrderTimeTextView, mDeliveryTimeTextView, mDeliveryFeeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_order_details);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_orderdetails);
        CurrentOrderAdapter currentOrderAdapter = new CurrentOrderAdapter(mDishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(currentOrderAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_deliveredorder_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCustomerNameTextView = (TextView) findViewById(R.id.orderdetails_customername);
        mCustomerAddressTextView = (TextView) findViewById(R.id.orderdetails_deliveryaddress);
        mOrderIdTextView = (TextView) findViewById(R.id.orderdetails_orderid);
        mOrderTimeTextView = (TextView) findViewById(R.id.orderdetails_ordertime);
        mTotalItemPriceTextView = (TextView) findViewById(R.id.orderdetails_totalitemsprice);
        mTotalItemsTextView = (TextView) findViewById(R.id.orderdetails_totalitems);
        mTotalPriceTextView = (TextView) findViewById(R.id.orderdetails_totalprice);
        mDeliveryTimeTextView = (TextView) findViewById(R.id.deliverytime);
        mDeliveryFeeTextView = (TextView) findViewById(R.id.orderdetails_deliveryfee);

        mCustomerNameTextView.setText(getIntent().getStringExtra("customername"));
        mCustomerAddressTextView.setText(getIntent().getStringExtra("customeraddress"));
        mOrderIdTextView.setText(getIntent().getStringExtra("orderid"));
        mOrderTimeTextView.setText(getIntent().getStringExtra("ordertime"));
        mTotalItemPriceTextView.setText(getIntent().getStringExtra("totalitemprice"));
        mTotalPriceTextView.setText(getIntent().getStringExtra("totalprice"));
        mTotalItemsTextView.setText(getIntent().getStringExtra("totalitems"));
        mDeliveryFeeTextView.setText(getIntent().getStringExtra("deliveryfee"));

        mDishesData = getIntent().getStringExtra("customerorder");

        //unused info - can be used in the future
        mCustomerEmail = getIntent().getStringExtra("ordercustemail");
        mOrderConfirmed = getIntent().getStringExtra("orderconfirmed");
        mOrderDelivered = getIntent().getStringExtra("orderdelivered");

        try {
            JSONArray jsonArray = new JSONArray(mDishesData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CurrentOrder currentOrder = new CurrentOrder(jsonObject.getString("dishname"), jsonObject.getString("dishprice"),
                        jsonObject.getInt("dishquantity"));
                mDishesList.add(currentOrder);
            }

            currentOrderAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}