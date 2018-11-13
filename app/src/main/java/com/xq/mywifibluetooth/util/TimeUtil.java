package com.xq.mywifibluetooth.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

}
