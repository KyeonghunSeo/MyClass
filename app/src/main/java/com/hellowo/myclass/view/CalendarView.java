package com.hellowo.myclass.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellowo.myclass.AppFont;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.utils.CalendarUtil;
import com.hellowo.myclass.utils.ViewUtil;

import java.util.Calendar;

public class CalendarView extends FrameLayout {
    private final static long showingAnimationDuation = 250;
    private final static int columns = 7;
    private final static int rows = 6;
    private final static int dateTextSize = 16;
    private final static int lineSize = 0;
    private final static int dateTextViewMargin = AppScreen.dpToPx(4);
    private final static int verticalLineSize = AppScreen.dpToPx(0);
    private Context context;
    private Calendar todayCal;
    private Calendar currentCal;
    private Calendar startCal;
    private Calendar endCal;
    private FrameLayout timeBlockLy;
    private LinearLayout calLy;
    private LinearLayout coverLy;
    private TextView[] dateTexts;
    private LinearLayout[] lineLys;
    private LinearLayout[] cellLys;
    private LinearLayout[] coverLineLys;
    private LinearLayout[] coverCellLys;
    private LinearLayout[] horizontalLineLys;
    private LinearLayout[] verticalLineLys;
    private int startPos;
    private int lastDate;
    private int weekpos;
    private int todayPos = -1;
    private CalendarInterface calendarInterface;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, Calendar calendar) {
        setVerticalScrollBarEnabled(false);
        this.context = context;
        this.currentCal = calendar;
        todayCal = Calendar.getInstance();
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        timeBlockLy = new FrameLayout(context);
        calLy = new LinearLayout(context);
        coverLy = new LinearLayout(context);
        lineLys = new LinearLayout[rows];
        cellLys = new LinearLayout[rows * columns];
        coverLineLys = new LinearLayout[rows];
        coverCellLys = new LinearLayout[rows * columns];
        dateTexts = new TextView[rows * columns];
        horizontalLineLys = new LinearLayout[rows];
        verticalLineLys = new LinearLayout[rows * columns];
        addView(calLy);
        addView(timeBlockLy);
        addView(coverLy);

        createViewItem();
        setLayoutParams();
        setLineParam();

        ViewUtil.runCallbackAfterViewDrawed(this, new Runnable() {
            @Override
            public void run() {
                drawCalendar();
            }
        });
    }

    private void drawCalendar() {
        timeBlockLy.removeAllViews();
        setCalendarData();
        drawMonthCalendarDate();
    }

    private void setCalendarData(){
        setRows();
        setStartPos();
        setWeekPos();
        startCal.setTimeInMillis(currentCal.getTimeInMillis());
        startCal.add(Calendar.DATE, -startPos);
        endCal.setTimeInMillis(startCal.getTimeInMillis());
        endCal.add(Calendar.DATE, rows * columns - 1);
        CalendarUtil.setCalendarTime0(todayCal);
        CalendarUtil.setCalendarTime0(startCal);
        CalendarUtil.setCalendarTime23(endCal);
    }

    private void setRows() {
        currentCal.set(Calendar.DATE, 1);
        lastDate = currentCal.getActualMaximum(Calendar.DATE);
        int currentRows = getCurrentRows();

        if(currentRows == 4) {
            lineLys[4].setVisibility(GONE);
            coverLineLys[4].setVisibility(GONE);
            horizontalLineLys[4].setVisibility(GONE);
            lineLys[5].setVisibility(GONE);
            coverLineLys[5].setVisibility(GONE);
            horizontalLineLys[5].setVisibility(GONE);
        }else if(currentRows == 5) {
            lineLys[4].setVisibility(VISIBLE);
            coverLineLys[4].setVisibility(VISIBLE);
            horizontalLineLys[4].setVisibility(VISIBLE);
            lineLys[5].setVisibility(GONE);
            coverLineLys[5].setVisibility(GONE);
            horizontalLineLys[5].setVisibility(GONE);
        }else{
            lineLys[4].setVisibility(VISIBLE);
            coverLineLys[4].setVisibility(VISIBLE);
            horizontalLineLys[4].setVisibility(VISIBLE);
            lineLys[5].setVisibility(VISIBLE);
            coverLineLys[5].setVisibility(VISIBLE);
            horizontalLineLys[5].setVisibility(VISIBLE);
        }
    }

    /**
     * 해당 달이 시작되는 1일의 포지션 구함
     */
    private void setStartPos(){
        startPos = currentCal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        if (startPos < 0) {
            startPos += 7;
        }
    }

    private void setWeekPos() {
        weekpos = 0;
    }

    public void createViewItem() {
        for (int i = 0; i < rows * 2; i++) {
            if (i % 2 == 1) {
                lineLys[i / 2] = new LinearLayout(context);
                calLy.addView(lineLys[i / 2]);
                coverLineLys[i / 2] = new LinearLayout(context);
                coverLy.addView(coverLineLys[i / 2]);

                for (int j = 0; j < columns * 2; j++) {
                    if (j % 2 == 0) {
                        int pos = ((i / 2) * columns) + (j / 2);
                        cellLys[pos] = new LinearLayout(context);
                        dateTexts[pos] = new TextView(context);
                        lineLys[i / 2].addView(cellLys[pos]);
                        cellLys[pos].addView(dateTexts[pos]);
                        coverCellLys[pos] = new LinearLayout(context);
                        coverLineLys[i / 2].addView(coverCellLys[pos]);
                    } else {
                        int pos = ((i / 2) * (columns)) + (j - 1) / 2;
                        verticalLineLys[pos] = new LinearLayout(context);
                        lineLys[i / 2].addView(verticalLineLys[pos]);
                    }
                }
            } else {
                horizontalLineLys[i / 2] = new LinearLayout(context);
                calLy.addView(horizontalLineLys[i / 2]);
            }
        }
    }

    public void setLayoutParams() {
        setBackgroundColor(Color.WHITE);
        timeBlockLy.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        calLy.setOrientation(LinearLayout.VERTICAL);
        calLy.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );
        coverLy.setOrientation(LinearLayout.VERTICAL);
        coverLy.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        );

        LinearLayout.LayoutParams dateTextLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);
        dateTextLp.setMargins(0, dateTextViewMargin, 0, 0);

        for (int i = 0; i < rows * 2; i++) {
            lineLys[i / 2].setOrientation(LinearLayout.HORIZONTAL);
            lineLys[i / 2].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            coverLineLys[i / 2].setOrientation(LinearLayout.HORIZONTAL);
            coverLineLys[i / 2].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            for (int j = 0; j < columns; j++) {
                final int cellnum = ((i / 2) * columns) + j;
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
                cellLys[cellnum].setOrientation(LinearLayout.VERTICAL);
                cellLys[cellnum].setGravity(Gravity.CENTER_HORIZONTAL);
                cellLys[cellnum].setLayoutParams(param);

                coverCellLys[cellnum].setOrientation(LinearLayout.VERTICAL);
                coverCellLys[cellnum].setLayoutParams(param);
                coverCellLys[cellnum].setBackgroundResource(R.drawable.ripple_button);
                coverCellLys[cellnum].setClickable(true);
                coverCellLys[cellnum].setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDateClick(cellnum, false);
                    }
                });
                coverCellLys[cellnum].setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onDateClick(cellnum, true);
                        return false;
                    }
                });

                dateTexts[cellnum].setLayoutParams(dateTextLp);
                dateTexts[cellnum].setTextSize(TypedValue.COMPLEX_UNIT_DIP, dateTextSize);
                dateTexts[cellnum].setPadding(AppScreen.dpToPx(3), AppScreen.dpToPx(3),
                        AppScreen.dpToPx(3), AppScreen.dpToPx(3));
                dateTexts[cellnum].setIncludeFontPadding(false);
                dateTexts[cellnum].setGravity(Gravity.CENTER);
                dateTexts[cellnum].setTypeface(AppFont.mainConceptBold);
            }
        }
    }

    /**
     * 라인 레이아웃 세팅
     */
    public void setLineParam() {
        for (int i = 0; i < rows; i++) {
            horizontalLineLys[i].setBackgroundColor(
                    context.getResources().getColor(R.color.main_background_dark));
            horizontalLineLys[i].setLayoutParams(
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineSize));
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int pos = (i * columns) + j;
                verticalLineLys[pos].setBackgroundColor(
                        context.getResources().getColor(R.color.main_background_dark));
                verticalLineLys[pos].setLayoutParams(
                        new LinearLayout.LayoutParams(lineSize, j == (columns - 1) ?
                                ViewGroup.LayoutParams.MATCH_PARENT : verticalLineSize));
            }
        }
    }

    /**
     * 날짜 클릭 이벤트 발생
     * @param cal 해당 날짜
     */
    public void onDateClickEvent(Calendar cal){
        int cellNum = 0;
        Calendar tempCal = (Calendar)startCal.clone();
        while(!(cal.get(Calendar.YEAR) == tempCal.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_YEAR) == tempCal.get(Calendar.DAY_OF_YEAR))
                && tempCal.compareTo(endCal) < 0){
            tempCal.add(Calendar.DATE, 1);
            cellNum++;
        }
        onDateClick(cellNum, false);
    }

    /**
     * 날짜 클릭
     */
    private void onDateClick(int cellnum, boolean isLongClick) {
        Calendar clickedCal = (Calendar)currentCal.clone();
        clickedCal.add(Calendar.DATE,  cellnum - startPos);
        if(isLongClick){

        }else{
            if(calendarInterface != null) {
                calendarInterface.onClicked(clickedCal);
            }
        }
    }

    /**
     * 월간 달력 날짜에 맞도록 달력을 그리고 날짜 등을 채워 넣음
     */
    public void drawMonthCalendarDate() {
        int next_month_date = 0;
        boolean is_today;
        Calendar targetCal = (Calendar) currentCal.clone();
        targetCal.add(Calendar.DATE, -startPos);
        for (int i = 0; i < rows * columns; i++) {
            is_today = CalendarUtil.isSameDay(targetCal, todayCal);
            if (i < startPos) {
                dateTexts[i].setText(String.valueOf(targetCal.get(Calendar.DATE)));
                setDateText(dateTexts[i], i, targetCal, true, is_today, false);
            } else if (i >= startPos && i < startPos + lastDate) {
                dateTexts[i].setText(String.valueOf(1 + i - startPos));
                setDateText(dateTexts[i], i, targetCal, false, is_today, false);
            } else {
                dateTexts[i].setText(String.valueOf(1 + next_month_date));
                next_month_date++;
                setDateText(dateTexts[i], i, targetCal, true, is_today, false);
            }
            targetCal.add(Calendar.DATE, 1);
        }
    }

    /**
     * 오늘, 주말, 공휴일, 과거등을 고려하여 날짜 텍스트 세팅
     */
    private void setDateText(TextView textview, int pos, Calendar cal, boolean alpha,
                             boolean is_today, boolean is_selected){
        if(is_today){
            todayPos = pos;
        }

        if(is_today) {
            textview.setBackgroundResource(R.drawable.fill_circle_primary);
            textview.setTextColor(context.getResources().getColor(R.color.white));
        }else{
            textview.setBackgroundResource(R.color.blank);
            if(weekpos == pos % columns){
                textview.setTextColor(context.getResources().getColor(R.color.accent));
            }else{
                textview.setTextColor(context.getResources().getColor(R.color.primary_text));
            }
        }

        if(alpha) {
            textview.setAlpha(0.3f);
        }else{
            textview.setAlpha(1f);
        }
    }

    /**
     * 블럭 나타나는 애니메이션 설정
     */
    private void setAppearBlockAnimation(long delay, View view){
        ViewCompat.animate(view)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(delay)
                .setDuration(showingAnimationDuation)
                .start();
    }

    /**
     * 블럭 하이라이트 애니메이션 설정
     */
    private void setHighlightBlockAnimation(View view){
        ViewCompat.animate(view)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(100)
                .setDuration(400)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .start();
    }

    @Override
    public String toString() {
        return "CalendarView{" +
                "rows=" + rows +
                ", startDate=" + startCal.get(Calendar.DATE) +
                ", endDate=" + endCal.get(Calendar.DATE) +
                ", startPos=" + startPos +
                ", weekpos=" + weekpos +
                ", todayPos=" + todayPos +
                ", lastDate=" + lastDate +
                '}';
    }

    public Calendar getStartCal() {
        return startCal;
    }

    public Calendar getEndCal() {
        return endCal;
    }

    public Calendar getTodayCal() {
        return todayCal;
    }

    public int getCurrentRows() {
        return currentCal.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    public int getColumns() {
        return columns;
    }

    public LinearLayout[] getCellLys() {
        return cellLys;
    }

    public void setCalendarInterface(CalendarInterface calendarInterface) {
        this.calendarInterface = calendarInterface;
    }

    public void prevMonth() {
        currentCal.add(Calendar.MONTH, -1);
        drawCalendar();
    }

    public void nextMonth() {
        currentCal.add(Calendar.MONTH, 1);
        drawCalendar();
    }

    public interface CalendarInterface{
        public void onClicked(Calendar clickedCal);
    }
}