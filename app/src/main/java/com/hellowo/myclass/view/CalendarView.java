package com.hellowo.myclass.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
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

public class CalendarView extends NestedScrollView {
    private final static long showingAnimationDuation = 250;
    private final static int columns = 7;
    private final static int dateTextSize = 12;
    private final static int lineSize = 1;
    private final static int dateTextViewSize = AppScreen.dpToPx(17);
    private final static int dateTextViewMargin = AppScreen.dpToPx(2);
    private final static int verticalLineSize = AppScreen.dpToPx(0);
    private final static int todayAnimationDelay = 300;
    private final static int todayAnimDuration = 650;
    private final static int autoScrollMessage = 0;
    private final static int autoScrollDelay = 500;
    public final static Calendar weekSelectedCal = Calendar.getInstance();
    private Context context;
    private Calendar todayCal;
    private Calendar currentCal;
    private Calendar startCal;
    private Calendar endCal;
    private FrameLayout frameLy;
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
    private int rows;
    private int startPos;
    private int lastDate;
    private int weekpos;
    private int todayPos = -1;

    public CalendarView(Context context, Calendar calendar) {
        super(context);
        init(context, calendar);
        ViewUtil.runCallbackAfterViewDrawed(this, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     * 뷰 초기화
     */
    private void init(Context context, Calendar calendar) {
        setVerticalScrollBarEnabled(false);
        this.context = context;
        this.currentCal = calendar;
        todayCal = Calendar.getInstance();
        startCal = Calendar.getInstance();
        endCal = Calendar.getInstance();
        setCalendarData();
        frameLy = new FrameLayout(context);
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
        frameLy.addView(calLy);
        frameLy.addView(timeBlockLy);
        frameLy.addView(coverLy);
        addView(frameLy);
        createViewItem();
        setLayoutParams();
        setLineParam();
        drawMonthCalendarDate();
    }

    /**
     * 날짜 설정
     */
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

    /**
     * 행 구함
     */
    private void setRows() {
        currentCal.set(Calendar.DATE, 1);
        rows = currentCal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        lastDate = currentCal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 스타팅 포지션 구함
     */
    private void setStartPos(){
        startPos = currentCal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
        if (startPos < 0) {
            startPos += 7;
        }
    }

    /**
     * 주말 포지션 구함
     */
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
        frameLy.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        frameLy.setBackgroundColor(Color.WHITE);
        timeBlockLy.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        frameLy.setBackgroundColor(Color.WHITE);
        calLy.setOrientation(LinearLayout.VERTICAL);
        calLy.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AppScreen.dpToPx(500))
        );
        coverLy.setOrientation(LinearLayout.VERTICAL);
        coverLy.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AppScreen.dpToPx(500))
        );

        LinearLayout.LayoutParams dateTextLp = new LinearLayout.LayoutParams(
                dateTextViewSize, dateTextViewSize);
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
                dateTexts[i].setText("" + targetCal.get(Calendar.DATE));
                setDateText(dateTexts[i], i, targetCal, true, is_today, false);
            } else if (i >= startPos && i < startPos + lastDate) {
                dateTexts[i].setText("" + (1 + i - startPos));
                setDateText(dateTexts[i], i, targetCal, false, is_today, false);
            } else {
                dateTexts[i].setText("" + (1 + next_month_date));
                next_month_date++;
                setDateText(dateTexts[i], i, targetCal, true, is_today, false);
            }
            targetCal.add(Calendar.DATE, 1);
        }
    }

    /**
     * 주간 달력 날짜에 맞도록 달력을 그리고 날짜 등을 채워 넣음
     */
    public void drawWeekCalendarDate() {
        boolean is_today;
        boolean is_selected;
        Calendar targetCal = (Calendar) currentCal.clone();
        targetCal.add(Calendar.DATE, -startPos);
        for (int i = 0; i < columns; i++) {
            is_today = CalendarUtil.isSameDay(targetCal, todayCal);
            is_selected = CalendarUtil.isSameDay(targetCal, weekSelectedCal);
            dateTexts[i].setText("" + targetCal.get(Calendar.DATE));
            setDateText(dateTexts[i], i, targetCal, false, is_today, is_selected);
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
        /*
        if(cal.compareTo(todayCal) < 0) {
            alpha = true;
        }
        */
        if(weekpos == pos % columns){
            textview.setTextColor(context.getResources().getColor(R.color.black));
        }else{
            textview.setTextColor(context.getResources().getColor(R.color.primary));
        }
    }

    /**
     * 투데이 애니메이션 재생
     */
    public void animateToday(){
        if(todayPos >= 0 && todayPos < rows * columns){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    int scrollY = 0;
                    for(int i = 0 ; i < todayPos / columns ; i++){
                        scrollY += cellLys[i * columns].getHeight();
                    }
                    smoothScrollTo(0, scrollY);
                    ObjectAnimator.ofFloat(dateTexts[todayPos], "rotationY", 0f, 360f)
                            .setDuration(todayAnimDuration).start();
                }
            }, todayAnimationDelay);
        }
    }

    /*
    public void showTimeBlocks(BlockShowMode mode) {
        timeBlockLy.removeAllViews();
        expandRows();
        addTimeBlocks(mode);
        isTimeBlockShowed = true;
        if(readyTodayAnimation){
            animateToday();
        }
    }

    private void expandRows() {
        int[] cellHeight = currentCanvas.getCellHeight();
        int total_height = 0;
        for(int i = 0 ; i < cellHeight.length ; i++){
            lineLys[i].setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            cellHeight[i]));
            coverLineLys[i].setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            cellHeight[i] + lineSize));
            total_height += (cellHeight[i] + lineSize);
        }
        calLy.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, total_height));
        coverLy.setLayoutParams(
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, total_height));
    }

    private void addTimeBlocks(BlockShowMode mode) {
        List<TimeBlock> timeBlocks = currentCanvas.getTimeBlocks();
        if(timeBlocks != null && timeBlocks.size() > 0){
            if(mode == BlockShowMode.FirstShowing){ // 처음 보여줄때는 모든 블럭에 딜레이 애니메이션
                long delay_offset = 0;
                for(TimeBlock block : timeBlocks){
                    if(block.timeBlockViews != null){
                        for(TimeBlockView view : block.timeBlockViews){
                            view.setAlpha(0f);
                            view.setScaleX(0f);
                            view.setScaleY(0f);
                            timeBlockLy.addView(view);
                            setAppearBlockAnimation(250 + (25 * delay_offset), view);
                            delay_offset++;
                        }
                    }
                }
            }else if(mode == BlockShowMode.HightLightLastAction){
                boolean isTargeted;
                TimeBlock lastEditBlock = tbm.getLastChangedBlock();
                for(TimeBlock block : timeBlocks){
                    isTargeted = false;
                    if(lastEditBlock != null && block.getId() == lastEditBlock.getId()){
                        lastEditBlock = block;
                        isTargeted = true;
                    }
                    if(block.timeBlockViews != null){
                        for(TimeBlockView view : block.timeBlockViews){
                            timeBlockLy.addView(view);
                            if(isTargeted){
                                view.setAlpha(0f);
                                view.setScaleX(0f);
                                view.setScaleY(0f);
                                setHighlightBlockAnimation(view);
                            }
                        }
                    }
                }
            }
        }
    }*/

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

    public Calendar getCurrentCal() {
        return currentCal;
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

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public LinearLayout[] getCellLys() {
        return cellLys;
    }
}