package com.example.bipullohia.fauxifyrestaurant;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class DishesOfferedFragment extends Fragment {

    public static final String TAG = "DishesOfferedFrag";

    ExpandableListView mExpandableListView;
    DishesOfferedAdapter mDishesOfferedAdapter;
    List<String> mParentDataList;
    JSONObject objMenu, jobmenu;
    String categoryName, category;
    HashMap<String, List<DishMenu>> mChildDataHashMap;
    Integer mNoOfCategories;
    Button mAddCategoryButton;
    JSONArray jsonArray;
    ArrayList<String> mCategoriesList;

    public DishesOfferedFragment() {
        // Required empty public constructor
    }

    static boolean shouldRefreshOnResume = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_dishes_offered, container, false);

        mExpandableListView = (ExpandableListView) rootview.findViewById(R.id.expandable_listview);
        mAddCategoryButton = (Button) rootview.findViewById(R.id.button_add_category);

        prepareDishData();

        mAddCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                final EditText txtUrl = new EditText(getContext());

                alertbuilder.setMessage(R.string.enter_new_category)
                        .setCancelable(true)
                        .setView(txtUrl)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (txtUrl.getText().toString().length() > 0) {
                                            String abc = txtUrl.getText().toString();

                                            categoryName = abc.substring(0, 1).toUpperCase() + abc.substring(1);
                                            sendCategoryName();
                                            Toast.makeText(getContext(), categoryName + " added", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), R.string.blank_text_try_again,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        )

                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertbuilder.create();
                alert.setTitle(getString(R.string.new_category));
                alert.show();
            }
        });

        mExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                long position = mExpandableListView.getExpandableListPosition(i);

                int itemType = ExpandableListView.getPackedPositionType(position);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(position);
                final int childPosition = ExpandableListView.getPackedPositionChild(position);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    category = mParentDataList.get(groupPosition);

                    CharSequence options[] = new CharSequence[]{getString(R.string.add_an_item),
                                                                getString(R.string.delete_the_category)};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select an option for " + category + ":");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {

                                Intent intent = new Intent(getContext(), AddDishIntoCategoryActivity.class);
                                intent.putExtra("category", category);
                                intent.putExtra("categoryData", jsonArray.toString());
                                intent.putStringArrayListExtra("categoryList", mCategoriesList);

                                startActivity(intent);

                            } else if (which == 1) {

                                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                                alertbuilder.setMessage("Delete Category " + "''" + category + "'' ?")
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                deleteCategory();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = alertbuilder.create();
                                alert.show();
                            }
                        }
                    });
                    builder.show();
                    return true;

                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

                    category = mParentDataList.get(groupPosition);

                    final String currentitem = mChildDataHashMap
                            .get(mParentDataList.get(groupPosition)).get(childPosition).getdishName();
                    final String currentitemid = mChildDataHashMap
                            .get(mParentDataList.get(groupPosition)).get(childPosition).getDishId();
                    final String currentitemprice = mChildDataHashMap
                            .get(mParentDataList.get(groupPosition)).get(childPosition).getdishPrice();
                    final int currentitemidtype = mChildDataHashMap
                            .get(mParentDataList.get(groupPosition)).get(childPosition).getIsVeg();

                    CharSequence options[] = new CharSequence[]{"Modify " + currentitem, "Delete " + currentitem};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.select_an_option);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {

                                Intent intent1 = new Intent(getContext(), ModifyDishIntoCategoryActivity.class);
                                intent1.putExtra("category", category);

                                intent1.putExtra("dishname", currentitem);
                                intent1.putExtra("dishprice", currentitemprice);
                                intent1.putExtra("dishid", currentitemid);
                                intent1.putExtra("dishtype", currentitemidtype);

                                intent1.putExtra("categoryData", jsonArray.toString());
                                intent1.putStringArrayListExtra("categoryList", mCategoriesList);
                                startActivity(intent1);

                            } else if (which == 1) {

                                AlertDialog.Builder alertbuilder1 = new AlertDialog.Builder(getContext());
                                alertbuilder1.setMessage("Delete ''" + currentitem + "'' ?")
                                        .setCancelable(true)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                jobmenu = new JSONObject();
                                                for (int i = 0; i < mCategoriesList.size(); i++) {

                                                    if (!mCategoriesList.get(i).equals(category)) {

                                                        try {
                                                            jobmenu.accumulate(mCategoriesList.get(i), jsonArray.get(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else if (mCategoriesList.get(i).equals(category)) {

                                                        JSONObject jobb;

                                                        try {
                                                            jobb = jsonArray.getJSONObject(i);
                                                            jobb.remove(currentitemid);
                                                            jobmenu.accumulate(mCategoriesList.get(i), jobb);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }

                                            //Log.d(TAG, "menu: " + String.valueOf(jobmenu));

                                            deleteItem();
                                            Toast.makeText(getContext(),"Item Deleted: "+currentitem,Toast.LENGTH_SHORT).show();
                                        }
                            })

                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alert = alertbuilder1.create();
                            alert.show();
                        }
                    }
                });

                builder.show();
                return true;
            }
            else{
                return false;
            }
        }
    });
    return rootview;
}

    private void deleteItem() {
        new BGTaskDeleteItem().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (shouldRefreshOnResume) {
            shouldRefreshOnResume = false;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(DishesOfferedFragment.this).attach(DishesOfferedFragment.this).commit();
        }
    }

    private void deleteCategory() {
        new BGTaskDeleteCategory().execute();
    }

    private void sendCategoryName() {
        new BGTaskAddCategory().execute();
    }

    private void prepareDishData() {
        new BGTaskPrepareDishData().execute();
    }

