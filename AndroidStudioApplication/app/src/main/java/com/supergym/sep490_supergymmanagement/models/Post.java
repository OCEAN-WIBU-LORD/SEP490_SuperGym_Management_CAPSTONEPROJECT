package com.supergym.sep490_supergymmanagement.models;

import android.util.Log;
import com.google.firebase.database.PropertyName;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post {
    @PropertyName("PostId")
    private String postId;

    @PropertyName("Title")
    private String title;

    @PropertyName("ThumbnailUrl")
    private String thumbnailUrl;

    @PropertyName("Date")
    private String date;

    @PropertyName("UserId")
    private String userId;

    @PropertyName("Content")
    private String content;

    @PropertyName("CategoryId")
    private String categoryId;

    private String authorName;

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public Post() {
        // Constructor không tham số
    }

    public Post(String postId, String title, String content, String categoryId, String thumbnailUrl, Date date, String userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.categoryId = categoryId;
        this.thumbnailUrl = thumbnailUrl;
        this.date = formatDate(date);
        this.userId = userId;
    }

    private static String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        return dateFormat.format(date);
    }

    public Date getParsedDate() {
        if (date == null) {
            Log.e("Post", "Date field is null for postId: " + postId);
            return null;
        }

        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            Log.e("Post", "Error parsing date for postId: " + postId + ", date: " + date, e);
            return null;
        }
    }

    // Getters và setters với @PropertyName
    @PropertyName("PostId")
    public String getPostId() { return postId; }
    @PropertyName("PostId")
    public void setPostId(String postId) { this.postId = postId; }

    @PropertyName("Title")
    public String getTitle() { return title; }
    @PropertyName("Title")
    public void setTitle(String title) { this.title = title; }

    @PropertyName("ThumbnailUrl")
    public String getThumbnailUrl() { return thumbnailUrl; }
    @PropertyName("ThumbnailUrl")
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    @PropertyName("Date")
    public String getDate() { return date; }
    @PropertyName("Date")
    public void setDate(String date) { this.date = date; }

    @PropertyName("UserId")
    public String getUserId() { return userId; }
    @PropertyName("UserId")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("Content")
    public String getContent() { return content; }
    @PropertyName("Content")
    public void setContent(String content) { this.content = content; }

    @PropertyName("CategoryId")
    public String getCategoryId() { return categoryId; }
    @PropertyName("CategoryId")
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}
