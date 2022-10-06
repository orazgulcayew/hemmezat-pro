package com.gocreative.tm.hemmezat.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezat.Models.Category;
import com.gocreative.tm.hemmezat.Models.SubCategory;
import com.gocreative.tm.hemmezat.NavigationFragments.AddProductFragment;
import com.gocreative.tm.hemmezat.NavigationFragments.CategorySelectFragment;
import com.gocreative.tm.hemmezat.NavigationFragments.SubCategoriesFragment;
import com.gocreative.tm.hemmezat.R;

import java.util.ArrayList;
import java.util.List;


public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.CategoriesViewHolder> {

    Context context;
    ArrayList<SubCategory> categoryArrayList;

    public SubCategoriesAdapter(Context context, ArrayList<SubCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_list, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        SubCategory category = categoryArrayList.get(position);
        holder.textView.setText(category.getMain_name());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String categoryName = category.getMain_name();
                String subCategoryName = category.getUpper_main_name();
                bundle.putString("subCategoryName", categoryName);
                bundle.putString("categoryName", subCategoryName);
                AddProductFragment addProductFragment = new AddProductFragment();
                addProductFragment.setArguments(bundle);

                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, addProductFragment)
                        .addToBackStack(CategorySelectFragment.class.getSimpleName())
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout relativeLayout;
        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.main_category_holder);
            textView = itemView.findViewById(R.id.main_category_name);
        }
    }
}