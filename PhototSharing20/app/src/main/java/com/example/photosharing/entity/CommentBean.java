package com.example.photosharing.entity;

import java.util.List;

public class CommentBean {
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
        private int current;
        private List<RecordsBean> records;
        private int size;
        private int total;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public static class RecordsBean {
            private String appKey;
            private String commentLevel;
            private String content;
            private String createTime;
            private String id;
            private String pUserId;
            private String parentCommentId;
            private String parentCommentUserId;
            private String replyCommentId;
            private String replyCommentUserId;
            private String shareId;
            private String userName;

            public String getAppKey() {
                return appKey;
            }

            public void setAppKey(String appKey) {
                this.appKey = appKey;
            }

            public String getCommentLevel() {
                return commentLevel;
            }

            public void setCommentLevel(String commentLevel) {
                this.commentLevel = commentLevel;
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

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPUserId() {
                return pUserId;
            }

            public void setPUserId(String pUserId) {
                this.pUserId = pUserId;
            }

            public String getParentCommentId() {
                return parentCommentId;
            }

            public void setParentCommentId(String parentCommentId) {
                this.parentCommentId = parentCommentId;
            }

            public String getParentCommentUserId() {
                return parentCommentUserId;
            }

            public void setParentCommentUserId(String parentCommentUserId) {
                this.parentCommentUserId = parentCommentUserId;
            }

            public String getReplyCommentId() {
                return replyCommentId;
            }

            public void setReplyCommentId(String replyCommentId) {
                this.replyCommentId = replyCommentId;
            }

            public String getReplyCommentUserId() {
                return replyCommentUserId;
            }

            public void setReplyCommentUserId(String replyCommentUserId) {
                this.replyCommentUserId = replyCommentUserId;
            }

            public String getShareId() {
                return shareId;
            }

            public void setShareId(String shareId) {
                this.shareId = shareId;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }
        }
    }
}
