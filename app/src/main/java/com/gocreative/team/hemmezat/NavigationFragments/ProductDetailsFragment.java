package com.gocreative.team.hemmezat.NavigationFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.team.hemmezat.Adapters.ImageAdapter;
import com.gocreative.team.hemmezat.Models.Image;
import com.gocreative.tm.hemmezat.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment {

    List<Image> imageList;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    String name, location, date, info, ownerUid, category, subCategory, phone, priceS, productUid, currency;
    float price;
    long viewed;
    boolean isAdmin;
    List<String> urls;
    ExtendedFloatingActionButton actionButton;

    TextView nameV, locationV, dateV, infoV, categoryV, phoneV, priceV, nameV2, priceV2, viewedV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewProductDetails);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        imageList = new ArrayList<>();

        nameV = view.findViewById(R.id.name);
        locationV = view.findViewById(R.id.location);
        dateV = view.findViewById(R.id.date_created);
        infoV = view.findViewById(R.id.product_info);
        categoryV = view.findViewById(R.id.category_view);
        phoneV = view.findViewById(R.id.phone_number);
        priceV = view.findViewById(R.id.price);
        priceV2 = view.findViewById(R.id.price_2);
        nameV2 = view.findViewById(R.id.name_2);
        actionButton = view.findViewById(R.id.call_phone);
        viewedV = view.findViewById(R.id.viewed);

        viewed = getArguments().getLong("viewed");

        name = getArguments().getString("name");
        location = getArguments().getString("location");
        date = getArguments().getString("date");
        info = getArguments().getString("info");
        ownerUid = getArguments().getString("owner_uid");
        category = getArguments().getString("category");
        subCategory = getArguments().getString("sub_category");
        phone = getArguments().getString("phone");
        price = getArguments().getFloat("price");
        urls = getArguments().getStringArrayList("imageUrls");
        productUid = getArguments().getString("product_uid");
        priceS = Float.toString(price);
        isAdmin = getArguments().getBoolean("is_admin");
        currency = getArguments().getString("currency");

        addViewed2(ownerUid, productUid);

        nameV.setText(name);
        locationV.setText(location);
        dateV.setText(date);
        infoV.setText(info);
        categoryV.setText(category + "/" + subCategory);
        phoneV.setText(phone);
        priceV.setText(currency + " " + priceS);
        priceV2.setText(currency + " " + priceS);
        nameV2.setText(name);

        String v = Long.toString(viewed);
        viewedV.setText(v);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });

        for (String url: urls){
            imageList.add(new Image(url));
        }

        imageAdapter = new ImageAdapter(getContext(), imageList);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();

        return view;
    }
    private void addViewed2(String ownerUid, String productUid){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FieldValue increment = FieldValue.increment(1);
        DocumentReference documentReference;

        if (isAdmin){
            documentReference = firestore.collection("admin").document("admin_products").collection("products").document(productUid);
        }else{
            documentReference = firestore.collection("users").document(ownerUid).collection("user_products").document(productUid);
        }

        documentReference.update("viewed", increment);

        DocumentReference documentReference2 = firestore.collection("all_products").document(productUid);
        documentReference2.update("viewed", increment);


    }

}