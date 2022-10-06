package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gocreative.tm.hemmezatproadmin.Adapters.AllProductsAdapter;
import com.gocreative.tm.hemmezatproadmin.Models.AllProducts;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class WaitinProductsFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    CollectionReference reference;
    AllProductsAdapter allProductsAdapter;
    ArrayList<AllProducts> allProductsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waitin_products, container, false);

        recyclerView = view.findViewById(R.id.category_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("all_products");

        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter = new AllProductsAdapter(getContext(), allProductsArrayList);

        recyclerView.setAdapter(allProductsAdapter);
        eventChangeListener();

        return view;
    }

    private void eventChangeListener() {
        reference.whereEqualTo("accepted", false).orderBy("date_created", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d("firebase firestore", "onEvent: " + error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        allProductsArrayList.add(documentChange.getDocument().toObject(AllProducts.class));
                    }
                    allProductsAdapter.notifyDataSetChanged();

                }
            }
        });
    }
}