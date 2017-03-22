package com.hellowo.myclass.behavior;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageBehavior extends CoordinatorLayout.Behavior<View> {

    public ProfileImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof CollapsingToolbarLayout;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        Log.i("aaa", ""+dependency.getY());
        return false;
    }
}