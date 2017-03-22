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

import com.hellowo.myclass.AppScreen;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageBehavior extends CoordinatorLayout.Behavior<CircleImageView> {
    int startX;
    int startY;
    int minHeaderHeight;

    public ProfileImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        startX = AppScreen.getDeviceWidth(context) / 2 - AppScreen.dpToPx(200) / 2;
        startY = AppScreen.dpToPx(50);
        minHeaderHeight = AppScreen.dpToPx(70);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        if(dependency instanceof AppBarLayout){
            Log.i("minHeaderHeight", "CollapsingToolbarLayout");
        }
            Log.i("minHeaderHeight", ""+minHeaderHeight);
        Log.i("aaa", ""+dependency.getY());
        child.setX(startX);
        return false;
    }
}