package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Adapters.AllProductsAdapter;
import com.gocreative.tm.hemmezatproadmin.Models.AllProducts;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {
    EditText searchView;
    Query queryKey;

    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    CollectionReference reference, categoriesReference;
    AllProductsAdapter allProductsAdapter;
    ArrayList<AllProducts> allProductsArrayList;
    List<String> categories;
    ChipGroup chipGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.search_view);
        searchView.requestFocus();
        chipGroup = view.findViewById(R.id.filter_chip);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("all_products");
        categoriesReference = firestore.collection("admin")
                .document("categories")
                .collection("main_categories");

        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter = new AllProductsAdapter(getContext(), allProductsArrayList);
        allProductsAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT);
        categories = new ArrayList<>();
        recyclerView.setAdapter(allProductsAdapter);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchProduct(s.toString().toLowerCase(Locale.ROOT));
            }
        });

        searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchProduct(searchView.getText().toString().toLowerCase(Locale.ROOT));
                    hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void searchProduct(String toString) {
        for (int i=0; i<chipGroup.getChildCount();i++){
            Chip chip = (Chip)chipGroup.getChildAt(i);
            if (chip.isChecked()){
                if (chip.getText().toString().equals("Brendlar")){
                    queryKey = reference.whereEqualTo("accepted", true)
                            .orderBy("price", Query.Direction.DESCENDING);

                }
                else if (chip.getText().toString().equals("Elýeterli")){

                    queryKey = reference.whereEqualTo("accepted", true)
                            .orderBy("price", Query.Direction.ASCENDING);
                }
                else if (chip.getText().toString().equals("Täze goýulanlar") || chip.getText().toString().equals("Ählisi")){

                    queryKey = reference.whereEqualTo("accepted", true)
                            .orderBy("date_created", Query.Direction.DESCENDING);

                }
                else if (chip.getText().toString().equals("Köne goýulanlar")){

                    queryKey = reference.whereEqualTo("accepted", true)
                            .orderBy("date_created", Query.Direction.ASCENDING);
                }

                // Update -------------1.0.1
                else{
                    queryKey = reference
                            .orderBy("date_created", Query.Direction.DESCENDING)
                            .whereEqualTo("category", chip.getText());
                }
            }
        }

        queryKey.whereArrayContains("search_key", toString)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT);

        allProductsAdapter = new AllProductsAdapter(getContext(), allProductsArrayList);

        recyclerView.setAdapter(allProductsAdapter);

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        categoriesReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                    if (snapshot.exists()){
                        String category = snapshot.getString("main_name");
                        categories.add(category);
                    }else{
                        Log.d("kjkjkj", "onSuccess: " + "failed");
                    }
                }
                for (String category : categories) {

                    Chip mChip = (Chip) getLayoutInflater().inflate(R.layout.item_chip_category, null, false);
                    mChip.setText(category);
                    int paddingDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 10,
                            getResources().getDisplayMetrics()
                    );
                    mChip.setPadding(paddingDp, 0, paddingDp, 0);
                    mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                        }
                    });
                    chipGroup.addView(mChip);
                }
            }
        });
    }
}