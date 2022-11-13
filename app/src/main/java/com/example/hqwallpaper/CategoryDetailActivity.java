package com.example.hqwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryDetailActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout errorLayout;
    private RecyclerView rvDetailCategory;
    private ArrayList<Wallpaper> categoryList;
    private Category selectedItem;
    private WallpaperAdapter adapter;
    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        Toolbar toolbar = findViewById(R.id.toolBar);
        progressBar = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        errorLayout = findViewById(R.id.errorLayout);
        Button btnRetry = findViewById(R.id.btnRetry);
        rvDetailCategory = findViewById(R.id.rvDetailCategory);

        rvDetailCategory.setLayoutManager(new GridLayoutManager(CategoryDetailActivity.this, 3));

        selectedItem = (Category) getIntent().getSerializableExtra("category");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(selectedItem.name);
        toolbar.setNavigationOnClickListener(v -> finish());

        fetchDataFromServer();
        btnRetry.setOnClickListener(v -> {
            fetchDataFromServer();
            errorLayout.setVisibility(View.GONE);
        });
        swipeRefresh.setOnRefreshListener(() -> {
            pageNumber = 1;
            categoryList = null;
            adapter = null;
            fetchDataFromServer();
        });
    }

    private void fetchDataFromServer() {
        String categoryURL = "https://pixabay.com/api/?key=21806668-9f282bdb091be8dd3d877dcb8&image_type=photo&orientation=vertical&order=latest&safesearch=true&per_page=20&page=" + pageNumber +"&category=" + selectedItem.name.toLowerCase();
        if (!swipeRefresh.isRefreshing()){
            progressBar.setVisibility(View.VISIBLE);
        }
        StringRequest request = new StringRequest(categoryURL, response -> {

            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            Gson gson = new Gson();
            try {
                JSONObject mainObject = new JSONObject(response);
                JSONArray categoryArray = mainObject.getJSONArray("hits");
                if (categoryList == null) {
                    categoryList = new ArrayList<>();
                }
                ArrayList<Wallpaper> tempList = gson.fromJson(categoryArray.toString(), new TypeToken<ArrayList<Wallpaper>>(){}.getType());
                categoryList.addAll(tempList);

                if (adapter == null) {
                    adapter = new WallpaperAdapter(categoryList, (parent, view, position, id) -> {
                        Wallpaper currentItem = categoryList.get(position);
                        Intent categoryIntent = new Intent(CategoryDetailActivity.this, LatestDetailActivity.class);
                        categoryIntent.putExtra("wallpaper", currentItem);
                        startActivity(categoryIntent);
                    });

                    adapter.setMoreListner(() -> {
                        pageNumber++;
                        fetchDataFromServer();
                    });
                    rvDetailCategory.setAdapter(adapter);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                errorLayout.setVisibility(View.VISIBLE);
            }
        }, error -> {
            error.printStackTrace();

            if (categoryList == null) {
                errorLayout.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
        Volley.newRequestQueue(CategoryDetailActivity.this).add(request);
        request.setShouldCache(false);
    }
}