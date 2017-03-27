package com.hellowo.myclass.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class ViewUtil {

    /**
     * 뷰가 그려진 후에 콜백 실행해줌
     * @param view
     */
    public static void runCallbackAfterViewDrawed(final View view, final Runnable callback){
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        callback.run();
                    }
                });
    }

    /**
     * 이미지뷰에 그레이 필터 씌우기
     */
    public static void setImageViewGrayFilter(ImageView v){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0); //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setAlpha(128);   // 128 = 0.5
    }

    /**
     * 이미지뷰 필터 해제
     */
    public static void removeImageViewFilter(ImageView v){
        v.setColorFilter(null);
        v.setAlpha(255);
    }

    /**
     * 키패드 숨기기
     */
    public static void hideKeyPad(Activity activity, EditText editText) {
        InputMethodManager imm
                = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
