package com.lyldding.calendarlibrary;

import android.graphics.Color;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2020/7/3
 */
public class CalendarConfig {
    /**
     * 显示选中日期
     */
    private boolean isShowSelectDate = true;
    private int selectBackgroundColor = 0xff1157cb;
    private int selectTextColor = Color.RED;
    /**
     * 显示当前日期
     */
    private boolean isShowCurrentDate = true;
    private int currentBackgroundColor = 0xff1157cb;
    private int currentTextColor = Color.RED;
    /**
     * 显示边框
     */
    private boolean isShowBorder = false;
    private int borderColor = Color.GRAY;
    /**
     * 切换上下月按钮时滑动
     */
    private boolean isBtnSwitchMonthScroll = true;
    /**
     * 显示其他月份日期
     */
    private boolean isContainOtherMonthDate = true;
    private int otherMonthTextColor = Color.LTGRAY;
    /**
     * 周日为第一天，false 周一为第一天
     */
    private boolean isSundayAtFirst = true;


    /**
     * 当前月日期颜色
     */
    private int currentMonthDayTextColor = Color.BLACK;

    /**
     * 年月标题的字体size dp
     */
    private int titleTextSize = 15;

    /**
     * 整体星期标题的背景色
     */
    private int weekBackgroundColor = 0xff88baff;
    /**
     * dp
     */
    private int weekTextSize = 9;
    /**
     * 整体日历的背景色
     */
    private int background = Color.WHITE;
    /**
     * 整体日历的背景圆角dp
     */
    private int backgroundRadius = 3;
    /**
     * dp
     */
    private int dayTextSize = 12;


    public boolean isShowSelectDate() {
        return isShowSelectDate;
    }

    public int getSelectBackgroundColor() {
        return selectBackgroundColor;
    }

    public int getSelectTextColor() {
        return selectTextColor;
    }

    public boolean isShowCurrentDate() {
        return isShowCurrentDate;
    }

    public int getCurrentBackgroundColor() {
        return currentBackgroundColor;
    }

    public int getCurrentTextColor() {
        return currentTextColor;
    }

