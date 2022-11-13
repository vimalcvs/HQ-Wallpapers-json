package com.example.hqwallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class LatestFragment extends Fragment {

    public LatestFragment() {
    }

    public boolean errorOccured = false;
    BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("mytag", "Network changed");
            ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Log.i("mytag", "Internet Connected");
                  if (errorOccured) {
                      fetchdata();
                  }

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter connectionIntent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (getActivity() != null) {
            getActivity().registerReceiver(networkChangeReceiver, connectionIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_latest, container, false);
    }

    private RecyclerView rvWallpaper;
    private LinearLayout errorLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<Wallpaper> latestwallpaperList;
    private WallpaperAdapter adapter;
    private DbHelper dbHelper;
    private int pageNumber = 1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvWallpaper = view.findViewById(R.id.rvWallpaper);
        Button btnRetry = view.findViewById(R.id.btnRetry);
        errorLayout = view.findViewById(R.id.errorLayout);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        rvWallpaper.setLayoutManager(new GridLayoutManager(getContext(), 3));

        dbHelper = new DbHelper(getContext());
        fetchdata();

        btnRetry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            fetchdata();
        });
        swipeRefresh.setOnRefreshListener(() -> {
            pageNumber = 1;
            adapter = null;
            latestwallpaperList = null;
            fetchdata();
        });
    }

    private void fetchdata() {
        String wallpaperLink = "https://pixabay.com/api/?key=21806668-9f282bdb091be8dd3d877dcb8&image_type=photo&orientation=vertical&order=latest&safesearch=true&per_page=20&page=" + pageNumber ;

        if (!swipeRefresh.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        errorLayout.setVisibility(View.GONE);

        StringRequest request = new StringRequest(wallpaperLink, response -> {

            errorOccured = false;
            try {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Gson gson = new Gson();

                if (latestwallpaperList == null) {
                    latestwallpaperList = new ArrayList<>();
                }
                JSONObject mainObject = new JSONObject(response);
                JSONArray hitsArray = mainObject.getJSONArray("hits");
                ArrayList<Wallpaper> tempList = gson.fromJson(hitsArray.toString(), new TypeToken<ArrayList<Wallpaper>>(){}.getType());
                latestwallpaperList.addAll(tempList);
                if (adapter == null) {

                    adapter = new WallpaperAdapter(latestwallpaperList, (parent, view, position, id) -> {

                        Wallpaper selectedObject = latestwallpaperList.get(position);
                        Intent latestIntent = new Intent(getContext(), LatestDetailActivity.class);
                        latestIntent.putExtra("wallpaper", selectedObject);
                        startActivity(latestIntent);
                    });

                    adapter.setFavoriteListner((parent, view, position, id) -> {
                        Wallpaper currentWall = latestwallpaperList.get(position);
                        if (dbHelper.isFavoriteWallpaper(currentWall)) {
                            int deleteCount = dbHelper.deleteFavoriteWallpaper(currentWall);
                            if (deleteCount > 0) {
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Unable to remove from favorites", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            long insertCount = dbHelper.addToFavoriteWallpaper(currentWall);
                            if (insertCount > -1) {
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getContext(), "Unable to add favorite", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    adapter.setMoreListner(() -> {
                        pageNumber++;
                        fetchdata();
                        progressBar.setVisibility(View.VISIBLE);
                    });
                    rvWallpaper.setAdapter(adapter);

                } else {
                    adapter.notifyDataSetChanged();
                }




            } catch (JSONException e) {
                e.printStackTrace();
                errorLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Exception Error", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            error.printStackTrace();
            swipeRefresh.setRefreshing(false);
            if (latestwallpaperList == null) {
                errorLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                errorOccured = true;
            } else {
                Toast.makeText(getContext(), "Unable to load data, check internet", Toast.LENGTH_SHORT).show();
            }

        });
        Volley.newRequestQueue(requireContext()).add(request);

    }
}