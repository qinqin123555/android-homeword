package com.example.photosharing.entity;

import java.util.List;

public class ItemBean {

    private String msg;
    private int code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String collectId;
        private int collectNum;
        private String content;
        private String createTime;
        private boolean hasCollect;
        private boolean hasFocus;
        private boolean hasLike;
        private String id;
        private String imageCode;
        private List<ImageUrlListBean> imageUrlList;
        private int likeId;
        private int likeNum;
        private String pUserId;
        private String title;
        private String username;

        public String getCollectId() {
            return collectId;
        }

        public void setCollectId(String collectId) {
            this.collectId = collectId;
        }

        public int getCollectNum() {
            return collectNum;
        }

        public void setCollectNum(int collectNum) {
            this.collectNum = collectNum;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public boolean isHasCollect() {
            return hasCollect;
        }

        public void setHasCollect(boolean hasCollect) {
            this.hasCollect = hasCollect;
        }

        public boolean isHasFocus() {
            return hasFocus;
        }

        public void setHasFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }

        public boolean isHasLike() {
            return hasLike;
        }

        public void setHasLike(boolean hasLike) {
            this.hasLike = hasLike;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageCode() {
            return imageCode;
        }

        public void setImageCode(String imageCode) {
            this.imageCode = imageCode;
        }

        public List<ImageUrlListBean> getImageUrlList() {
            return imageUrlList;
        }

        public void setImageUrlList(List<ImageUrlListBean> imageUrlList) {
            this.imageUrlList = imageUrlList;
        }

        public int getLikeId() {
            return likeId;
        }

        public void setLikeId(int likeId) {
            this.likeId = likeId;
        }

        public int getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(int likeNum) {
            this.likeNum = likeNum;
        }

        public String getPUserId() {
            return pUserId;
        }

        public void setPUserId(String pUserId) {
            this.pUserId = pUserId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public static class ImageUrlListBean {
        }
    }
}
