package com.example.photosharing.net;

public enum URLS {


    LOGIN("/user/login"), // 登录
    REGISTER("/user/register"), // 注册

    GetUserByName("/user/getUserByName"), // 根据用户名获取用户信息

    UPDATE("/user/update"), // 更改个人信息

    ItemInfo("/share/detail"),
    COLLECTION("/collect"), // get 收藏列表

    COLLECT("/collect"), // post 添加

    CANCEL_COLLECT("/collect/cancel"), // 取消收藏

    GET_COMMENT("/comment/first"),

    ADD_COMMENT("/comment/first"), // get 一级评论

    FOLLOWED("/focus"), // get 关注列表

    ADD_FOLLOW("/focus"), // post 添加关注

    CANCEL_FOLLOW("/focus/cancel"), // 取消关注

    UPLOAD_IMAGE("/image/upload"), // 上传文件

    GET_LIKE("/like"), // get 点赞列表

    LIKE("/like"), // post 点赞

    CANCEL_LIKE("/like/cancel"), // 取消点赞

    GET_FIND("/share"), // get 发现列表

    ADD_SHARE("/share/add"), // post 新增图文

    SAVE_TO_SHARE("/share/change"), // 更新状态

    DELETE_SHARE("/share/delete"), // 删除图文

    SHARE_DETAIL("/share/detail"),

    GET_MYSELF_SHARE("/share/myself"),

    GET_SAVE_SHARE("/share/save"),

    SAVE("/share/save");

    static final String Base = "http://47.107.52.7:88/member/photo";

    private final String url;


    URLS(String string) {
        this.url = Base + string;
    }

    public String getUrl() {
        return url;
    }

}
