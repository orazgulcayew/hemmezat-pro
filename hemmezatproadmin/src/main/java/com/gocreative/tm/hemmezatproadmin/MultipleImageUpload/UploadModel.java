package com.gocreative.tm.hemmezatproadmin.MultipleImageUpload;

import android.net.Uri;

public class UploadModel {
    String imageName;
    Uri imageUri;

    public UploadModel(){}

    public UploadModel(String imageName, Uri imageUri){
        this.imageName = imageName;
        this.imageUri = imageUri;
    }

    public String getImageName() {
        return imageName;
    }

    public Uri getImageUri() {
        return imageUri;
    }



}
