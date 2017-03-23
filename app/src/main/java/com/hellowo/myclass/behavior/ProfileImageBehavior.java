package com.hellowo.myclass.behavior;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.hellowo.myclass.App;
import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppScreen;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageBehavior extends CoordinatorLayout.Behavior<CircleImageView> {
    final static float END_RATIO = 0.33f;
    final static int END_MARGIN = AppScreen.dpToPx(10);
    int startX;
    int startY;
    int endX;
    int endY;
    int minHeaderHeight;
    int statusBarHeight;

    public ProfileImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        minHeaderHeight = AppScreen.topToolbarHeight + /*하단 걸리는 부분*/AppScreen.dpToPx(60);
        statusBarHeight = AppScreen.getStatusBarHeight(context);
        startX = AppScreen.getDeviceWidth(context) / 2 - AppScreen.dpToPx(75);
        endX = AppScreen.getDeviceWidth(context) - AppScreen.dpToPx(110);
        startY = AppScreen.dpToPx(75) + statusBarHeight;
        endY = -(AppScreen.dpToPx(75) / 2) + statusBarHeight;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        int maxScroll = dependency.getHeight() - minHeaderHeight;
        float scroll = maxScroll + (dependency.getY() - statusBarHeight);

        float scroll_rate = scroll / maxScroll;
        Log.i("aaa", "scroll_rate/"+scroll_rate);

        float scale = END_RATIO + (scroll_rate * (1 - END_RATIO));
        float x = startX + (endX - startX) * (1 - scroll_rate);
        float y = startY + (endY - startY) * (1 - scroll_rate);

        child.setScaleX(scale);
        child.setScaleY(scale);

        child.setX(x);
        child.setY(y);
        return false;
    }
}