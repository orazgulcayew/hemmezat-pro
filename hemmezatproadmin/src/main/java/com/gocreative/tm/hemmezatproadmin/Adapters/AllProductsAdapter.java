package com.gocreative.tm.hemmezatproadmin.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Models.AllProducts;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.CategorySelectFragment;
import com.gocreative.tm.hemmezatproadmin.NavigationFragments.ProductDetailsFragment;
import com.gocreative.tm.hemmezatproadmin.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.AllProductsViewHolder> {
    Context context;
    ArrayList<AllProducts> allProductsArrayList;
    String dateToSend;

    public AllProductsAdapter(Context context, ArrayList<AllProducts> allProductsArrayList) {
        this.context = context;
        this.allProductsArrayList = allProductsArrayList;
    }

    @NonNull
    @Override
    public AllProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_list, parent, false);
        return new AllProductsViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull AllProductsViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String price = Float.toString(allProductsArrayList.get(position).getPrice());
        String firstImageUrl = allProductsArrayList.get(position).getImages().get(0);
        holder.nameView.setText(allProductsArrayList.get(position).getName());
        holder.priceView.setText(allProductsArrayList.get(position).getCurrency() + " " + price);
        holder.typeV.setText(allProductsArrayList.get(position).getType());
        holder.viewedV.setText(Long.toString(allProductsArrayList.get(position).getViewed()));

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
                bundle.putString("type",  allProductsArrayList.get(position).getType());
                bundle.putFloat("price", allProductsArrayList.get(position).getPrice());
                bundle.putBoolean("accepted", allProductsArrayList.get(position).isAccepted());
                bundle.putBoolean("is_admin", allProductsArrayList.get(position).isAdmin());
                bundle.putString("product_id",  allProductsArrayList.get(position).getProduct_id());
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

        if (allProductsArrayList.get(position).getType().equals("Haryt")){
            holder.typeV.setTextColor(holder.itemView.getContext().getColor(R.color.green_500));
        }else{
            holder.typeV.setTextColor(holder.itemView.getContext().getColor(R.color.yellow_500));
        }
    }

    @Override
    public int getItemCount() {
        return allProductsArrayList.size();
    }

    public class AllProductsViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, priceView, viewedV, typeV;
        ImageView productImageView;

        public AllProductsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.product_name);
            priceView = itemView.findViewById(R.id.product_price);
            productImageView = itemView.findViewById(R.id.product_image);
            typeV = itemView.findViewById(R.id.product_type);
            viewedV = itemView.findViewById(R.id.viewed);


        }
    }
    public static String getMonthName(String month){
        String[] monthNames = {"Ýanwar", "Fewral", "Mart",
                "Aprel", "Maý", "Iýun", "Iýul", "Awgust",
                "Sentýabr", "Oktýabr", "Noýabr", "Dekabr"};
        return monthNames[Integer.parseInt(month)-1];
    }
}
