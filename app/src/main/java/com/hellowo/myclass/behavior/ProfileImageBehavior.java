package com.hellowo.myclass.behavior;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.hellowo.myclass.App;
import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageBehavior extends CoordinatorLayout.Behavior<CardView> {
    final static float END_RATIO = 0.33f;
    float startRadius;
    int startX;
    int startY;
    int endX;
    int endY;
    int statusBarHeight;

    public ProfileImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        statusBarHeight = AppScreen.getStatusBarHeight(context);
        startRadius = AppScreen.dpToPx(10);
        startX = AppScreen.dpToPx(0);
        endX = AppScreen.dpToPx(0);
        startY = AppScreen.dpToPx(50);
        endY = AppScreen.dpToPx(50);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CardView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CardView child, View dependency) {
        int maxScroll = dependency.getHeight() - dependency.findViewById(R.id.appbarStatusLy).getHeight();
        float scroll = maxScroll + (dependency.getY() - statusBarHeight);

        float scroll_rate = scroll / maxScroll;
        Log.i("aaa", "scroll_rate/"+scroll_rate);

        float scale = END_RATIO + (scroll_rate * (1 - END_RATIO));
        float radius_scale = startRadius + (1 - scroll_rate) * ((child.getHeight() - startRadius) / 2);
        float x = startX + (endX - startX) * (1 - scroll_rate);
        float y = startY + (endY - startY) * (1 - scroll_rate);

        child.setRadius(radius_scale);
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setX(x);
        child.setY(y);
        return false;
    }
}