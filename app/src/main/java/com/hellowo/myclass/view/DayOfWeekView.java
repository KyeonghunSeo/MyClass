package com.hellowo.myclass.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellowo.myclass.AppColor;

/**
 * Created by Day2Life Android Dev on 2016-08-11.
 * 캘린더 상단 요일 뷰
 */
public class DayOfWeekView extends LinearLayout {
    private final static int dowTextSize = 13;

    private String[] dowString = {"월", "화", "수", "목", "금", "토", "일"};
    private TextView[] dowTexts;

    public DayOfWeekView(Context context) {
        super(context);
        init(context);
    }

    public DayOfWeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DayOfWeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.WHITE);
        setOrientation(HORIZONTAL);
        dowTexts = new TextView[7];
        for(int i = 0 ; i < 7 ; i++){
            dowTexts[i] = new TextView(context);
            dowTexts[i].setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
            dowTexts[i].setText(dowString[i]);
            dowTexts[i].setTextColor((i == 0) ? AppColor.accent : AppColor.primaryText);
            dowTexts[i].setGravity(Gravity.CENTER);
            dowTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, dowTextSize);
            addView(dowTexts[i]);
        }
    }

}
