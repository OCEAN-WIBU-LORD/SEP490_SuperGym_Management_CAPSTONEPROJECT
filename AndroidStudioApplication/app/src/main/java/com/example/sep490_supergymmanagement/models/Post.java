package com.example.sep490_supergymmanagement.models;

import java.util.Date;

public class Post {

    private String post_id;
    private String author;
    private String category;
    private String content;
    private Date date;
    private String title;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Post(String post_id, String author, String category, String content, Date date, String title) {
        this.post_id = post_id;
        this.author = author;
        this.category = category;
        this.content = content;
        this.date = date;
        this.title = title;
    }
    public Post(){

    }
}
