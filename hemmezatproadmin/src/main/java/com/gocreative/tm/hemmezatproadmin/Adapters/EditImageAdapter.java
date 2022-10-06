package com.gocreative.tm.hemmezatproadmin.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Models.EditImage;
import com.gocreative.tm.hemmezatproadmin.Models.Image;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.ImageHolder> {
    Context context;
    List<EditImage> imageList;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DocumentReference reference;
    FirebaseFirestore firestore;

    public EditImageAdapter(Context context, List<EditImage> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picked_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, @SuppressLint("RecyclerView") int position) {
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReferenceFromUrl(imageList.get(position).getImageUrl());



        Picasso.get().load(imageList.get(position).getImageUrl())
                .placeholder(R.drawable.ic_image)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.deletImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(holder.itemView, imageList.get(position).getOwnerUid(), imageList.get(position).getProductId(),imageList.get(position).getImageUrl(), imageList.get(position).isAdmin(),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView, deletImg;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.picked_image);
            deletImg = itemView.findViewById(R.id.delete_image);
        }
    }

    private void showDeleteDialog(View view, String ownerUid, String productId, String url, boolean isAdmin, int pos){
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

        title.setText("Surat pozmak");
        subText.setText("Siz hakykatdanam suraty pozmak isleýärsiňizmi?");


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageReference.delete();

                reference = firestore.collection("all_products").document(productId);
                reference.update("images", FieldValue.arrayRemove(url));

                if (isAdmin){
                    reference = firestore.collection("admin").document("admin_products").collection("products").document(productId);
                }else{
                    reference = firestore.collection("users").document(ownerUid).collection("user_products").document(productId);
                }
                reference.update("images", FieldValue.arrayRemove(url));
                dialog.dismiss();

                imageList.remove(pos);
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
