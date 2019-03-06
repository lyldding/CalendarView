package com.lyldding.calendarlibrary;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2019/1/1
 */
public class CalendarUtils {
    private static final String TAG = "CalendarUtils";
    public static int DAY_ROWS = 6;
    public static int WEEK_COLUMN = 7;
    public static int DAY_CELL_NUM = DAY_ROWS * WEEK_COLUMN;
    public static List<String> WEEKS = Arrays.asList("日", "一", "二", "三", "四", "五", "六");
    public static List<String> WEEKS_1 = Arrays.asList("一", "二", "三", "四", "五", "六", "日");
    /**
     * 缓存每月天数信息
     */
    private HashMap<String, List<DayBean>> mCacheMonthData;

    private CalendarUtils() {
        mCacheMonthData = new HashMap<>();
    }

    public static CalendarUtils getInstance() {
        return Holder.INSTANCE;
    }

    private final static class Holder {
        private final static CalendarUtils INSTANCE = new CalendarUtils();
    }

    public void clearCacheMonth() {
        mCacheMonthData = new HashMap<>();
    }

    /**
     * 获得当月显示的日期（上月 +当月 + 下月）
     *
     * @param year                当前年份
     * @param month               当前月份
     * @param isContainOtherMonth true 包含其他月份
     * @return
     */
    public List<DayBean> getMonthDate(int year, int month, boolean isContainOtherMonth, boolean isSundayAtFirst) {
        String key = year + "" + month;
        if (mCacheMonthData.containsKey(key)) {
            return mCacheMonthData.get(key);
        }
        List<DayBean> dayBeans = new ArrayList<>();
        int needLastMonthDays = getLastMonthDaysAtFristWeek(year, month - 1, isSundayAtFirst);

        int[] date = getLastMonthOfYear(year, month);
        int lastYear = date[0];
        int lastMonth = date[1];
        int lastMonthDays = getMonthDays(lastYear, lastMonth);
        int currentMonthDays = getMonthDays(year, month);

        date = getNextMonthOfYear(year, month);
        int nextYear = date[0];
        int nextMonth = date[1];

        for (int i = 0; i < needLastMonthDays; i++) {
            if (isContainOtherMonth) {
                dayBeans.add(createDayBean(lastYear, lastMonth, lastMonthDays - needLastMonthDays + 1 + i, false));
            } else {
                dayBeans.add(new DayBean(true));
            }
        }
        for (int i = 0; i < currentMonthDays; i++) {
            dayBeans.add(createDayBean(year, month, i + 1, true));
        }

        for (int i = 0; i < CalendarUtils.DAY_CELL_NUM - currentMonthDays - needLastMonthDays; i++) {
            if (isContainOtherMonth) {
                dayBeans.add(createDayBean(nextYear, nextMonth, i + 1, false));
            } else {
                dayBeans.add(new DayBean(true));
            }
        }
        mCacheMonthData.put(key, dayBeans);
        return dayBeans;
    }

    public int[] getNextMonthOfYear(int year, int month) {
        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }
        return new int[]{nextYear, nextMonth};
    }

    public int[] getLastMonthOfYear(int year, int month) {
        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        return new int[]{lastYear, lastMonth};
    }


    private DayBean createDayBean(int year, int month, int day, boolean isCurrentMonth) {
        DayBean dayBean = new DayBean(false);
        dayBean.setCurrentMonth(isCurrentMonth);
        dayBean.setYear(year);
        dayBean.setMonth(month);
        dayBean.setDay(day);
        return dayBean;
    }

    /**
     * 是否为同一天
     */
    public boolean isSameDay(DayBean dayBean, int[] date) {
        return date[2] == dayBean.getDay() && date[1] == dayBean.getMonth() && date[0] == dayBean.getYear();
    }

    /**
     * 计算当前日期
     */
    public int[] getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
    }

    public long dateToMillis(int[] date) {
        int day = date.length == 2 ? 1 : date[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(date[0], date[1], day);
        return calendar.getTimeInMillis();
    }


    /**
     * 计算指定月份的天数
     */
    private int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0))
                        || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }


    /**
     * 通过当月1号是周几，来确定需要显示上月的日期
     */
    private int getLastMonthDaysAtFristWeek(int year, int month, boolean isSundayAtFirst) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        Log.d(TAG, "getFirstDayOfMonthInWeek: " + calendar.get(Calendar.DAY_OF_WEEK));
        int result = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (!isSundayAtFirst) {
            if (result == 0) {
                result = 7;
            }
            result -= 1;
        }
        return result;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}