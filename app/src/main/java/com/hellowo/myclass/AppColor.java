package com.hellowo.myclass;

public class AppColor {
    public static int primary;
    public static int accent;
    public static int primaryText;
    public static int secondaryText;
    public static int disableText;
    public static int primaryWhiteText;
    public static int secondaryWhiteText;
    public static int disableWhiteText;
    public static int gainsboro;

    public static void init(App app) {
        primary = app.getResources().getColor(R.color.primary);
        accent = app.getResources().getColor(R.color.accent);
        primaryText = app.getResources().getColor(R.color.primary_text);
        secondaryText = app.getResources().getColor(R.color.secondary_text);
        disableText = app.getResources().getColor(R.color.disable_text);
        primaryWhiteText = app.getResources().getColor(R.color.primary_white_text);
        secondaryWhiteText = app.getResources().getColor(R.color.secondary_white_text);
        disableWhiteText = app.getResources().getColor(R.color.disable_white_text);
        gainsboro = app.getResources().getColor(R.color.gainsboro);
    }

}
