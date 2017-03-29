package com.hellowo.myclass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AppDateFormat {
    public static DateFormat mdeDate;

    public static void init(App app) {
        mdeDate = new SimpleDateFormat("M월 d일 E요일");
    }
}
