package com.hellowo.myclass.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.model.Student;

import java.io.File;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class StudentChipView extends FrameLayout {
    public StudentChipView(Context context) {
        super(context);
        init();
    }

    public StudentChipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StudentChipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StudentChipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private final static int PADDING = AppScreen.dpToPx(10);
    private int chipSize = AppScreen.dpToPx(40);
    private int chipMargin = AppScreen.dpToPx(5);
    private int maxChipWidth;

    private Map<String, Student> studentMap;

    public void init(){
        setPadding(0, PADDING, 0, PADDING);
    }

    public void makeStudentChips(Map<String, Student> studentMap) {
        this.studentMap = studentMap;

        removeAllViews();

        if(studentMap.size() > 0) {

            maxChipWidth = studentMap.size() * chipSize + (studentMap.size() - 1) * chipSize;

            int position = 0;

            for(String id : studentMap.keySet()) {
                Student student = studentMap.get(id);
                makeStudentChip(student, position);
                position++;
            }

        }
    }

    private void makeStudentChip(final Student student, final int position) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(chipSize, chipSize));
        imageView.setBackgroundResource(R.drawable.fill_circle_white);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setElevation(AppScreen.dpToPx(1));
        }

        if(!TextUtils.isEmpty(student.profileImageUri)) { // 이미지 경로가 있으면 로드함

            Glide.with(getContext())
                    .load(new File(student.profileImageUri))
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(imageView);

        }else {

            Glide.with(getContext()).load(R.drawable.default_profile_circle)
                    .bitmapTransform(new CropCircleTransformation(getContext()))
                    .into(imageView);

        }

        if(maxChipWidth > getWidth()) {
            imageView.setTranslationX((getWidth() - chipSize) / studentMap.size() * position);
        }else {
            imageView.setTranslationX(chipSize * position + chipMargin * position);
        }

        addView(imageView);

    }

    public int getChipCount() {
        return getChildCount();
    }
}
