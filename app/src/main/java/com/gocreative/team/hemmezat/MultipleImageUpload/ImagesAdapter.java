package com.gocreative.team.hemmezat.MultipleImageUpload;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gocreative.team.hemmezat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    Context context;
    List<UploadModel> imagesList;

    public ImagesAdapter(Context context, List<UploadModel> imagesList){
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picked_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.xIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesList.remove(position);
                notifyDataSetChanged();
            }
        });

        Picasso.get().load(imagesList.get(position).getImageUri())
                .fit()
                .centerCrop()
                .into(holder.pickedImage);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView pickedImage, xIcon;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            pickedImage = itemView.findViewById(R.id.picked_image);
            xIcon = itemView.findViewById(R.id.delete_image);

        }
    }
}
