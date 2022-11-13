package com.example.hqwallpaper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context) {
        super(context, "wallpaper.db", null, 1);
    }

    private static final String TABLE_FAVORITES = "tbl_favorites";

    private static final String KEY_FAVORITES_ID = "id";
    private static final String KEY_FAVORITES_PageURL = "pageURL";
    private static final String KEY_FAVORITES_PreviewURL = "previewURL";
    private static final String KEY_FAVORITES_WebFormatURL = "webformatURL";
    private static final String KEY_FAVORITES_LargeImageURL = "largeImageURL";
    private static final String KEY_FAVORITES_ImageSize = "imageSize";
    private static final String KEY_FAVORITES_Views = "views";
    private static final String KEY_FAVORITES_Downloads = "downloads";

    private static final String CREATE_TABLE_FAVORITES = "CREATE TABLE " +TABLE_FAVORITES+ "( " +
                                                        KEY_FAVORITES_ID + " INTEGER PRIMARY KEY," +
                                                        KEY_FAVORITES_PageURL + " TEXT," +
                                                        KEY_FAVORITES_PreviewURL + " TEXT," +
                                                        KEY_FAVORITES_WebFormatURL + " TEXT," +
                                                        KEY_FAVORITES_LargeImageURL + " TEXT," +
                                                        KEY_FAVORITES_ImageSize + " TEXT," +
                                                        KEY_FAVORITES_Views + " INTEGER," +
                                                        KEY_FAVORITES_Downloads + " INTEGER" +
                                                        " )";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }
   public long addToFavoriteWallpaper(Wallpaper wallpaper) {

        if (isFavoriteWallpaper(wallpaper)) {
            return -1;
        } else {

            ContentValues cv = new ContentValues();
            cv.put(KEY_FAVORITES_ID, wallpaper.getId());
            cv.put(KEY_FAVORITES_Downloads, wallpaper.getDownloads());
            cv.put(KEY_FAVORITES_Views, wallpaper.getViews());
            cv.put(KEY_FAVORITES_PageURL, wallpaper.getPageURL());
            cv.put(KEY_FAVORITES_ImageSize, wallpaper.getImageSize());
            cv.put(KEY_FAVORITES_PreviewURL, wallpaper.getPreviewURL());
            cv.put(KEY_FAVORITES_WebFormatURL, wallpaper.getWebformatURL());
            cv.put(KEY_FAVORITES_LargeImageURL, wallpaper.getLargeImageURL());

            SQLiteDatabase db = getWritableDatabase();
            return db.insert(TABLE_FAVORITES, null, cv);
        }
   }
   public int deleteFavoriteWallpaper(Wallpaper wallpaper) {
        SQLiteDatabase db = getWritableDatabase();
       return db.delete(TABLE_FAVORITES,   KEY_FAVORITES_ID + "=" + wallpaper.getId(), null);
   }
   public int deleteAllFavorites(){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_FAVORITES, "1", null);
   }
   public boolean isFavoriteWallpaper(Wallpaper wallpaper){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_FAVORITES_ID + "=" + wallpaper.getId(), null);
        int cursorCount = cursor.getCount();
        cursor.close();
       return cursorCount > 0;
   }

   @SuppressLint("Range")
   public ArrayList<Wallpaper> getAllFavorites(){
        ArrayList<Wallpaper> wallpaperList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);
        while (cursor.moveToNext()) {
            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setId(cursor.getInt(cursor.getColumnIndex(KEY_FAVORITES_ID)));
            wallpaper.setLargeImageURL(cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_LargeImageURL)));
            wallpaper.setPageURL(cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_PageURL)));
            wallpaper.setImageSize(cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_ImageSize)));
            wallpaper.setPreviewURL(cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_PreviewURL)));
            wallpaper.setWebformatURL(cursor.getString(cursor.getColumnIndex(KEY_FAVORITES_WebFormatURL)));
            wallpaper.setDownloads(cursor.getInt(cursor.getColumnIndex(KEY_FAVORITES_Downloads)));
            wallpaper.setViews(cursor.getInt(cursor.getColumnIndex(KEY_FAVORITES_Views)));

            wallpaperList.add(wallpaper);
        }
        cursor.close();
        return wallpaperList;
   }
}
