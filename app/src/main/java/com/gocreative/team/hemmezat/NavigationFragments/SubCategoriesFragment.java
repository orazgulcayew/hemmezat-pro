package com.gocreative.team.hemmezat.NavigationFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.team.hemmezat.Adapters.SubCategoriesAdapter;
import com.gocreative.team.hemmezat.Models.Category;
import com.gocreative.team.hemmezat.Models.SubCategory;
import com.gocreative.tm.hemmezat.Constants;
import com.gocreative.tm.hemmezat.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class SubCategoriesFragment extends Fragment {
    FirebaseFirestore firestore;
    DocumentReference documentReference;
    SubCategoriesAdapter categoriesAdapter;
    RecyclerView recyclerView;
    ArrayList<SubCategory> categoryArrayList;
    ProgressDialog progressDialog;
    String categoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_categories, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Biraz garaşyň...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        assert getArguments() != null;
        categoryName = getArguments().getString("categoryName");

        recyclerView = view.findViewById(R.id.categories_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        documentReference=firestore.collection(Constants.ADMIN).document(Constants.CATEGORIES).collection(Constants.MAIN_CATEGORIES).document(categoryName);

        categoryArrayList = new ArrayList<SubCategory>();
        categoriesAdapter = new SubCategoriesAdapter(getContext(), categoryArrayList);

        recyclerView.setAdapter(categoriesAdapter);
        eventChangeListener();


        return view;
    }
    private void eventChangeListener() {
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Log.d("firebase firestore", "onEvent: " + error.getMessage());
                    return;
                }
                Category category = value.toObject(Category.class);

                for (String name : category.getSub_categories()){
                    categoryArrayList.add(new SubCategory(name, categoryName));
                    categoriesAdapter.notifyDataSetChanged();
                }

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
    }
}