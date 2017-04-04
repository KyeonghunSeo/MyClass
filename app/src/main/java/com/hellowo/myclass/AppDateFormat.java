package com.hellowo.myclass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppDateFormat {
    public static DateFormat ym;
    public static DateFormat mdeDate;
    public static DateFormat time;
    public static DateFormat ymdkey;
    public static DateFormat smallmdeDate;

    public static void init(App app) {
        ym = new SimpleDateFormat("yyyy년 M월");
        mdeDate = new SimpleDateFormat("M월 d일 E요일");
        time = new SimpleDateFormat("a hh:mm");
        ymdkey = new SimpleDateFormat("yyyyMMdd");
        smallmdeDate = new SimpleDateFormat("M. d. E");
    }
}
