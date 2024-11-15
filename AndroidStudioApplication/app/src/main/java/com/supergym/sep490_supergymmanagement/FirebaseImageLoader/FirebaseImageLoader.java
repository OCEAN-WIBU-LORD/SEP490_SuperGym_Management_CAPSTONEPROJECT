package com.supergym.sep490_supergymmanagement.FirebaseImageLoader;


import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.ImageAdapter;
import com.supergym.sep490_supergymmanagement.models.ImageModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseImageLoader {

    private List<ImageModel> imageList = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private RecyclerView recyclerView;

    public FirebaseImageLoader(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.imageAdapter = new ImageAdapter(context, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public void loadNewestImages() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference facesFolderRef = storage.getReference().child("faces");

        facesFolderRef.listAll().addOnSuccessListener(listResult -> {
            List<StorageReference> allImages = listResult.getItems();
            List<ImageModel> tempImageList = new ArrayList<>();

            // Load each image metadata asynchronously
            for (StorageReference imageRef : allImages) {
                imageRef.getMetadata().addOnSuccessListener(metadata -> {
                    long creationTime = metadata.getCreationTimeMillis();
                    String imageName = imageRef.getName();

                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Add each image with its metadata to the temp list
                        tempImageList.add(new ImageModel(uri.toString(), imageName, creationTime));

                        // Once all items are added to the list, sort by creation time
                        if (tempImageList.size() == allImages.size()) {
                            // Sort images by creation time in descending order (newest first)
                            Collections.sort(tempImageList, (img1, img2) -> Long.compare(img2.getCreationTime(), img1.getCreationTime()));

                            // Take only the 50 newest images
                            List<ImageModel> newestImages = tempImageList.size() > 50
                                    ? tempImageList.subList(0, 50)
                                    : tempImageList;

                            // Update imageList and notify adapter
                            imageList.clear();
                            imageList.addAll(newestImages);
                            imageAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(e -> Log.e("FirebaseImageLoader", "Failed to load image URL", e));
                }).addOnFailureListener(e -> Log.e("FirebaseImageLoader", "Failed to get metadata", e));
            }
        }).addOnFailureListener(e -> Log.e("FirebaseImageLoader", "Failed to list images", e));
    }




}
