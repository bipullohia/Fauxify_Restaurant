package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.MyViewHolder> {

    //This Adapter class is to inflate the cart items dish detail row
    private ArrayList<CurrentOrder> dishesListInCart;

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dishNameTextView, dishPriceTextView, dishQuantityTextView, dishAmountTextView;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();

            dishNameTextView = (TextView) view.findViewById(R.id.dishname_incart);
            dishPriceTextView = (TextView) view.findViewById(R.id.dishprice_incart);
            dishQuantityTextView = (TextView) view.findViewById(R.id.dishquantity_incart);
            dishAmountTextView = (TextView) view.findViewById(R.id.dishamount_incart);
        }
    }

    CurrentOrderAdapter(ArrayList<CurrentOrder> dishesListInCart) {
        this.dishesListInCart = dishesListInCart;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cartitem_dishesdetails_rowlayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CurrentOrder currentOrder = dishesListInCart.get(position);

        holder.dishNameTextView.setText(currentOrder.getCurrentdishName());
        holder.dishPriceTextView.setText(currentOrder.getCurrentdishPrice());
        holder.dishQuantityTextView.setText(String.valueOf(currentOrder.getCurrentdishQuantity()));
        holder.dishAmountTextView.setText(String.valueOf(Integer.parseInt(currentOrder.getCurrentdishPrice())*
                currentOrder.getCurrentdishQuantity()));
    }

    @Override
    public int getItemCount() {
        return dishesListInCart.size();
    }
}

