package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Bipul Lohia on 9/27/2016.
 */

public class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.MyViewHolder> {

    public static ArrayList<Orders> orderList;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            int position = getAdapterPosition();
            Intent intent;
            intent = new Intent(context, PendingOrderDetails.class);

            Orders orders = orderList.get(position);

            intent.putExtra("orderid", orders.getOrderId());
            intent.putExtra("customername", orders.getCustomername());
            intent.putExtra("ordercustemail", orders.getCustomeremail());
            intent.putExtra("totalitems", orders.getTotalitems());
            intent.putExtra("totalprice", orders.getTotalprice());
            intent.putExtra("ordertime", orders.getOrdertime());
            intent.putExtra("orderconfirmed", orders.getOrderconfirmed());
            intent.putExtra("orderdelivered", orders.getOrderdelivered());
            intent.putExtra("customerorder", orders.getCustomerorder());
            intent.putExtra("totalitemprice", orders.getTotalitemsprice());
            intent.putExtra("customeraddress", orders.getCustomeraddress());
            intent.putExtra("deliveryfee", orders.getDeliveryFee());

            context.startActivity(intent);

        }
    }


    public PendingOrdersAdapter(ArrayList<Orders> orderList) {
        PendingOrdersAdapter.orderList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ordersfragment_rowlayout, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.orderId.setText(orders.getOrderId());
        holder.orderTime.setText(orders.getOrdertime());
        holder.custName.setText(orders.getCustomername());
        holder.totalPrice.setText(orders.getTotalprice());
        holder.totalItems.setText(orders.getTotalitems());
        holder.orderConfirmed.setText(orders.getOrderconfirmed());
        holder.orderDelivered.setText(orders.getOrderdelivered());



        if (orders.getOrderconfirmed().equals("Confirmed")) {
            holder.orderConfirmed.setTextColor(ContextCompat.getColor(holder.context, R.color.green));
        } else {
            holder.orderConfirmed.setTextColor(ContextCompat.getColor(holder.context, R.color.red));
        }

        if (orders.getOrderdelivered().equals("Delivered")) {
            holder.orderDelivered.setTextColor(ContextCompat.getColor(holder.context, R.color.green));
        } else{
            holder.orderDelivered.setTextColor(ContextCompat.getColor(holder.context, R.color.red));
        }

        Log.e("dishesinfo", orders.getOrderId());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

}

