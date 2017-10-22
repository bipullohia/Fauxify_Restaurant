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


class DishesOfferedAdapter extends BaseExpandableListAdapter {

    private List<String> mHeaderTitlesList;
    private HashMap<String, List<DishMenu>> mChildTitlesHashMap;
    private Context mContext;

    DishesOfferedAdapter(Context context, List<String> mHeaderTitlesList, HashMap<String, List<DishMenu>> mChildTitlesHashMap) {

        this.mContext = context;
        this.mChildTitlesHashMap = mChildTitlesHashMap;
        this.mHeaderTitlesList = mHeaderTitlesList;
    }

    @Override
    public int getGroupCount() {
        return mHeaderTitlesList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mChildTitlesHashMap.get(mHeaderTitlesList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return mHeaderTitlesList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mChildTitlesHashMap.get(mHeaderTitlesList.get(i)).get(i1);
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
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.rowlayout_dishesoffered_parentviewlist, null);
        }

        TextView titleParentTextView = (TextView) view.findViewById(R.id.textview_parent);
        titleParentTextView.setPadding(108, 0, 0, 0);
        titleParentTextView.setText(title);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {

       DishMenu childDishMenu = (DishMenu) getChild(i,i1);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.rowlayout_dishesoffered_childviewlist, null);
        }

        ImageView vegImageView = (ImageView) view.findViewById(R.id.isVegDish);
        ImageView nonVegImageView = (ImageView) view.findViewById(R.id.isNonvegDish);

        TextView dishNameTextView = (TextView) view.findViewById(R.id.child_dishname_textview);
        TextView dishPriceTextView = (TextView) view.findViewById(R.id.child_dishprice_textview);
        dishNameTextView.setText(childDishMenu.getdishName());
        dishPriceTextView.setText(childDishMenu.getdishPrice());

        if(childDishMenu.getIsVeg()==1){
            nonVegImageView.setVisibility(View.GONE);
            vegImageView.setVisibility(View.VISIBLE);
        }

        else if(childDishMenu.getIsVeg()==0){
            vegImageView.setVisibility(View.GONE);
            nonVegImageView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
