package com.gocreative.tm.hemmezat.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gocreative.tm.hemmezat.Models.Category;
import com.gocreative.tm.hemmezat.NavigationFragments.AddProductFragment;
import com.gocreative.tm.hemmezat.NavigationFragments.CategorySelectFragment;
import com.gocreative.tm.hemmezat.NavigationFragments.SubCategoriesFragment;
import com.gocreative.tm.hemmezat.R;

import java.util.ArrayList;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    Context context;
    ArrayList<Category> categoryArrayList;

    public CategoriesAdapter(Context context, ArrayList<Category> categoryArrayList) {
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
        Category category = categoryArrayList.get(position);
        holder.textView.setText(category.getMain_name());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String categoryName = category.getMain_name();
                bundle.putString("categoryName", categoryName);

                SubCategoriesFragment subCategoriesFragment = new SubCategoriesFragment();
                subCategoriesFragment.setArguments(bundle);
                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, subCategoriesFragment)
                        .addToBackStack(CategorySelectFragment.class.getSimpleName())
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout relativeLayout;
        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.main_category_holder);
            textView = itemView.findViewById(R.id.main_category_name);
        }
    }
}