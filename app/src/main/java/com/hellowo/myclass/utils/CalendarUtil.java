package com.hellowo.myclass.utils;

import java.util.Calendar;

public class CalendarUtil {
    private static Calendar tempCal = Calendar.getInstance();
    private static Calendar tempCal2 = Calendar.getInstance();

    /**
     * 하루의 시작으로 설정
     */
    public static void setCalendarTime0(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 하루의 끝으로 설정
     */
    public static void setCalendarTime23(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    /**
     * 정각으로 설정
     */
    public static void setCalendarOClock(Calendar calendar){
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 오늘의 시작으로 설정
     */
    public static void setCalendarTodayStart(Calendar calendar){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 오늘의 끝으로 설정
     */
    public static void setCalendarTodayEnd(Calendar calendar){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 1시간 간격으로 맞춰줌 맞춰줌 시작시간이 23시 일때는 끝나는 시간은 23시 59분
     */
    public static void setTime1HourInterval(Calendar startCal, Calendar endCal){
        if(startCal.get(Calendar.HOUR_OF_DAY) < 23){
            endCal.setTimeInMillis(startCal.getTimeInMillis());
            endCal.add(Calendar.HOUR_OF_DAY, 1);
        }else{
            endCal.setTimeInMillis(startCal.getTimeInMillis());
            setCalendarTime23(endCal);
        }
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2){
        if(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isSameDay(long time1, long time2){
        tempCal.setTimeInMillis(time1);
        tempCal2.setTimeInMillis(time2);
        return isSameDay(tempCal, tempCal2);
    }

    /**
     * 년월일을 카피함
     * @param toCal 바뀌는 캘린더
     */
    public static void copyYearMonthDate(Calendar toCal, Calendar fromCal) {
        toCal.set(
                fromCal.get(Calendar.YEAR),
                fromCal.get(Calendar.MONTH),
                fromCal.get(Calendar.DATE));
    }

    /**
     * 시분초를 카피함
     * @param toCal 바뀌는 캘린더
     */
    public static void copyHourMinSecMill(Calendar toCal, Calendar fromCal) {
        toCal.set(Calendar.HOUR_OF_DAY, fromCal.get(Calendar.HOUR_OF_DAY));
        toCal.set(Calendar.MINUTE, fromCal.get(Calendar.MINUTE));
        toCal.set(Calendar.SECOND, fromCal.get(Calendar.SECOND));
        toCal.set(Calendar.MILLISECOND, fromCal.get(Calendar.MILLISECOND));
    }

    /**
     * 오늘인지 체크
     */
    public static boolean isToday(Calendar calendar) {
        tempCal.setTimeInMillis(System.currentTimeMillis());
        return isSameDay(calendar, tempCal);
    }

    /**
     * 현재로부터 몇달 차이 나는지 계산
     */
    public static long getMonthDiffFromToday(Calendar cal) {
        CalendarUtil.setCalendarTodayStart(tempCal);
        int diffYear = cal.get(Calendar.YEAR) - tempCal.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + cal.get(Calendar.MONTH) - tempCal.get(Calendar.MONTH);
        if(diffMonth <= 0){
            return 0;
        }else{
            return diffMonth;
        }
    }

    /**
     * 각 시간이 몇일 차이인지 계산
     */
    public static int getDiffDate(long t1, long t2){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        if(t1 > t2){
            c1.setTimeInMillis(t2);
            c2.setTimeInMillis(t1);
        }else{
            c1.setTimeInMillis(t1);
            c2.setTimeInMillis(t2);
        }

        if(c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
            return c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        }else{
            return (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 365
                    + (c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR));
        }
    }
}
