package com.example.photosharing.net;

import com.example.photosharing.utils.MergeURLUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;

public class FragmentRequest {

    MyRequest MyRequest = new MyRequest();

    public void getShare(Map<String, String> map, Callback callback) {
        MyRequest.doGet(URLS.GET_FIND.getUrl(),
                MyHeaders.getHeaders(),
                map,
                callback);
    }

    public void getFollow(Map<String, String> map, Callback callback) {
        MyRequest.doGet(URLS.FOLLOWED.getUrl(),
                MyHeaders.getHeaders(),
                map,
                callback);
    }

    public void getMyself(Map<String, String> map, Callback callback) {
        MyRequest.doGet(URLS.GET_MYSELF_SHARE.getUrl(),
                MyHeaders.getHeaders(),
                map,
                callback);
    }

    public void getLike(Map<String, String> map, Callback callback) {
        MyRequest.doGet(URLS.GET_LIKE.getUrl(),
                MyHeaders.getHeaders(),
                map,
                callback);
    }

    public void getCollection(Map<String, String> map, Callback callback) {
        MyRequest.doGet(URLS.COLLECTION.getUrl(),
                MyHeaders.getHeaders(),
                map,
                callback);
    }

    public void deleteShow(Map<String, String> map, Callback callback) {
        MyRequest.doPost(MergeURLUtil.merge(URLS.DELETE_SHARE.getUrl(), map),
                MyHeaders.getHeaders(),
                new HashMap<>(),
                callback);
    }

}
