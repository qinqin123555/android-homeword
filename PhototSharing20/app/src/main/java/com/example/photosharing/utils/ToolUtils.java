package com.example.photosharing.utils;

import java.text.SimpleDateFormat;

public class ToolUtils {

    public static String formatTrackTime(double trackTime) {
        String result = new SimpleDateFormat("yyyy-MM-dd").format(trackTime);
        return result;
    }

    public static String getTrackTime() {
        return String.valueOf(System.currentTimeMillis());
    }

}
