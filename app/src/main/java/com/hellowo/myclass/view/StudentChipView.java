package com.hellowo.myclass.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hellowo.myclass.AppColor;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.model.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
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

    private int chipSize = AppScreen.dpToPx(40);
    private int chipMargin = AppScreen.dpToPx(5);
    private int maxWidth;
    private int maxChipWidth;

    private List<Student> studentList;

    public void init(){}

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setChipSize(int chipSize) {
        this.chipSize = chipSize;
    }

    public void makeStudentChips(List<Student> studentList) {
        this.studentList = studentList;

        removeAllViews();

        if(studentList.size() > 0) {

            maxChipWidth = studentList.size() * chipSize + (studentList.size() - 1) * chipSize;

            int position = 0;

            for(Student student : studentList) {
                makeStudentChip(student, position);
                position++;
            }

        }
    }

    private void makeStudentChip(final Student student, final int position) {
        CircleImageView imageView = new CircleImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(chipSize, chipSize));
        imageView.setBackgroundResource(R.drawable.fill_circle_white);
        imageView.setBorderColor(AppColor.primaryWhiteText);
        imageView.setBorderWidth(AppScreen.dpToPx(2));

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
            imageView.setTranslationX((maxWidth - chipSize) / studentList.size() * position);
        }else {
            imageView.setTranslationX(chipSize * position + chipMargin * position);
        }

        addView(imageView);

    }
}