    public boolean isShowBorder() {
        return isShowBorder;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public boolean isBtnSwitchMonthScroll() {
        return isBtnSwitchMonthScroll;
    }

    public boolean isContainOtherMonthDate() {
        return isContainOtherMonthDate;
    }

    public int getOtherMonthTextColor() {
        return otherMonthTextColor;
    }

    public boolean isSundayAtFirst() {
        return isSundayAtFirst;
    }

    public int getCurrentMonthDayTextColor() {
        return currentMonthDayTextColor;
    }

    public int getBackgroundRadius() {
        return backgroundRadius;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public int getWeekBackgroundColor() {
        return weekBackgroundColor;
    }

    public int getWeekTextSize() {
        return weekTextSize;
    }

    public int getBackground() {
        return background;
    }

    public int getDayTextSize() {
        return dayTextSize;
    }

    public static final class Builder {
        private boolean isShowSelectDate = true;
        private int selectBackgroundColor = 0xff1157cb;
        private int selectTextColor = Color.RED;
        private boolean isShowCurrentDate = true;
        private int currentBackgroundColor = 0xff1157cb;
        private int currentTextColor = Color.RED;
        private boolean isShowBorder = false;
        private int borderColor = Color.GRAY;
        private boolean isBtnSwitchMonthScroll = true;
        private boolean isContainOtherMonthDate = true;
        private int otherMonthTextColor = Color.LTGRAY;
        private boolean isSundayAtFirst = true;
        private int currentMonthDayTextColor = Color.BLACK;
        private int titleTextSize = 15;
        private int weekBackgroundColor = 0xff88baff;
        private int weekTextSize = 9;
        private int background = Color.WHITE;
        private int backgroundRadius = 3;
        private int dayTextSize = 12;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withIsShowSelectDate(boolean isShowSelectDate) {
            this.isShowSelectDate = isShowSelectDate;
            return this;
        }

        public Builder withSelectBackgroundColor(int selectBackgroundColor) {
            this.selectBackgroundColor = selectBackgroundColor;
            return this;
        }

        public Builder withSelectTextColor(int selectTextColor) {
            this.selectTextColor = selectTextColor;
            return this;
        }

        public Builder withIsShowCurrentDate(boolean isShowCurrentDate) {
            this.isShowCurrentDate = isShowCurrentDate;
            return this;
        }

        public Builder withCurrentBackgroundColor(int currentBackgroundColor) {
            this.currentBackgroundColor = currentBackgroundColor;
            return this;
        }

        public Builder withCurrentTextColor(int currentTextColor) {
            this.currentTextColor = currentTextColor;
            return this;
        }

        public Builder withIsShowBorder(boolean isShowBorder) {
            this.isShowBorder = isShowBorder;
            return this;
        }

        public Builder withBorderColor(int borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        public Builder withIsBtnSwitchMonthScroll(boolean isBtnSwitchMonthScroll) {
            this.isBtnSwitchMonthScroll = isBtnSwitchMonthScroll;
            return this;
        }

        public Builder withIsContainOtherMonthDate(boolean isContainOtherMonthDate) {
            this.isContainOtherMonthDate = isContainOtherMonthDate;
            return this;
        }

        public Builder withOtherMonthTextColor(int otherMonthTextColor) {
            this.otherMonthTextColor = otherMonthTextColor;
            return this;
        }

        public Builder withIsSundayAtFirst(boolean isSundayAtFirst) {
            this.isSundayAtFirst = isSundayAtFirst;
            return this;
        }

        public Builder withCurrentMonthDayTextColor(int currentMonthDayTextColor) {
            this.currentMonthDayTextColor = currentMonthDayTextColor;
            return this;
        }

        public Builder withTitleTextSize(int titleTextSize) {
            this.titleTextSize = titleTextSize;
            return this;
        }

        public Builder withWeekBackgroundColor(int weekBackgroundColor) {
            this.weekBackgroundColor = weekBackgroundColor;
            return this;
        }

        public Builder withWeekTextSize(int weekTextSize) {
            this.weekTextSize = weekTextSize;
            return this;
        }

        public Builder withBackground(int background) {
            this.background = background;
            return this;
        }

        public Builder withBackgroundRadius(int backgroundRadius) {
            this.backgroundRadius = backgroundRadius;
            return this;
        }

        public Builder withDayTextSize(int dayTextSize) {
            this.dayTextSize = dayTextSize;
            return this;
        }

        public CalendarConfig build() {
            CalendarConfig calendarConfig = new CalendarConfig();
            calendarConfig.isShowBorder = this.isShowBorder;
            calendarConfig.backgroundRadius = this.backgroundRadius;
            calendarConfig.selectBackgroundColor = this.selectBackgroundColor;
            calendarConfig.otherMonthTextColor = this.otherMonthTextColor;
            calendarConfig.weekTextSize = this.weekTextSize;
            calendarConfig.currentBackgroundColor = this.currentBackgroundColor;
            calendarConfig.weekBackgroundColor = this.weekBackgroundColor;
            calendarConfig.isShowSelectDate = this.isShowSelectDate;
            calendarConfig.isShowCurrentDate = this.isShowCurrentDate;
            calendarConfig.isSundayAtFirst = this.isSundayAtFirst;
            calendarConfig.currentMonthDayTextColor = this.currentMonthDayTextColor;
            calendarConfig.isContainOtherMonthDate = this.isContainOtherMonthDate;
            calendarConfig.background = this.background;
            calendarConfig.titleTextSize = this.titleTextSize;
            calendarConfig.dayTextSize = this.dayTextSize;
            calendarConfig.currentTextColor = this.currentTextColor;
            calendarConfig.isBtnSwitchMonthScroll = this.isBtnSwitchMonthScroll;
            calendarConfig.selectTextColor = this.selectTextColor;
            calendarConfig.borderColor = this.borderColor;
            return calendarConfig;
        }
    }
}
