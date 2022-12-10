package com.gocreative.team.hemmezat.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.team.hemmezat.Models.AllProducts;
import com.gocreative.team.hemmezat.NavigationFragments.CategorySelectFragment;
import com.gocreative.team.hemmezat.NavigationFragments.ProductDetailsFragment;
import com.gocreative.team.hemmezat.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyProductsAdapter extends RecyclerView.Adapter<MyProductsAdapter.AllProductsViewHolder> {
    Context context;
    ArrayList<AllProducts> allProductsArrayList;
    String dateToSend;
    DocumentReference documentReference;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference storageReference;

    public MyProductsAdapter(Context context, ArrayList<AllProducts> allProductsArrayList) {
        this.context = context;
        this.allProductsArrayList = allProductsArrayList;
    }

    @NonNull
    @Override
    public MyProductsAdapter.AllProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_product_item_list, parent, false);
        return new AllProductsViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyProductsAdapter.AllProductsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String price = Float.toString(allProductsArrayList.get(position).getPrice());
        String firstImageUrl = allProductsArrayList.get(position).getImages().get(0);
        holder.nameView.setText(allProductsArrayList.get(position).getName());
        holder.priceView.setText(allProductsArrayList.get(position).getCurrency() + " " + price);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        String productId = allProductsArrayList.get(position).getProduct_id();

        Picasso.get().load(firstImageUrl)
                .placeholder(R.drawable.ic_image)
                .fit()
                .centerCrop()
                .into(holder.productImageView);

        // Goyulan senesi
        SimpleDateFormat year = new SimpleDateFormat("yy", Locale.getDefault());
        SimpleDateFormat month = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat day = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat today = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        Instant now = Instant.now();
        Date yesterday = Date.from(now.minus(1, ChronoUnit.DAYS));
        String yesterdatDate = today.format(yesterday);
        Date datePub = allProductsArrayList.get(position).getDate_created().toDate();
        Date todayDate = new Date();
        String _today = today.format(todayDate);
        String pubDate = today.format(datePub);
        String setDate = "• " + getMonthName(month.format(datePub)) + " " + day.format(datePub) + ", " + year.format(datePub) + "ý";
        if (_today.equals(pubDate)){
            setDate = "• şu gün";
            dateToSend = setDate;
        }else if (yesterdatDate.equals(pubDate)){
            setDate = "• düýn";
            dateToSend = setDate;
        }
        else {
            dateToSend = setDate;
        }

        String finalSetDate = setDate;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putStringArrayList("imageUrls", (ArrayList<String>) allProductsArrayList.get(position).getImages());
                bundle.putString("date", finalSetDate);
                bundle.putString("name", allProductsArrayList.get(position).getName());
                bundle.putString("category",  allProductsArrayList.get(position).getCategory());
                bundle.putString("info",  allProductsArrayList.get(position).getInfo());
                bundle.putString("location",  allProductsArrayList.get(position).getLocation());
                bundle.putString("phone",  allProductsArrayList.get(position).getPhone_number());
                bundle.putString("sub_category",  allProductsArrayList.get(position).getSub_category());
                bundle.putString("owner_uid",  allProductsArrayList.get(position).getOwner_uid());
                bundle.putFloat("price", allProductsArrayList.get(position).getPrice());
                bundle.putString("product_uid",  allProductsArrayList.get(position).getProduct_id());
                bundle.putBoolean("is_admin",  allProductsArrayList.get(position).isAdmin());
                bundle.putLong("viewed", allProductsArrayList.get(position).getViewed());
                bundle.putString("currency",  allProductsArrayList.get(position).getCurrency());



                ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                productDetailsFragment.setArguments(bundle);

                FragmentManager manager = ((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, productDetailsFragment)
                        .addToBackStack(CategorySelectFragment.class.getSimpleName())
                        .commit();
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(holder.itemView, allProductsArrayList.get(position).getOwner_uid(), productId, position, allProductsArrayList.get(position).getImages());

            }
        });

        boolean state = allProductsArrayList.get(position).isAccepted();

        if (state){
            holder.stateView.setTextColor(holder.itemView.getContext().getColor(R.color.green_500));
            holder.stateView.setText("Goýuldy");
        }else{
            holder.stateView.setTextColor(holder.itemView.getContext().getColor(R.color.yellow_500));
            holder.stateView.setText("Garaşylýar...");
        }

    }

    @Override
    public int getItemCount() {
        return allProductsArrayList.size();
    }

    public class AllProductsViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, priceView, stateView;
        ImageView productImageView;
        LinearLayout linearLayout;

        public AllProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.product_name);
            priceView = itemView.findViewById(R.id.product_price);
            productImageView = itemView.findViewById(R.id.product_image);
            linearLayout = itemView.findViewById(R.id.delete_product);
            stateView = itemView.findViewById(R.id.state_product);
        }
    }
    public static String getMonthName(String month){
        String[] monthNames = {"Ýanwar", "Fewral", "Mart",
                "Aprel", "Maý", "Iýun", "Iýul", "Awgust",
                "Sentýabr", "Oktýabr", "Noýabr", "Dekabr"};
        return monthNames[Integer.parseInt(month)-1];
    }

    private void showDeleteDialog(View view, String ownerUid, String productId, int pos, List<String> urls){
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
                documentReference = firestore.collection("all_products").document(productId);
                documentReference.delete();
                documentReference = firestore.collection("users").document(ownerUid).collection("user_products").document(productId);
                documentReference.delete();

                for (String url: urls){
                    storageReference = storage.getReferenceFromUrl(url);
                    storageReference.delete();
                }

                dialog.dismiss();

                allProductsArrayList.remove(pos);
                notifyItemRemoved(pos);
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
}
