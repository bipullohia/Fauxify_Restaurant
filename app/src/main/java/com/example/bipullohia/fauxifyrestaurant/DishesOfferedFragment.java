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


/**
 * A simple {@link Fragment} subclass.
 */
public class DishesOfferedFragment extends Fragment {

    ExpandableListView expandableListView;
    DishesOfferedAdapter dishesOfferedAdapter;
    List<String> listParentData;
    JSONObject objMenu, jobmenu;
    String categoryName, category;
    HashMap<String, List<DishMenu>> listChildData;
    Integer noOfCategories;
    Button addCategory;
    JSONArray jsonArray;
    ArrayList<String> categories;

    public DishesOfferedFragment() {
        // Required empty public constructor
    }

    static boolean shouldRefreshOnResume = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_dishes_offered, container, false);

        expandableListView = (ExpandableListView) rootview.findViewById(R.id.expandable_listview);
        addCategory = (Button) rootview.findViewById(R.id.addcategory_fragdishoff);

        prepareDishData();

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                final EditText txtUrl = new EditText(getContext());

                alertbuilder.setMessage("Enter a new Category name")
                        .setCancelable(true)
                        .setView(txtUrl)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (txtUrl.getText().toString().length() > 0) {
                                            String abc = txtUrl.getText().toString();

                                            categoryName = abc.substring(0, 1).toUpperCase() + abc.substring(1);
                                            sendCategoryName();
                                            Toast.makeText(getContext(), categoryName, Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toast.makeText(getContext(), "You entered blank text", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                        )

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = alertbuilder.create();
                alert.setTitle("New Category");
                alert.show();

            }
        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                long p = expandableListView.getExpandableListPosition(i);

                int itemType = ExpandableListView.getPackedPositionType(p);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(p);
                final int childPosition = ExpandableListView.getPackedPositionChild(p);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                    category = listParentData.get(groupPosition);
                    //Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
                    CharSequence options[] = new CharSequence[]{"ADD an Item", "DELETE the Category"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select an option for " + category + ":");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {

                                Intent intent = new Intent(getContext(), AddDishIntoCategory.class);
                                intent.putExtra("Category", category);
                                intent.putExtra("categoryData", jsonArray.toString());
                                intent.putStringArrayListExtra("categoryList", categories);

                                startActivity(intent);

                            } else if (which == 1) {

                                AlertDialog.Builder alertbuilder = new AlertDialog.Builder(getContext());
                                alertbuilder.setMessage("DELETE Category " + "''" + category + "'' ?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                deleteCategory();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

                    category = listParentData.get(groupPosition);

                    final String currentitem = listChildData.get(listParentData.get(groupPosition)).get(childPosition).getdishName();
                    final String currentitemid = listChildData.get(listParentData.get(groupPosition)).get(childPosition).getDishId();
                    final String currentitemprice = listChildData.get(listParentData.get(groupPosition)).get(childPosition).getdishPrice();
                    final int currentitemidtype = listChildData.get(listParentData.get(groupPosition)).get(childPosition).getIsVeg();

                    CharSequence options[] = new CharSequence[]{"MODIFY " + currentitem, "DELETE " + currentitem};

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Select an Option:");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {

                                Intent intent1 = new Intent(getContext(), ModifyDishIntoCategory.class);
                                intent1.putExtra("Category", category);

                                intent1.putExtra("dishname", currentitem);
                                intent1.putExtra("dishprice", currentitemprice);
                                intent1.putExtra("dishid", currentitemid);
                                intent1.putExtra("dishtype", currentitemidtype);

                                intent1.putExtra("categoryData", jsonArray.toString());
                                intent1.putStringArrayListExtra("categoryList", categories);
                                startActivity(intent1);

                            } else if (which == 1) {

                                AlertDialog.Builder alertbuilder1 = new AlertDialog.Builder(getContext());
                                alertbuilder1.setMessage("DELETE ''" + currentitem + "'' ?")
                                        .setCancelable(true)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {


                                                jobmenu = new JSONObject();

                                                for (int i = 0; i < categories.size(); i++) {

                                                    if (!categories.get(i).equals(category)) {

                                                        try {
                                                            jobmenu.accumulate(categories.get(i), jsonArray.get(i));
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    } else if (categories.get(i).equals(category)) {

                                                        JSONObject jobb;
                                                       // jodishid = new JSONObject();

                                                        try {


                                                            jobb = jsonArray.getJSONObject(i);
                                                            jobb.remove(currentitemid);
                                                            //Iterator x = jobb.keys();

                                                            //while (x.hasNext()) {


                                                            //  String key = (String) x.next();


                                                            //   if (!key.equals(currentitemid)) {

                                                            //  jodishid.put(key, jobb.get(key));
                                                            // }
                                                            jobmenu.accumulate(categories.get(i), jobb);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


                                                    }

                                                }




                                            Log.e("final json",String.valueOf(jobmenu));

                                            deleteItem();

                                            Toast.makeText(

                                            getContext(),

                                            "Item Deleted "+currentitem,Toast.LENGTH_SHORT).

                                            show();

                                        }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

            else

            {
                return false;
            }


        }


    }

    );

    return rootview;

}

    private void deleteItem() {

        new bTaskDeleteItem().execute();
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

        new bTaskDeleteCategory().execute();
    }

    private void sendCategoryName() {


        new bTaskAddCategory().execute();

    }

    private void prepareDishData() {

        new bgroundtaskPrepareDishData().execute();

    }

class bgroundtaskPrepareDishData extends AsyncTask<Void, Void, String> {

    String json_url;
    String JSON_STRING;
    JSONObject jobject;
    ProgressDialog pd;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String restId = sharedPref.getString("restId", null);

        json_url = MainActivity.requestURL + "Restaurants/" + restId;
        pd = ProgressDialog.show(getContext(), "", "Loading Dishes offered...", false);
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

            categories = new ArrayList<String>();

            jobject = new JSONObject(resultjson);
            JSONObject job = jobject.getJSONObject("Menu");

            objMenu = job;

            Iterator x = job.keys();
            jsonArray = new JSONArray();
            noOfCategories = 0;

            while (x.hasNext()) {

                String key = (String) x.next();
                categories.add(key);
                jsonArray.put(job.get(key));
                noOfCategories++;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        listParentData = new ArrayList<String>();
        listChildData = new HashMap<String, List<DishMenu>>();

        for (int i = 0; i <= noOfCategories - 1; i++) {

            try {

                listParentData.add(categories.get(i));
                String categorydetails = String.valueOf(jsonArray.getJSONObject(i));

                JSONObject jo = new JSONObject(categorydetails);

                Log.e("result", String.valueOf(jo));

                ArrayList<String> dishDetails = new ArrayList<>();
                ArrayList<String> dishId = new ArrayList<>();

                Iterator iter = jo.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    dishId.add(key);
                    dishDetails.add(jo.getString(key));
                }

                Log.e("dishid", dishId.toString());

                ArrayList<DishMenu> dishlist = new ArrayList<>();

                for (int j = 0; j <= (dishId.size() - 1); j++) {

                    JSONObject jsob = new JSONObject(dishDetails.get(j));

                    DishMenu dishes = new DishMenu(jsob.getString("dishname"), jsob.getString("dishprice"),
                            dishId.get(j), jsob.getInt("isveg"));
                    dishlist.add(dishes);
                }

                listChildData.put(listParentData.get(i), dishlist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dishesOfferedAdapter = new DishesOfferedAdapter(getContext(), listParentData, listChildData);
        expandableListView.setAdapter(dishesOfferedAdapter);

        pd.dismiss();
    }
}


private class bTaskAddCategory extends AsyncTask<Void, Void, String>

{

    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken ;


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


            Log.e("test", json);
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

private class bTaskDeleteCategory extends AsyncTask<Void, Void, String> {

    JSONArray jsonArray1 = new JSONArray();
    ArrayList<String> categories1 = new ArrayList<>();
    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken ;

        Log.e("categories", categories.toString());

        for (int i = 0; i < categories.size(); i++) {

            if (!categories.get(i).equals(category)) {

                Log.e("ismatch", String.valueOf(!categories.get(i).equals(category)) + String.valueOf(categories.get(i)) + "  " + category);
                categories1.add(categories.get(i));
                try {
                    jsonArray1.put(jsonArray.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        Log.e("abcdefghi", categories1.toString() + "   " + jsonArray1.toString());

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


            Log.e("test234", json);
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

private class bTaskDeleteItem extends AsyncTask<Void, Void, String>

{

    String json_url, userId, userToken;

    @Override
    protected void onPreExecute() {

        SharedPreferences sharedPref;
        sharedPref = DishesOfferedFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        userId = sharedPref.getString("restId", null);
        userToken = sharedPref.getString("restToken", null);

        json_url = MainActivity.requestURL + "Restaurants/" + userId + "?access_token=" + userToken ;

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


            Log.e("test", json);
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


