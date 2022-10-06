package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Adapters.MyProductsAdapter;
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

public class MyProductsFragment extends Fragment {
    String userUid;
    FirebaseFirestore firestore;
    CollectionReference collectionReference;
    RecyclerView recyclerView;
    MyProductsAdapter allProductsAdapter;
    ArrayList<AllProducts> allProductsArrayList;

    int itemSize;
    LinearLayout noData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);

        userUid = getArguments().getString("userUid");
        noData = view.findViewById(R.id.no_data);

        recyclerView = view.findViewById(R.id.my_products_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("admin").document("admin_products").collection("products");

        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter = new MyProductsAdapter(getContext(), allProductsArrayList);

        recyclerView.setAdapter(allProductsAdapter);
        eventChangeListener();


        return view;
    }

    private void eventChangeListener() {
        collectionReference.orderBy("date_created", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.d("firebase firestore", "onEvent: " + error.getMessage());
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        itemSize++;
                        allProductsArrayList.add(documentChange.getDocument().toObject(AllProducts.class));
                        allProductsAdapter.notifyDataSetChanged();
                    }
                }
                if (itemSize == 0){
                    noData.setVisibility(View.VISIBLE);
                }else{
                    noData.setVisibility(View.GONE);
                }
            }
        });
    }
}