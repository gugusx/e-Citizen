package com.guguscode.reportcitizen.Menu;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guguscode.reportcitizen.Adapter.DataModel;
import com.guguscode.reportcitizen.Adapter.RvAdapter;
import com.guguscode.reportcitizen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuBeranda extends AppCompatActivity {


    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort system
    private SwipeRefreshLayout swipeRefreshLayout;

    private String URLstring = "http://aplikasi-tesis.info/tesis/beranda.php";
    ArrayList<DataModel> dataModelArrayList;
    private RvAdapter rvAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_beranda);
        //title action bar
        getSupportActionBar().setTitle("Beranda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        swipeRefreshLayout =findViewById(R.id.Swipe);


        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Urut", "terbaru");

        if (mSorting.equals("terbaru")){
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("terlama")){
            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        recyclerView = findViewById(R.id.recycler);

        fetchingJSON();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchingJSON();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

    }

    private void fetchingJSON() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("strrrrr", ">>" + response);

                        try {

                            JSONObject obj = new JSONObject(response);
                            if(obj.optString("status").equals("true")){

                                dataModelArrayList = new ArrayList<>();
                                JSONArray dataArray  = obj.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    DataModel playerModel = new DataModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);

                                    playerModel.setNp(dataobj.getString("nama_pelapor"));
                                    playerModel.setJk(dataobj.getString("sub_kategori"));
                                    playerModel.setKet(dataobj.getString("keterangan"));
                                    playerModel.setGbrURL(dataobj.getString("gambar"));
                                    playerModel.setWkt(dataobj.getString("waktu_lapor"));
                                    playerModel.setNt(dataobj.getString("nama_tempat"));
                                    dataModelArrayList.add(playerModel);

                                }
                                setupRecycler();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupRecycler(){

        rvAdapter = new RvAdapter(this,dataModelArrayList);
        recyclerView.setAdapter(rvAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort){
            showShortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShortDialog() {
        String[] sortOptions = {"Terbaru", "Terlama"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Berdasarkan")
                .setIcon(R.drawable.ic_sort)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Urut", "terbaru");
                            editor.apply();
                            recreate();
                        }
                        else if (which ==1) {
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putString("Urut", "terlama");
                            editor.apply();
                            recreate();
                        }
                    }
                });
        builder.show();
    }

}