private class BGTaskPrepareDishData extends AsyncTask<Void, Void, String> {

    String json_url;
    String JSON_STRING;
    JSONObject jobject;
    ProgressDialog pd;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String restId = sharedPref.getString("restId", null);

        json_url = getString(R.string.request_url) + "Restaurants/" + restId;
        pd = ProgressDialog.show(getContext(), "", getString(R.string.loading_dishes_offered), false);
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

            mCategoriesList = new ArrayList<String>();

            jobject = new JSONObject(resultjson);
            JSONObject job = jobject.getJSONObject("Menu");

            objMenu = job;

            Iterator x = job.keys();
            jsonArray = new JSONArray();
            mNoOfCategories = 0;

            while (x.hasNext()) {

                String key = (String) x.next();
                mCategoriesList.add(key);
                jsonArray.put(job.get(key));
                mNoOfCategories++;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        mParentDataList = new ArrayList<String>();
        mChildDataHashMap = new HashMap<String, List<DishMenu>>();

        for (int i = 0; i <= mNoOfCategories - 1; i++) {

            try {
                mParentDataList.add(mCategoriesList.get(i));
                String categorydetails = String.valueOf(jsonArray.getJSONObject(i));

                JSONObject jo = new JSONObject(categorydetails);

                ArrayList<String> dishDetails = new ArrayList<>();
                ArrayList<String> dishId = new ArrayList<>();

                Iterator iter = jo.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    dishId.add(key);
                    dishDetails.add(jo.getString(key));
                }

                //Log.d(TAG, "dishid" + dishId.toString());

                ArrayList<DishMenu> dishlist = new ArrayList<>();

                for (int j = 0; j <= (dishId.size() - 1); j++) {

                    JSONObject jsob = new JSONObject(dishDetails.get(j));
                    DishMenu dishes = new DishMenu(jsob.getString("dishname"), jsob.getString("dishprice"),
                            dishId.get(j), jsob.getInt("isveg"));
                    dishlist.add(dishes);
                }

                mChildDataHashMap.put(mParentDataList.get(i), dishlist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mDishesOfferedAdapter = new DishesOfferedAdapter(getContext(), mParentDataList, mChildDataHashMap);
        mExpandableListView.setAdapter(mDishesOfferedAdapter);

        pd.dismiss();
    }
}


private class BGTaskAddCategory extends AsyncTask<Void, Void, String>

{
    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = getString(R.string.request_url) + "Restaurants/" + userId + "?access_token=" + userToken ;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            URL urll = new URL(json_url);
            HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json");

            try {
                JSONObject jo = new JSONObject();
                objMenu.put(categoryName, jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("Menu", objMenu);

            String json = jsonObject.toString();

            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = httpConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(httpConnection.getResponseMessage());
            }

            //Log.d(TAG, "testdata -" + json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(DishesOfferedFragment.this).attach(DishesOfferedFragment.this).commit();
    }
}

private class BGTaskDeleteCategory extends AsyncTask<Void, Void, String> {

    JSONArray jsonArray1 = new JSONArray();
    ArrayList<String> categories1 = new ArrayList<>();
    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = getString(R.string.request_url) + "Restaurants/" + userId + "?access_token=" + userToken ;

        Log.d(TAG, "category list: " + mCategoriesList.toString());

        for (int i = 0; i < mCategoriesList.size(); i++) {

            if (!mCategoriesList.get(i).equals(category)) {

                categories1.add(mCategoriesList.get(i));
                try {
                    jsonArray1.put(jsonArray.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(json_url);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject menu = new JSONObject();
            JSONObject job = new JSONObject();

            for (int i = 0; i < categories1.size(); i++) {
                job.put(categories1.get(i), jsonArray1.get(i));
            }

            menu.accumulate("Menu", job);
            String json = menu.toString();

            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = httpConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(httpConnection.getResponseMessage());
            }

            //Log.d(TAG, "testdata2 -" + json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(DishesOfferedFragment.this).attach(DishesOfferedFragment.this).commit();
    }
}

private class BGTaskDeleteItem extends AsyncTask<Void, Void, String>

{
    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = getString(R.string.request_url) + "Restaurants/" + userId + "?access_token=" + userToken;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            URL url = new URL(json_url);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            httpConnection.setRequestMethod("PUT");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("Menu", jobmenu);

            String json = jsonObject.toString();

            OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
            out.write(json);
            out.flush();
            out.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = httpConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                System.out.println("" + sb.toString());
            } else {
                System.out.println(httpConnection.getResponseMessage());
            }

            //Log.d(TAG, "testdata3 -" + json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(DishesOfferedFragment.this).attach(DishesOfferedFragment.this).commit();
    }
}
}