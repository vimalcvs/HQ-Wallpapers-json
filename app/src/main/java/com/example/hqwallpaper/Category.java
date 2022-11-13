package com.example.hqwallpaper;

import java.io.Serializable;

public class Category implements Serializable {

    String name;
    String imageURL;

    public Category(String name, String imageURL) {
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

}
