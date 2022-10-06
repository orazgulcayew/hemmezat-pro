package com.gocreative.tm.hemmezatproadmin.Models;

import com.google.firebase.Timestamp;

import java.util.List;

public class AllProducts {
    private String category;
    private String sub_category;
    private String phone_number;
    private String owner_uid;
    private String name;
    private String product_id;
    private String type;
    private String location;
    private boolean accepted;
    private String info;
    private float price;
    private Timestamp date_created;
    private List<String> images;
    private boolean admin;
    private long viewed;
    private String currency;
    public AllProducts(){ }

    public AllProducts(String category, String sub_category, String phone_number,
                       String owner_uid, String name, String location, String info,
                       Timestamp date_created, List<String> images, float price) {
        this.category = category;
        this.sub_category = sub_category;
        this.phone_number = phone_number;
        this.owner_uid = owner_uid;
        this.name = name;
        this.location = location;
        this.info = info;
        this.date_created = date_created;
        this.images = images;
        this.price = price;

    }

    public long getViewed() {
        return viewed;
    }

    public void setViewed(long viewed) {
        this.viewed = viewed;
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getCurrency() {
        return currency;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOwner_uid() {
        return owner_uid;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Timestamp getDate_created() {
        return date_created;
    }

    public void setDate_created(Timestamp date_created) {
        this.date_created = date_created;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
