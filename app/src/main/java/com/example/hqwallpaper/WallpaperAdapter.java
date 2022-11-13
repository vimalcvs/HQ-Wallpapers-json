package com.example.hqwallpaper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.WallpaperHolder> {

    ArrayList<Wallpaper> dataset;
    AdapterView.OnItemClickListener onclick;
    AdapterView.OnItemClickListener favoriteListner;
    OnLoadMoreListner moreListner;
    private DbHelper dbHelper;

    public WallpaperAdapter(ArrayList<Wallpaper> dataset, AdapterView.OnItemClickListener onclick) {
        this.dataset = dataset;
        this.onclick = onclick;
    }

    public void setFavoriteListner(AdapterView.OnItemClickListener favoriteListner) {
        this.favoriteListner = favoriteListner;
    }

    public void setMoreListner(OnLoadMoreListner moreListner) {
        this.moreListner = moreListner;
    }

    @NonNull
    @Override
    public WallpaperHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper_layout, parent, false);
        if (dbHelper == null) {
            dbHelper = new DbHelper(parent.getContext());
        }
        return new WallpaperHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperHolder holder, int position) {

        Wallpaper currentItem = dataset.get(position);
        Picasso.get().load(currentItem.webformatURL).placeholder(R.drawable.placeholder_vertical)
                .error(R.drawable.error_vertical).into(holder.ivWallpaper);

        if (dbHelper.isFavoriteWallpaper(currentItem)) {
            holder.ivFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }

        holder.itemView.setOnClickListener(v -> onclick.onItemClick(null, holder.itemView, holder.getAdapterPosition(), 0));

        holder.ivFavorite.setOnClickListener(v -> {
            if (favoriteListner != null) {
                favoriteListner.onItemClick(null, holder.ivFavorite, holder.getAdapterPosition(), 0);
            }
        });

        if (position == dataset.size() -1) {
            if (moreListner != null) {
                moreListner.onLoadMore();
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class WallpaperHolder extends RecyclerView.ViewHolder{

        ImageView ivWallpaper;
        ImageView ivFavorite;
        public WallpaperHolder(@NonNull View itemView) {
            super(itemView);
            ivWallpaper = itemView.findViewById(R.id.ivWallpaper);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}
