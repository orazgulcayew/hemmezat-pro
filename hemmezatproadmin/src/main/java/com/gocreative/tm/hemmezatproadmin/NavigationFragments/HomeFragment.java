package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Adapters.AdminProductsAdapter;
import com.gocreative.tm.hemmezatproadmin.Models.AllProducts;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    CollectionReference reference, categoriesReference;
    AdminProductsAdapter allProductsAdapter;
    ArrayList<AllProducts> allProductsArrayList;
    ImageCarousel carousel;
    FloatingActionButton fab;

    Query queryKey;
    ChipGroup chipGroup;
    List<String> urls;
    List<String> categories;
    List<CarouselItem> list;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        AppCompatButton goToSearchF = view.findViewById(R.id.go_to_search);

        chipGroup = view.findViewById(R.id.filter_chip);

        carousel = view.findViewById(R.id.carousel);
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.productsRecyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager vipLayoutManager = new GridLayoutManager(getContext(), 2);

        list = new ArrayList<>();
        urls = new ArrayList<>();
        categories = new ArrayList<>();

        recyclerView.setLayoutManager(vipLayoutManager);

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("admin").document("admin_products").collection("products");
        categoriesReference = firestore.collection("admin")
                .document("categories")
                .collection("main_categories");

        fab.setOnClickListener(v->{
            int firstVisibleItemIndex = vipLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (firstVisibleItemIndex > 8) {
                vipLayoutManager.smoothScrollToPosition(recyclerView,null,0);
            }
        });

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int firstVisibleItemIndex = vipLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstVisibleItemIndex > 8) {
                    fab.setVisibility(View.VISIBLE);
                }else{
                    fab.setVisibility(View.GONE);
                }
            }
        });

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                eventChangeListener();
            }
        });
        eventChangeListener();

        goToSearchF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, searchFragment)
                        .addToBackStack(HomeFragment.class.getSimpleName())
                        .commit();
                InputMethodManager imm = (InputMethodManager)   getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        getAdvertisements();

        return view;
    }

    private void eventChangeListener() {
        for (int i=0; i<chipGroup.getChildCount();i++){
            Chip chip = (Chip)chipGroup.getChildAt(i);
            if (chip.isChecked()){
                if (chip.getText().toString().equals("Brendlar")){
                    queryKey = reference
                            .orderBy("price", Query.Direction.DESCENDING);

                }
                else if (chip.getText().toString().equals("Elýeterli")){
                    queryKey = reference
                            .orderBy("price", Query.Direction.ASCENDING);
                }
                else if (chip.getText().toString().equals("Täze goýulanlar") || chip.getText().toString().equals("Ählisi")){
                    queryKey = reference
                            .orderBy("date_created", Query.Direction.DESCENDING);

                }
                else if (chip.getText().toString().equals("Köne goýulanlar")){
                    queryKey = reference
                            .orderBy("date_created", Query.Direction.ASCENDING);
                }
                else if (chip.getText().equals("Auto Shaylar")){
                    queryKey = reference
                            .orderBy("date_created", Query.Direction.DESCENDING)
                            .whereEqualTo("category", "Auto Shaylar");
                }
                // Update -------------1.0.1
                else{
                    queryKey = reference
                            .orderBy("date_created", Query.Direction.DESCENDING)
                            .whereEqualTo("category", chip.getText());
                }
            }
        }

        queryKey.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                Log.d("aaddddddddd", "--------------------------------------------------------------------------------: " + allProductsArrayList);

            }
        });


        allProductsArrayList = new ArrayList<AllProducts>();
        allProductsAdapter = new AdminProductsAdapter(getContext(), allProductsArrayList);
        allProductsAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        recyclerView.setAdapter(allProductsAdapter);
    }
    private void getAdvertisements(){
        DocumentReference reference = firestore.collection("admin").document("reklamalar");

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    urls = (List<String>) task.getResult().get("images");
                    if (urls.size() != 0){
                        for (String url: urls){
                            list.add(new CarouselItem(url));
                        }
                        carousel.setData(list);
                    }
                }else{
                    Log.d("Advertisement getting", "onComplete: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("asdasdasdasdas", "onViewStateRestored: " + "WE are call"  + chipGroup.getCheckedChipId());

//        Chip chip = chipGroup.findViewById(chipGroup.getCheckedChipId());
//        queryKey = reference
//                .orderBy("date_created", Query.Direction.DESCENDING)
//                .whereEqualTo("category", chip.getText());
//
//        queryKey.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null){
//                    Log.d("firebase firestore", "onEvent: " + error.getMessage());
//                    return;
//                }
//                for (DocumentChange documentChange : value.getDocumentChanges()){
//                    if (documentChange.getType() == DocumentChange.Type.ADDED){
//                        allProductsArrayList.add(documentChange.getDocument().toObject(AllProducts.class));
//                    }
//                    allProductsAdapter.notifyDataSetChanged();
//
//                }
//            }
//        });
//
//        allProductsArrayList = new ArrayList<AllProducts>();
//        allProductsAdapter = new AdminProductsAdapter(getContext(), allProductsArrayList);
//        allProductsAdapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
//        recyclerView.setAdapter(allProductsAdapter);
    }
}