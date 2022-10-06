package com.gocreative.tm.hemmezatproadmin.Models;

public class EditImage {
    private String imageUrl;
    private String ownerUid;
    private String productId;
    private boolean isAdmin;

    public EditImage(String imageUrl, String ownerUid, String productId, boolean isAdmin) {
        this.imageUrl = imageUrl;
        this.ownerUid = ownerUid;
        this.productId = productId;
        this.isAdmin = isAdmin;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
