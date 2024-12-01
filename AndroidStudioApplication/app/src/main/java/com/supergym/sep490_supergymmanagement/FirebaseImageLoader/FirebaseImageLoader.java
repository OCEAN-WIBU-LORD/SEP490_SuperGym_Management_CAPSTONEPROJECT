package com.supergym.sep490_supergymmanagement.FirebaseImageLoader;

import android.content.Context;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.supergym.sep490_supergymmanagement.adapters.ImageAdapter;
import com.supergym.sep490_supergymmanagement.models.ImageModel;

import java.util.ArrayList;
import java.util.List;

public class FirebaseImageLoader {

    private static final int PAGE_SIZE = 10;
    private final List<ImageModel> imageList = new ArrayList<>();
    private final ImageAdapter imageAdapter;
    private boolean isLoading = false;
    private boolean hasMoreImages = true;
    private String nextPageToken = null;

    public FirebaseImageLoader(RecyclerView recyclerView, Context context) {
        this.imageAdapter = new ImageAdapter(context, imageList);
        recyclerView.setAdapter(imageAdapter);
    }

    public void loadImages(boolean initialLoad, String selectedDate) {
        if (isLoading || !hasMoreImages) {
            return; // Prevent concurrent loads or loading when there are no more images
        }

        if (initialLoad) {
            resetPagination(); // Reset the image list when a new filter is applied (date change)
        }

        isLoading = true;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference facesFolderRef = storage.getReference().child("faces/" + selectedDate);

        if (nextPageToken == null) {
            facesFolderRef.list(PAGE_SIZE)
                    .addOnSuccessListener(this::handleListResult)
                    .addOnFailureListener(this::handleError);
        } else {
            facesFolderRef.list(PAGE_SIZE, nextPageToken)
                    .addOnSuccessListener(this::handleListResult)
                    .addOnFailureListener(this::handleError);
        }
    }

    // Reset pagination and clear the image list for a fresh load
    public void resetPagination() {
        imageList.clear();
        imageAdapter.notifyDataSetChanged();
        nextPageToken = null;
        hasMoreImages = true;
    }

    private void handleListResult(ListResult listResult) {
        List<StorageReference> allImages = listResult.getItems();

        if (allImages.isEmpty()) {
            hasMoreImages = false;
            isLoading = false;
            return;
        }

        for (StorageReference imageRef : allImages) {
            imageRef.getMetadata().addOnSuccessListener(metadata -> {
                long creationTime = metadata.getCreationTimeMillis();
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageList.add(new ImageModel(uri.toString(), imageRef.getName(), creationTime));
                    imageAdapter.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e("FirebaseImageLoader", "Error getting download URL: " + e.getMessage()));
            }).addOnFailureListener(e -> Log.e("FirebaseImageLoader", "Error getting metadata: " + e.getMessage()));
        }

        nextPageToken = listResult.getPageToken();
        hasMoreImages = nextPageToken != null;
        isLoading = false;
    }

    private void handleError(Exception e) {
        Log.e("FirebaseImageLoader", "Error loading images: " + e.getMessage());
        isLoading = false;
    }

    public List<ImageModel> getImageList() {
        return imageList;
    }
}
