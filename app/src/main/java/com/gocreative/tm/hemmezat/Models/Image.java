package com.gocreative.tm.hemmezat.Models;

import androidx.annotation.Keep;

@Keep
public class Image {
    private String imageUrl;

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
