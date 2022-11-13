package com.example.hqwallpaper;

import java.io.Serializable;

public class Wallpaper implements Serializable {
    int id;
    String pageURL;
    String previewURL;
    String webformatURL;
    String largeImageURL;
    String imageSize;
    int views;
    int downloads;

    public Wallpaper() {
    }

    public Wallpaper(int id, String pageURL, String previewURL, String webformatURL, String largeImageURL, String imageSize, int views, int downloads) {
        this.id = id;
        this.pageURL = pageURL;
        this.previewURL = previewURL;
        this.webformatURL = webformatURL;
        this.largeImageURL = largeImageURL;
        this.imageSize = imageSize;
        this.views = views;
        this.downloads = downloads;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public void setWebformatURL(String webformatURL) {
        this.webformatURL = webformatURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public String getImageSize() {
        return imageSize;
    }

    public void setImageSize(String imageSize) {
        this.imageSize = imageSize;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }
}
