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

import com.gocreative.team.hemmezat.Adapters.CategoriesAdapter;
import com.gocreative.team.hemmezat.Models.Category;
import com.gocreative.tm.hemmezat.Constants;
import com.gocreative.tm.hemmezat.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategorySelectFragment extends Fragment {
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    CategoriesAdapter categoriesAdapter;
    RecyclerView recyclerView;
    ArrayList<Category> categoryArrayList;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_select, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Biraz garaşyň...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        recyclerView = view.findViewById(R.id.categories_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        collectionReference=firestore.collection(Constants.ADMIN).document(Constants.CATEGORIES).collection(Constants.MAIN_CATEGORIES);;

        categoryArrayList = new ArrayList<Category>();
        categoriesAdapter = new CategoriesAdapter(getContext(), categoryArrayList);

        recyclerView.setAdapter(categoriesAdapter);
        eventChangeListener();

        return view;
    }

    private void eventChangeListener() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Log.d("firebase firestore", "onEvent: " + error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        categoryArrayList.add(documentChange.getDocument().toObject(Category.class));
                    }
                    categoriesAdapter.notifyDataSetChanged();

                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }


}