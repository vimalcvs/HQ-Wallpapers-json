package com.example.hqwallpaper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout errorLayout;
    private RecyclerView rvCategories;
    private ArrayList<Category> categoryArrayList;
    private CategoryAdapter adapter;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressBar);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        errorLayout = view.findViewById(R.id.errorLayout);
        Button btnRetry = view.findViewById(R.id.btnRetry);
        rvCategories = view.findViewById(R.id.rvCategories);

        fetchCategoriesFromServer();

        swipeRefresh.setOnRefreshListener(this::fetchCategoriesFromServer);
        btnRetry.setOnClickListener(v -> {
            fetchCategoriesFromServer();
            errorLayout.setVisibility(View.GONE);
        });

    }

    private void fetchCategoriesFromServer() {
        String categoriesURL = "https://androidworkshop.net/apis/pixabay/categories.php";

        if (!swipeRefresh.isRefreshing()){
            progressBar.setVisibility(View.VISIBLE);
        }

        StringRequest request = new StringRequest(categoriesURL, response -> {
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
            errorLayout.setVisibility(View.GONE);

            try {
                Gson gson = new Gson();
                JSONArray categoriesArray = new JSONArray(response);
                categoryArrayList = new ArrayList<>();
                categoryArrayList = gson.fromJson(categoriesArray.toString(), new TypeToken<ArrayList<Category>>(){}.getType());

                rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new CategoryAdapter(categoryArrayList, (parent, view, position, id) -> {
                    Category selecteditem = categoryArrayList.get(position);
                    Intent categoryIntent = new Intent(getContext(), CategoryDetailActivity.class);
                    categoryIntent.putExtra("category", selecteditem);
                    startActivity(categoryIntent);

                });
                rvCategories.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
                errorLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Unable to fetch data from Server", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            error.printStackTrace();
            if (categoryArrayList == null){
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                swipeRefresh.setRefreshing(false);
            }
        });
        Volley.newRequestQueue(requireContext()).add(request);
    }
}