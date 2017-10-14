package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class PendingOrdersAdapter extends RecyclerView.Adapter<PendingOrdersAdapter.MyViewHolder> {

    private static ArrayList<Orders> orderList;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mOrderIdTextView, mCustomerNameTextView, mOrderTimeTextView, mTotalPriceTextView,
                 mOrderDeliveredTextView, mOrderConfirmedTextView;
        Context context;
        ImageView mRedConfirmImageView, mGreenConfirmImageView, mRedDeliverTextView, mGreenDeliverTextView;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            mOrderIdTextView = (TextView) view.findViewById(R.id.orderrow_orderid);
            mOrderTimeTextView = (TextView) view.findViewById(R.id.orderrow_ordertime);
            mCustomerNameTextView = (TextView) view.findViewById(R.id.orderrow_custname);
            mTotalPriceTextView = (TextView) view.findViewById(R.id.orderrow_totalprice);
            mOrderConfirmedTextView = (TextView) view.findViewById(R.id.orderrow_orderconfirmed);
            mOrderDeliveredTextView = (TextView) view.findViewById(R.id.orderrow_orderdelivered);
            mRedConfirmImageView = (ImageView) view.findViewById(R.id.img_red_confirm);
            mGreenConfirmImageView = (ImageView) view.findViewById(R.id.img_green_confirm);
            mRedDeliverTextView = (ImageView) view.findViewById(R.id.img_deliver_red);
            mGreenDeliverTextView = (ImageView) view.findViewById(R.id.img_deliver_green);
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

    PendingOrdersAdapter(ArrayList<Orders> orderList) {
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
        holder.mOrderIdTextView.setText(orders.getOrderId());
        holder.mCustomerNameTextView.setText(orders.getCustomername());
        holder.mTotalPriceTextView.setText(orders.getTotalprice());
        holder.mOrderConfirmedTextView.setText(orders.getOrderconfirmed());
        holder.mOrderDeliveredTextView.setText(orders.getOrderdelivered());

        String timestamp = orders.getOrdertime();
        String orderDate = timestamp.substring(0, 2);
        Log.d("orderdate", orderDate);

        String orderMonth = "";
        String month = timestamp.substring(3,5);
        switch (month){

            case "01": orderMonth = "Jan"; break;
            case "02": orderMonth = "Feb"; break;
            case "03": orderMonth = "Mar"; break;
            case "04": orderMonth = "Apr"; break;
            case "05": orderMonth = "May"; break;
            case "06": orderMonth = "Jun"; break;
            case "07": orderMonth = "Jul"; break;
            case "08": orderMonth = "Aug"; break;
            case "09": orderMonth = "Sep"; break;
            case "10": orderMonth = "Oct"; break;
            case "11": orderMonth = "Nov"; break;
            case "12": orderMonth = "Dec"; break;
        }

        String orderYear = timestamp.substring(6, 10);
        String orderTiming = timestamp.substring(11, 16);
        if(timestamp.length()>19){
            orderTiming = orderTiming + timestamp.substring(20);
        }

        holder.mOrderTimeTextView.setText("Ordered on " + orderDate + " " + orderMonth + " " + orderYear + " at " + orderTiming);
        //02-10-2017 09:59:15 AM - timestamp format

        if (orders.getOrderconfirmed().equals("Confirmed")) {

            holder.mRedConfirmImageView.setVisibility(View.GONE);
            holder.mGreenConfirmImageView.setVisibility(View.VISIBLE);
        } else {

            holder.mGreenConfirmImageView.setVisibility(View.GONE);
            holder.mRedConfirmImageView.setVisibility(View.VISIBLE);
        }

        if (orders.getOrderdelivered().equals("Delivered")) {

            holder.mRedDeliverTextView.setVisibility(View.GONE);
            holder.mGreenDeliverTextView.setVisibility(View.VISIBLE);
        } else{

            holder.mGreenDeliverTextView.setVisibility(View.GONE);
            holder.mRedDeliverTextView.setVisibility(View.VISIBLE);
        }

        Log.e("dishesinfo orderId", orders.getOrderId());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}

