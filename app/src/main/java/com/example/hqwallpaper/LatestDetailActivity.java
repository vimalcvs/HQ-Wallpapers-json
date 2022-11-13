package com.example.hqwallpaper;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.chrisbanes.photoview.PhotoView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class LatestDetailActivity extends AppCompatActivity {

    private PhotoView pvWallpaper;
    private Wallpaper selectedObject;
    private Button ibfavorite;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_detail);

        ibfavorite = findViewById(R.id.ibfavorite);
        pvWallpaper = findViewById(R.id.pvWallpaper);

        Button btnSetWall = findViewById(R.id.btnSetWall);
        Button ibShare = findViewById(R.id.ibShare);
        Button ibDownload = findViewById(R.id.ibDownload);

        dbHelper = new DbHelper(LatestDetailActivity.this);

        selectedObject = (Wallpaper) getIntent().getSerializableExtra("wallpaper");

        if (dbHelper.isFavoriteWallpaper(selectedObject)) {
            ibfavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
        } else {
            ibfavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
        }

        ibfavorite.setOnClickListener(v -> {
            if (dbHelper.isFavoriteWallpaper(selectedObject)) {
                int deleteCount = dbHelper.deleteFavoriteWallpaper(selectedObject);
                if (deleteCount > 0) {
                    ibfavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                } else {
                    Toast.makeText(LatestDetailActivity.this, "Unable to remove favorite", Toast.LENGTH_SHORT).show();
                }
            } else {
                long insertCount = dbHelper.addToFavoriteWallpaper(selectedObject);
                if (insertCount > -1) {
                    ibfavorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                } else {
                    Toast.makeText(LatestDetailActivity.this, "Unable to add favorite", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Picasso.get().load(selectedObject.largeImageURL).into(pvWallpaper);

        btnSetWall.setOnClickListener(v -> {
            WallpaperManager wallManager = (WallpaperManager) getSystemService(WALLPAPER_SERVICE);
            Picasso.get().load(selectedObject.largeImageURL).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        Toast.makeText(LatestDetailActivity.this, "Wallpaper updated", Toast.LENGTH_SHORT).show();
                        wallManager.setBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    Toast.makeText(LatestDetailActivity.this, "Unable to set Wallpaper", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                    Toast.makeText(LatestDetailActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                }
            });
        });

        ibShare.setOnClickListener(v -> {

            Drawable drawable = pvWallpaper.getDrawable();
            if (!(drawable instanceof BitmapDrawable)) {
                return;
            }

            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();

            File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String fileName = URLUtil.guessFileName(selectedObject.getLargeImageURL(), null, null);
            File wallpaperFile = new File(directory, fileName);

            try {
                FileOutputStream fos = new FileOutputStream(wallpaperFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(LatestDetailActivity.this, getPackageName() + ".provider", wallpaperFile));

                startActivity(shareIntent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(LatestDetailActivity.this, "Unable to Share this wallpaper", Toast.LENGTH_SHORT).show();
            }
        });

        ibDownload.setOnClickListener(v -> Dexter.withContext(LatestDetailActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Drawable drawable = pvWallpaper.getDrawable();
                        if (!(drawable instanceof BitmapDrawable)) {
                            return;
                        }
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        Bitmap bitmap = bitmapDrawable.getBitmap();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            OutputStream os;
                            try {

                                ContentResolver resolver = getContentResolver();
                                ContentValues cv = new ContentValues();
                                String fileName = URLUtil.guessFileName(selectedObject.getLargeImageURL(), null, null);
                                cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName + ".jpg");
                                cv.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                                cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));
                                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                                os = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                Objects.requireNonNull(os);

                                Toast.makeText(LatestDetailActivity.this, "Wallpaper saved successfully", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LatestDetailActivity.this, "Unable to save wallpaper\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            File directory = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
                            if (!directory.exists()) {
                                directory.mkdir();
                            }
                            String fileName = URLUtil.guessFileName(selectedObject.getLargeImageURL(), null, null);
                            File wallpaperFile = new File(directory, fileName);

                            try {
                                FileOutputStream fos = new FileOutputStream(wallpaperFile);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                                String[] paths = new String[]{wallpaperFile.getAbsolutePath()};
                                String[] mimetype = new String[]{"image/*"};
                                MediaScannerConnection.scanFile(LatestDetailActivity.this, paths, mimetype, (path, uri) -> {

                                });
                                Toast.makeText(LatestDetailActivity.this, "Wallpaper saved at: " + wallpaperFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(LatestDetailActivity.this, "Unable to save wallpaper", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (permissionDeniedResponse.isPermanentlyDenied()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LatestDetailActivity.this);
                            builder.setTitle("Permission required");
                            builder.setMessage("we required this permission to download this wallpaper");
                            builder.setPositiveButton("Settings", (dialog, which) -> {
                                Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                settingsIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(settingsIntent);
                            });
                            builder.setNegativeButton("Not now", null);
                            builder.show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check());
    }
}