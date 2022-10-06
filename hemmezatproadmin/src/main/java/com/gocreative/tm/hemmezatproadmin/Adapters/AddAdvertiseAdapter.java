package com.gocreative.tm.hemmezatproadmin.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.tm.hemmezatproadmin.Models.Image;
import com.gocreative.tm.hemmezatproadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddAdvertiseAdapter extends RecyclerView.Adapter<AddAdvertiseAdapter.ImageHolder> {
    Context context;
    List<Image> imageList;
    String imageUrl;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DocumentReference reference;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    public AddAdvertiseAdapter(Context context, List<Image> imageList) {
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
        imageUrl = imageList.get(position).getImageUrl();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        progressDialog = new ProgressDialog(holder.itemView.getContext());
        progressDialog.setMessage("Biraz garaşyň...");

        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.ic_image)
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.deletImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(holder.itemView, imageUrl, position);
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

    private void showDeleteDialog(View view, String url, int pos){
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
                progressDialog.show();
                storageReference = firebaseStorage.getReferenceFromUrl(imageUrl);

                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
                dialog.dismiss();

                reference = firestore.collection("admin").document("reklamalar");
                reference.update("images", FieldValue.arrayRemove(url)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(view.getContext(), "Reklama üstünlikli pozuldy!", Toast.LENGTH_SHORT).show();
                            imageList.remove(pos);
                            notifyItemRemoved(pos);
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(view.getContext(), "Näsazlyk ýüze çykdy!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


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
