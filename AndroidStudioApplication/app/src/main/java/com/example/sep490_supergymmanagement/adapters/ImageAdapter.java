package com.example.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.ImageModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context context;
    private List<ImageModel> imageList;

    public ImageAdapter(Context context, List<ImageModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel image = imageList.get(position);

        // Load the image using Glide
        Glide.with(context)
                .load(image.getImageUrl())
                .into(holder.imageView);

        // Set image name
        holder.textViewImageName.setText(image.getImageName());

        // Format and set creation time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String creationTimeFormatted = sdf.format(new Date(image.getCreationTime()));
        holder.textViewCreationTime.setText("Date time: "+creationTimeFormatted);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewImageName;
        TextView textViewCreationTime;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            textViewImageName = itemView.findViewById(R.id.textViewImageName);
            textViewCreationTime = itemView.findViewById(R.id.textViewCreationTime);
        }
    }
}
