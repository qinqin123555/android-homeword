package com.example.photosharing.entity;


public class ShowBean {

    private int picture;

    private String title;

    private int avatar;

    private String author;

    private int love;

    private String likeNum;

    public ShowBean() {
    }

    public ShowBean(int picture, String title, int avatar, String author, int love, String likeNum) {
        this.picture = picture;
        this.title = title;
        this.avatar = avatar;
        this.author = author;
        this.love = love;
        this.likeNum = likeNum;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }
}
