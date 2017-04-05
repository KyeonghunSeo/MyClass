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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hellowo.myclass.AppColor;
import com.hellowo.myclass.AppConst;
import com.hellowo.myclass.AppFont;
import com.hellowo.myclass.AppScreen;
import com.hellowo.myclass.R;
import com.hellowo.myclass.model.Event;
import com.hellowo.myclass.utils.CalendarUtil;
import com.hellowo.myclass.utils.ViewUtil;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CalendarView extends FrameLayout {
    private final static long showingAnimationDuation = 250;
    private final static int columns = 7;
    private final static int rows = 6;
    private final static int dateTextSize = 15;
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
    private LinearLayout[] eventIndicatorLys;
    private int startPos;
    private int lastDate;
    private int weekpos;
    private int todayPos = -1;
    private int selectedPos = -1;
    private CalendarInterface calendarInterface;
    private Realm realm;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context, Calendar calendar, Realm realm) {
        setVerticalScrollBarEnabled(false);
        this.context = context;
        this.currentCal = calendar;
        this.realm = realm;
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
        eventIndicatorLys = new LinearLayout[rows * columns];
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
                onDateClick(todayPos, false);
            }
        });
    }

    private void drawCalendar() {
        timeBlockLy.removeAllViews();
        setCalendarData();
        drawMonthCalendarDate();
        drawIndicator();
    }

    public void drawIndicator() {
        for(int i = 0; i < eventIndicatorLys.length; i++) {
            eventIndicatorLys[i].getChildAt(0).setVisibility(GONE);
            eventIndicatorLys[i].getChildAt(1).setVisibility(GONE);
            eventIndicatorLys[i].getChildAt(2).setVisibility(GONE);
        }

        final long startTime = startCal.getTimeInMillis();
        final long endTime = endCal.getTimeInMillis();

        RealmResults<Event> eventRealmResults = realm.where(Event.class)
                .greaterThanOrEqualTo(Event.KEY_DT_END, startTime)
                .lessThanOrEqualTo(Event.KEY_DT_START, endTime)
                .findAllSorted(Event.KEY_DT_START, Sort.DESCENDING);

        for(Event event : eventRealmResults) {

            long eventStartTime = event.dtStart;
            long eventEndTime = event.dtEnd;

            int startCellNum = (int)((eventStartTime - startTime) / AppConst.DAY_MILL_SEC);
            int endCellNum = (int)((eventEndTime - startTime) / AppConst.DAY_MILL_SEC);

            for(int i = startCellNum; i <= endCellNum; i++) {
                try{
                    if(event.type == Event.TYPE_EVENT) {
                        eventIndicatorLys[i].getChildAt(1).setVisibility(VISIBLE);
                    }else if(event.type == Event.TYPE_TODO) {
                        eventIndicatorLys[i].getChildAt(2).setVisibility(VISIBLE);
                    }else {
                        eventIndicatorLys[i].getChildAt(0).setVisibility(VISIBLE);
                    }
                }catch (Exception ignore){}
            }
        }
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

                        lineLys[i / 2].addView(cellLys[pos]);

                        dateTexts[pos] = new TextView(context);
                        eventIndicatorLys[pos] = new LinearLayout(context);

                        cellLys[pos].addView(dateTexts[pos]);
                        cellLys[pos].addView(eventIndicatorLys[pos]);

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
                AppScreen.dpToPx(20), AppScreen.dpToPx(20));
        dateTextLp.setMargins(0, dateTextViewMargin, 0, 0);

        LinearLayout.LayoutParams indiLayoutLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        indiLayoutLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        indiLayoutLp.setMargins(0, AppScreen.dpToPx(3), 0, 0);

        LinearLayout.LayoutParams indiLp = new LinearLayout.LayoutParams(
                AppScreen.dpToPx(10), AppScreen.dpToPx(10));

        for (int i = 0; i < rows * 2; i++) {
            lineLys[i / 2].setOrientation(LinearLayout.HORIZONTAL);
            lineLys[i / 2].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
            coverLineLys[i / 2].setOrientation(LinearLayout.HORIZONTAL);
            coverLineLys[i / 2].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            for (int j = 0; j < columns; j++) {
                if(i % 2 == 0) {
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

                    ImageView student_event_indi = new ImageView(context);
                    ImageView normal_event_indi = new ImageView(context);
                    ImageView todo_event_indi = new ImageView(context);

                    student_event_indi.setLayoutParams(indiLp);
                    normal_event_indi.setLayoutParams(indiLp);
                    todo_event_indi.setLayoutParams(indiLp);

                    student_event_indi.setImageResource(R.drawable.ic_face_black_48dp);
                    normal_event_indi.setImageResource(R.drawable.ic_date_range_black_48dp);
                    todo_event_indi.setImageResource(R.drawable.ic_assignment_turned_in_black_48dp);

                    student_event_indi.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    normal_event_indi.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    todo_event_indi.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    eventIndicatorLys[cellnum].addView(student_event_indi);
                    eventIndicatorLys[cellnum].addView(normal_event_indi);
                    eventIndicatorLys[cellnum].addView(todo_event_indi);

                    eventIndicatorLys[cellnum].setOrientation(LinearLayout.HORIZONTAL);
                    eventIndicatorLys[cellnum].setLayoutParams(indiLayoutLp);

                    dateTexts[cellnum].setLayoutParams(dateTextLp);
                    dateTexts[cellnum].setTextSize(TypedValue.COMPLEX_UNIT_DIP, dateTextSize);
                    dateTexts[cellnum].setIncludeFontPadding(false);
                    dateTexts[cellnum].setGravity(Gravity.CENTER);
                    dateTexts[cellnum].setTypeface(AppFont.mainConceptBold);
                }
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

            if(selectedPos >= 0) {
                cellLys[selectedPos].setBackgroundColor(Color.TRANSPARENT);
            }

            cellLys[cellnum].setBackgroundResource(R.drawable.stroke_rect_primary);
            selectedPos = cellnum;
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
                setDateText(i, targetCal, true, is_today, false);
            } else if (i >= startPos && i < startPos + lastDate) {
                dateTexts[i].setText(String.valueOf(1 + i - startPos));
                setDateText(i, targetCal, false, is_today, false);
            } else {
                dateTexts[i].setText(String.valueOf(1 + next_month_date));
                next_month_date++;
                setDateText(i, targetCal, true, is_today, false);
            }
            targetCal.add(Calendar.DATE, 1);
        }
    }

    /**
     * 오늘, 주말, 공휴일, 과거등을 고려하여 날짜 텍스트 세팅
     */
    private void setDateText(int pos, Calendar cal, boolean alpha,
                             boolean is_today, boolean is_selected){
        if(is_today){
            todayPos = pos;
        }

        TextView textview = dateTexts[pos];

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
            eventIndicatorLys[pos].setAlpha(0.3f);
            textview.setAlpha(0.3f);
        }else{
            eventIndicatorLys[pos].setAlpha(1f);
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