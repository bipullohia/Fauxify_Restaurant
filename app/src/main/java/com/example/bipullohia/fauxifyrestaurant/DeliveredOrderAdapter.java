package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 11/5/2016.
 */

public class DeliveredOrderAdapter extends RecyclerView.Adapter<DeliveredOrderAdapter.MyViewHolder> {

    public static ArrayList<Orders> orderList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "error";
        public TextView orderId, orderTime, custName, totalItems, totalPrice, orderDelivered, orderConfirmed;
        Context context;

        public MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            orderId = (TextView) view.findViewById(R.id.orderrow_orderid);
            orderTime = (TextView) view.findViewById(R.id.orderrow_timestamp);
            custName = (TextView) view.findViewById(R.id.orderrow_custname);
            totalItems = (TextView) view.findViewById(R.id.orderrow_totalitems);
            totalPrice = (TextView) view.findViewById(R.id.orderrow_totalprice);
            orderConfirmed = (TextView) view.findViewById(R.id.orderrow_orderconfirmed);
            orderDelivered = (TextView) view.findViewById(R.id.orderrow_orderdelivered);
        }


        @Override
        public void onClick(View v) {


            int position= getAdapterPosition();
            Intent intent;
            intent = new Intent(context, DeliveredOrderDetails.class);

            Orders orders = orderList.get(position);

            intent.putExtra("orderid",orders.getOrderId());
            intent.putExtra("customername",orders.getCustomername());
            intent.putExtra("ordercustemail", orders.getCustomeremail());
            intent.putExtra("totalitems",orders.getTotalitems());
            intent.putExtra("totalprice", orders.getTotalprice());
            intent.putExtra("ordertime", orders.getOrdertime());
            intent.putExtra("orderconfirmed", orders.getOrderconfirmed());
            intent.putExtra("orderdelivered", orders.getOrderdelivered());
            intent.putExtra("customerorder", orders.getCustomerorder());
            intent.putExtra("totalitemprice", orders.getTotalitemsprice());
            intent.putExtra("customeraddress", orders.getCustomeraddress());

            context.startActivity(intent);

        }
    }


    public DeliveredOrderAdapter(ArrayList<Orders> orderList)
    {
        DeliveredOrderAdapter.orderList = orderList;
    }

    @Override
    public DeliveredOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ordersfragment_rowlayout, parent, false);

        return new DeliveredOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeliveredOrderAdapter.MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.orderId.setText(orders.getOrderId());
        holder.orderTime.setText(orders.getOrdertime());
        holder.custName.setText(orders.getCustomername());
        holder.totalPrice.setText(orders.getTotalprice());
        holder.totalItems.setText(orders.getTotalitems());
        holder.orderConfirmed.setText(orders.getOrderconfirmed());
        holder.orderDelivered.setText(orders.getOrderdelivered());

        Log.e("dishesinfo", orders.getCustomerorder());

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}


