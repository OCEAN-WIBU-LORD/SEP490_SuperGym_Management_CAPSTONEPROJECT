package com.example.sep490_supergymmanagement.models;



public class ImageModel {
    private String imageUrl;
    private String imageName;
    private long creationTime;  // New field for creation time

    public ImageModel(String imageUrl, String imageName, long creationTime) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.creationTime = creationTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public long getCreationTime() {  // New getter for creation time
        return creationTime;
    }
}
