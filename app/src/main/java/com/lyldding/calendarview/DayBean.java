package com.lyldding.calendarview;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2019/2/1
 */
public class DayBean implements Serializable {
    private RectF mRectF;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mTextColor;
    private boolean mIsCurrentMonth;
    private boolean mIsEmpty;

    DayBean(boolean isEmpty) {
        mIsEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }

    public void setEmpty(boolean empty) {
        mIsEmpty = empty;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public boolean isCurrentMonth() {
        return mIsCurrentMonth;
    }

    public void setCurrentMonth(boolean currentMonth) {
        mIsCurrentMonth = currentMonth;
    }

    public RectF getRectF() {
        return mRectF;
    }

    public void setRectF(RectF rectF) {
        mRectF = rectF;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public boolean isContains(float x, float y) {
        return mRectF.contains(x, y);
    }

    public String getDateStr() {
        return mYear + "-" + mMonth + "-" + mDay;
    }
}
