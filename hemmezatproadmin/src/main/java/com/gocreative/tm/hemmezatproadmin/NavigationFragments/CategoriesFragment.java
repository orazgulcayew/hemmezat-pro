package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CategoriesFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    CollectionReference reference;
    AllProductsAdapter allProductsAdapter;
    ArrayList<AllProducts> allProductsArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_categories, container, false);

        recyclerView = view.findViewById(R.id.category_fragment_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppCompatButton goToSearchF = view.findViewById(R.id.go_to_search);
        goToSearchF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, searchFragment)
                        .commit();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("all_products");

        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter = new AllProductsAdapter(getContext(), allProductsArrayList);
        allProductsAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        recyclerView.setAdapter(allProductsAdapter);
        eventChangeListener();

        return view;
    }

    private void eventChangeListener() {
        reference.whereEqualTo("accepted", true).orderBy("date_created", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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