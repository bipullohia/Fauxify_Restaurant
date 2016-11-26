package com.example.bipullohia.fauxifyrestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Bipul Lohia on 11/5/2016.
 */

public class DishesOfferedAdapter extends BaseExpandableListAdapter {


    private List<String> header_titles;
    private HashMap<String, List<DishMenu>> child_titles;
    private Context ctx;

    DishesOfferedAdapter(Context ctx, List<String> header_titles, HashMap<String, List<DishMenu>> child_titles) {

        this.ctx = ctx;
        this.child_titles = child_titles;
        this.header_titles = header_titles;

    }

    @Override
    public int getGroupCount() {
        return header_titles.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return child_titles.get(header_titles.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return header_titles.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return child_titles.get(header_titles.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        String title = (String) this.getGroup(i);

        if (view == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.dishesoffered_parentviewlist, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.parent_textview);
        textView.setPadding(108, 0, 0, 0);
         textView.setText(title);

        return view;
    }


    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

       DishMenu child = (DishMenu) getChild(i,i1);


        if (view == null) {

            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.dishesoffered_childviewlist, null);
        }

        ImageView imgVeg = (ImageView) view.findViewById(R.id.isVegDish);
        ImageView imgNonveg = (ImageView) view.findViewById(R.id.isNonvegDish);

        TextView textView = (TextView) view.findViewById(R.id.child_textview);
        TextView textView1 = (TextView) view.findViewById(R.id.child_textviewprice);
        textView.setText(child.getdishName());
        textView1.setText(child.getdishPrice());

        if(child.getIsVeg()==1){
            imgNonveg.setVisibility(View.GONE);
            imgVeg.setVisibility(View.VISIBLE);
        }

        else if(child.getIsVeg()==0){
            imgVeg.setVisibility(View.GONE);
            imgNonveg.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
