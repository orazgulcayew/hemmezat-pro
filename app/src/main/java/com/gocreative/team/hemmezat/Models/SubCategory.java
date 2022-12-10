package com.gocreative.team.hemmezat.Models;

import androidx.annotation.Keep;

@Keep
public class SubCategory {
    private String main_name;

    public String getUpper_main_name() {
        return upper_main_name;
    }

    public void setUpper_main_name(String upper_main_name) {
        this.upper_main_name = upper_main_name;
    }

    private String upper_main_name;

    public SubCategory(){
        //Empty Constructor
    }


    public String getMain_name() {
        return main_name;
    }

    public void setMain_name(String main_name) {
        this.main_name = main_name;
    }

    public SubCategory(String main_name, String upper_main_name){
        this.main_name = main_name;
        this.upper_main_name = upper_main_name;
    }
}
