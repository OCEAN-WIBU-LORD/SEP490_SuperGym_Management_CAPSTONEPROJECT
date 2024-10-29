package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

import java.util.Date;

public class Post {

    private String postId;
    private String title;
    private String thumbnailUrl;
    private Date date;
    private String userId;
    private String content;
    private String categoryId;

    public Post() {
    }

    public Post(String postId, String title, String thumbnailUrl, Date date, String userId, String content, String categoryId) {
        this.postId = postId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.date = date;
        this.userId = userId;
        this.content = content;
        this.categoryId = categoryId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
