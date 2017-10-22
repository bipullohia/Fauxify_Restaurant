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


class DeliveredOrderAdapter extends RecyclerView.Adapter<DeliveredOrderAdapter.MyViewHolder> {

    private static final String TAG = "DeliveredOrderAdapter";
    private static ArrayList<Orders> orderList;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView orderIdTextView, custNameTextView, orderTimeTextView, totalPriceTextView,
                 orderDeliveredTextView, orderConfirmedTextView;
        ImageView redConfirmImageView, greenConfirmImageView, redDeliverImageView, greenDeliverImageView;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnClickListener(this);

            orderIdTextView = (TextView) view.findViewById(R.id.orderrow_orderid);
            orderTimeTextView = (TextView) view.findViewById(R.id.orderrow_ordertime);
            custNameTextView = (TextView) view.findViewById(R.id.orderrow_custname);
            totalPriceTextView = (TextView) view.findViewById(R.id.orderrow_totalprice);
            orderConfirmedTextView = (TextView) view.findViewById(R.id.orderrow_orderconfirmed);
            orderDeliveredTextView = (TextView) view.findViewById(R.id.orderrow_orderdelivered);
            redConfirmImageView = (ImageView) view.findViewById(R.id.img_red_confirm);
            greenConfirmImageView = (ImageView) view.findViewById(R.id.img_green_confirm);
            redDeliverImageView = (ImageView) view.findViewById(R.id.img_deliver_red);
            greenDeliverImageView = (ImageView) view.findViewById(R.id.img_deliver_green);
        }

        @Override
        public void onClick(View v) {

            int position= getAdapterPosition();
            Intent intent;
            intent = new Intent(context, DeliveredOrderDetailsActivity.class);

            Orders orders = orderList.get(position);

            intent.putExtra("orderid",orders.getOrderId());
            intent.putExtra("customername",orders.getCustomerName());
            intent.putExtra("ordercustemail", orders.getCustomerEmail());
            intent.putExtra("totalitems",orders.getTotalItems());
            intent.putExtra("totalprice", orders.getTotalPrice());
            intent.putExtra("ordertime", orders.getOrderTime());
            intent.putExtra("orderconfirmed", orders.getOrderConfirmed());
            intent.putExtra("orderdelivered", orders.getOrderDelivered());
            intent.putExtra("customerorder", orders.getCustomerOrder());
            intent.putExtra("totalitemprice", orders.getTotalItemsPrice());
            intent.putExtra("customeraddress", orders.getCustomerAddress());
            intent.putExtra("deliveryfee", orders.getDeliveryFee());

            context.startActivity(intent);
        }
    }

    DeliveredOrderAdapter(ArrayList<Orders> orderList)
    {
        DeliveredOrderAdapter.orderList = orderList;
    }

    @Override
    public DeliveredOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)  {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout_ordersfragment, parent, false);

        return new DeliveredOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeliveredOrderAdapter.MyViewHolder holder, int position) {
        Orders orders = orderList.get(position);
        holder.orderIdTextView.setText(orders.getOrderId());
        holder.custNameTextView.setText(orders.getCustomerName());
        holder.totalPriceTextView.setText(orders.getTotalPrice());
        holder.orderConfirmedTextView.setText(orders.getOrderConfirmed());
        holder.orderDeliveredTextView.setText(orders.getOrderDelivered());

        String timestamp = orders.getOrderTime();
        String orderDate = timestamp.substring(0, 2);
        Log.d(TAG, "Order date: " + orderDate);

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

        holder.orderTimeTextView.setText("Ordered on " + orderDate + " " + orderMonth + " " + orderYear + " at " + orderTiming);

        if (orders.getOrderConfirmed().equals("Confirmed")) {
            holder.redConfirmImageView.setVisibility(View.GONE);
            holder.greenConfirmImageView.setVisibility(View.VISIBLE);

        } else {
            holder.greenConfirmImageView.setVisibility(View.GONE);
            holder.redConfirmImageView.setVisibility(View.VISIBLE);
        }

        if (orders.getOrderDelivered().equals("Delivered")) {
            holder.redDeliverImageView.setVisibility(View.GONE);
            holder.greenDeliverImageView.setVisibility(View.VISIBLE);

        } else{
            holder.greenDeliverImageView.setVisibility(View.GONE);
            holder.redDeliverImageView.setVisibility(View.VISIBLE);
        }

        Log.d(TAG, "Dishes Info: " + orders.getCustomerOrder());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}


