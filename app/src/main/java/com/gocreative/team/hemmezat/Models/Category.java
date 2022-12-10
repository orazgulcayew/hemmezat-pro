package com.gocreative.team.hemmezat.Models;

import androidx.annotation.Keep;

import java.util.List;
@Keep
public class Category {
    private List<String> sub_categories;
    private String main_name;

    public Category(){
        //Empty Constructor
    }


    public String getMain_name() {
        return main_name;
    }

    public void setMain_name(String main_name) {
        this.main_name = main_name;
    }

    public Category(List<String> sub_categories, String main_name){
        this.sub_categories = sub_categories;
        this.main_name = main_name;
    }

    public List<String> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(List<String> sub_categories) {
        this.sub_categories = sub_categories;
    }
}
