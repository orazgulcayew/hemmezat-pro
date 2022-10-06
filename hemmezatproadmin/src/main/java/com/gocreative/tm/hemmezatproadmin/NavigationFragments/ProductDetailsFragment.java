package com.gocreative.tm.hemmezatproadmin.NavigationFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Adapters.ImageAdapter;
import com.gocreative.tm.hemmezatproadmin.Models.Image;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment {

    List<Image> imageList;
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    ImageView accept, deny;
    LinearLayout linearLayout;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;
    DocumentReference reference;

    String name, location, date, info, ownerUid, category, subCategory, phone, priceS, productId, type, currency;
    float price;
    boolean isAccepted, isAdmin;
    List<String> urls;
    long viewed;

    ExtendedFloatingActionButton actionButton;

    TextView nameV, locationV, dateV, infoV, categoryV, phoneV, priceV, nameV2, priceV2, viewedV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
        imageList = new ArrayList<>();

        linearLayout = view.findViewById(R.id.linear_layout);
        accept = view.findViewById(R.id.accept);
        deny = view.findViewById(R.id.deny);
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

        name = getArguments().getString("name");
        location = getArguments().getString("location");
        date = getArguments().getString("date");
        info = getArguments().getString("info");
        ownerUid = getArguments().getString("owner_uid");
        productId = getArguments().getString("product_id");
        category = getArguments().getString("category");
        subCategory = getArguments().getString("sub_category");
        phone = getArguments().getString("phone");
        currency = getArguments().getString("currency");
        price = getArguments().getFloat("price");
        urls = getArguments().getStringArrayList("imageUrls");
        isAccepted = getArguments().getBoolean("accepted");
        isAdmin = getArguments().getBoolean("is_admin");
        type = getArguments().getString("type");
        viewedV = view.findViewById(R.id.viewed);
        viewed = getArguments().getLong("viewed");

        String v = Long.toString(viewed);
        viewedV.setText(v);

        priceS = Float.toString(price);

        nameV.setText(name);
        locationV.setText(location);
        dateV.setText(date);
        infoV.setText(info);
        categoryV.setText(category + "/" + subCategory);
        phoneV.setText(phone);
        priceV.setText(currency + " " + priceS);
        priceV2.setText(currency + " " + priceS);
        nameV2.setText(name);

        if (isAccepted){
            accept.setImageResource(R.drawable.ic_edit);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccepted){
                    Bundle bundle = new Bundle();

                    bundle.putStringArrayList("imageUrls", (ArrayList<String>) urls);
                    bundle.putString("name", name);
                    bundle.putString("category",  category);
                    bundle.putString("info",  info);
                    bundle.putString("location",  location);
                    bundle.putString("phone",  phone);
                    bundle.putString("sub_category",  subCategory);
                    bundle.putString("owner_uid",  ownerUid);
                    bundle.putString("type",  type);
                    bundle.putFloat("price", price);
                    bundle.putBoolean("accepted", isAccepted);
                    bundle.putBoolean("is_admin", isAdmin);
                    bundle.putString("product_id",  productId);
                    bundle.putLong("viewed", viewed);
                    bundle.putString("currency", currency);


                    EditFragment editFragment = new EditFragment();
                    editFragment.setArguments(bundle);

                    FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.fragment_container, editFragment)
                            .addToBackStack(ProductDetailsFragment.class.getSimpleName())
                            .commit();
                }else{
                    acceptUserRequest(ownerUid, productId);
                }
            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(v, ownerUid, productId);
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
    private void showDeleteDialog(View view, String ownerUid, String productId){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_dialog);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        Button yes = dialog.findViewById(R.id.dialog_yes);
        Button no = dialog.findViewById(R.id.dialog_no);

        TextView title = dialog.findViewById(R.id.alert_dialog_title);
        TextView subText = dialog.findViewById(R.id.alert_dialog_sub_text);

        title.setText("Bildiriş pozmak");
        subText.setText("Siz hakykatdanam bildirişi pozmak isleýärsiňizmi?");


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denyRequest(ownerUid, productId);
                dialog.dismiss();

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void denyRequest(String ownerUid, String productId) {
        reference = firestore.collection("all_products").document(productId);

        for (String url: urls){
            storageReference = storage.getReferenceFromUrl(url);
            storageReference.delete();
        }
        reference.delete();
        if (isAdmin){
            reference = firestore.collection("admin").document("admin_products").collection("products").document(productId);
        }else{
            reference = firestore.collection("users").document(ownerUid).collection("user_products").document(productId);
        }
        reference.delete();

        linearLayout.setVisibility(View.GONE);
    }

    private void acceptUserRequest(String ownerUid, String productId) {
        reference = firestore.collection("all_products").document(productId);
        reference.update("accepted", true);

        reference = firestore.collection("users").document(ownerUid).collection("user_products").document(productId);
        reference.update("accepted", true);

        linearLayout.setVisibility(View.GONE);

    }
}