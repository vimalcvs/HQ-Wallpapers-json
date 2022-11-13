package com.example.hqwallpaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private final ArrayList<Category> dataset;
    private final AdapterView.OnItemClickListener onItemClickListener;

    public CategoryAdapter(ArrayList<Category> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item_layout, parent, false);
        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {

        Category currentItem = dataset.get(position);

        Picasso.get().load(currentItem.getImageURL()).into(holder.ivCategory);
        holder.tvCategory.setText(currentItem.name);

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder{

        ImageView ivCategory;
        TextView tvCategory;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
