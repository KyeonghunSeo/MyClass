package com.hellowo.myclass;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class AppScreen {
    public static DisplayMetrics metrics;

    public static void init(App app){
        metrics = app.getResources().getDisplayMetrics();
    }

    public static int getDeviceWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dpToPx(float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        return (int) px;
    }

}
