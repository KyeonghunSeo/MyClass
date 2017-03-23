package com.hellowo.myclass;

import android.graphics.Typeface;

public class AppFont {
    public static Typeface mainConceptBold;

    public static void init(App app) {
        mainConceptBold = Typeface.createFromAsset(app.getAssets(), "dongle.ttf");
    }
}
