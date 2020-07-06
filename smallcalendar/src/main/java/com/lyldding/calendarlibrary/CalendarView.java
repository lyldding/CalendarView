package com.lyldding.calendarlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2019/1/1
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private int mTitleTextSize;
    private int mWeekTextSize;
    private int mDayTextSize;

    private int mWeekBackgroundColor = 0xff88baff;
    private int mBackground = Color.WHITE;


    private int mRadius;
    private int mStrokeWidth;
    private float mItemWidth;
    private int mDayContentHeight;
    private int mDayItemHeight;
    private int mWeekHeight;
    private int mTitleHeight;
    private Paint mPaint;
    private Paint mTextPaint;
    private int mViewHeight;
    private float mViewWidth;
    private RectF mViewRectF;
    private RectF mTitleRectF;
    private RectF mTitleTextRectF;
    private RectF mLastRectF;
    private RectF mNextRectF;
    private RectF mDayContentRectF;
    private List<RectF> mDayRectFs;
    private List<RectF> mNextMonthDayRectFs;
    private List<RectF> mLastMonthDayRectFs;
    private List<RectF> mNextYearMonthDayRectFs;
    private List<RectF> mLastYearMonthDayRectFs;
    private List<RectF> mWeekRectFs;
    private Context mContext;
    private String mTitleText;

    private float mTouchDownX;
    private float mTouchDownY;

    private List<DayBean> mCurrentDayBeans;
    private List<DayBean> mNextMonthDayBeans;
    private List<DayBean> mLastMonthDayBeans;
    private List<DayBean> mNextYearMonthDayBeans;
    private List<DayBean> mLastYearMonthDayBeans;
    private OnClickDayListener mOnClickDayListener;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int[] mInitDate;

    private boolean mIsClick;
    private boolean mIsMoved = false;
    private boolean mIsDrawing;
    private boolean mIsScrolling;
    private int mTouchSlop;
    private float mOffsetX;
    private float mOffsetY;
    private Type mScrollToType;

    private Scroller mContentScroller;
    private VelocityTracker mVelocityTracker;

    private ScrollOrientation mScrollOrientation = ScrollOrientation.None;
    private String mSelectDateStr;
    private CalendarConfig mConfig;

    enum Type {
        /**
         * 上个月，下个月，当前月
         */
        LAST,
        NEXT,
        /**
         * 上一年，下一年
         */
        LAST_YEAR,
        NEXT_YEAR,
        NONE
    }

    enum ScrollOrientation {
        /**
         * 滑动方向
         */
        Vertical,
        Horizontal,
        None
    }


    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        if (mConfig == null) {
            mConfig = createDefaultConfig();
        }
        initSize();

        mTouchSlop = CalendarUtils.getInstance().dip2px(mContext, 1);
        mContentScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();

        initPaint();
        initRectF();

        mInitDate = CalendarUtils.getInstance().getCurrentDate();
        mCurrentYear = mInitDate[0];
        mCurrentMonth = mInitDate[1];

        mViewHeight = CalendarUtils.getInstance().dip2px(getContext(), 244);
        mViewWidth = CalendarUtils.getInstance().dip2px(getContext(), 327);
    }

    private CalendarConfig createDefaultConfig() {
        return CalendarConfig.Builder.newBuilder()
                .withIsShowSelectDate(true)
                .withSelectTextColor(Color.RED)
                .withSelectBackgroundColor(0xff1157cb)
                .withCurrentTextColor(Color.RED)
                .withCurrentBackgroundColor(0xff1157cb)
                .withIsShowBorder(false)
                .withBorderColor(Color.GRAY)
                .withIsContainOtherMonthDate(true)
                .withOtherMonthTextColor(Color.LTGRAY)
                .withCurrentMonthDayTextColor(Color.BLACK)
                .withIsSundayAtFirst(true)
                .withIsBtnSwitchMonthScroll(true)
                .build();
    }

    public void setConfig(CalendarConfig config) {
        if (config != null) {
            mConfig = config;
            initSize();
            CalendarUtils.getInstance().clearCacheMonth();
            updateMonthData(mCurrentYear, mCurrentMonth, Type.NONE);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void initSize() {
        mRadius = CalendarUtils.getInstance().dip2px(mContext, mConfig.getBackgroundRadius());
        mStrokeWidth = CalendarUtils.getInstance().dip2px(mContext, 1);
        mWeekHeight = CalendarUtils.getInstance().dip2px(mContext, 20);
        mTitleHeight = CalendarUtils.getInstance().dip2px(mContext, 30);

        mTitleTextSize = CalendarUtils.getInstance().dip2px(mContext, mConfig.getTitleTextSize());
        mWeekTextSize = CalendarUtils.getInstance().dip2px(mContext, mConfig.getWeekTextSize());
        mDayTextSize = CalendarUtils.getInstance().dip2px(mContext, mConfig.getDayTextSize());
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mConfig.getCurrentMonthDayTextColor());
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    private void initRectF() {
        mViewRectF = new RectF();
        mTitleRectF = new RectF();
        mTitleTextRectF = new RectF();
        mLastRectF = new RectF();
        mNextRectF = new RectF();
        mDayContentRectF = new RectF();

        mDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextYearMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastYearMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);

        mWeekRectFs = new ArrayList<>(CalendarUtils.WEEK_COLUMN);

        mCurrentDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextMonthDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastMonthDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextYearMonthDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastYearMonthDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);


        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            mWeekRectFs.add(new RectF());
        }
        for (int row = 0; row < CalendarUtils.DAY_CELL_NUM; row++) {
            mDayRectFs.add(new RectF());
            mNextMonthDayRectFs.add(new RectF());
            mLastMonthDayRectFs.add(new RectF());

            mNextYearMonthDayRectFs.add(new RectF());
            mLastYearMonthDayRectFs.add(new RectF());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize((int) mViewWidth, widthMeasureSpec),
                getDefaultSize(mViewHeight, heightMeasureSpec));
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        Log.d(TAG, " mViewHeight = " + mViewHeight + " mViewWidth = " + mViewWidth);
        computeData();
        Log.e(TAG, "onMeasure: ok");
    }

    private void computeData() {
        mViewRectF.set(0 + mStrokeWidth / 2f, 0 + mStrokeWidth / 2f, mViewWidth - mStrokeWidth / 2f, mViewHeight - mStrokeWidth / 2f);
        mViewWidth = (int) mViewRectF.width();
        mItemWidth = mViewWidth / CalendarUtils.WEEK_COLUMN;
        computeTitleData();
        computeWeekData();
        computeDayData();
        updateMonthData(mCurrentYear, mCurrentMonth, Type.NONE);
    }

    private void computeTitleData() {
        //title 参数
        mTitleTextRectF.set(mViewWidth / 2 - CalendarUtils.getInstance().dip2px(mContext, 85) / 2f, 0, mViewWidth / 2 + CalendarUtils.getInstance().dip2px(mContext, 85) / 2f, mTitleHeight);
        mNextRectF.set(mTitleTextRectF.right, 0, mTitleTextRectF.right + CalendarUtils.getInstance().dip2px(mContext, 15), mTitleHeight);
        mLastRectF.set(mTitleTextRectF.left - CalendarUtils.getInstance().dip2px(mContext, 15), 0, mTitleTextRectF.left, mTitleHeight);
        mTitleRectF.set(0, 0, mViewWidth, mTitleHeight);
    }

    private void computeWeekData() {
        //week 参数
        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            float startX = index * mItemWidth + mStrokeWidth * 0.5f;
            mWeekRectFs.get(index).set(startX, mTitleHeight, startX + mItemWidth, mTitleHeight + mWeekHeight);
        }
    }

    private void computeDayData() {
        mDayContentHeight = mViewHeight - mTitleHeight - mWeekHeight;
        mDayItemHeight = mDayContentHeight / CalendarUtils.DAY_ROWS;
        //day 参数
        for (int row = 0; row < CalendarUtils.DAY_ROWS; row++) {
            float startY = mTitleHeight + mWeekHeight + row * mDayItemHeight;
            for (int column = 0; column < CalendarUtils.WEEK_COLUMN; column++) {
                int index = row * CalendarUtils.WEEK_COLUMN + column;
                float startX = column * mItemWidth + mViewRectF.left;
                RectF rectF = mDayRectFs.get(index);
                rectF.set(startX, startY, startX + mItemWidth, startY + mDayItemHeight);
                mNextMonthDayRectFs.get(index).set(rectF.left + mViewWidth, rectF.top, rectF.right + mViewWidth, rectF.bottom);
                mLastMonthDayRectFs.get(index).set(rectF.left - mViewWidth, rectF.top, rectF.right - mViewWidth, rectF.bottom);

                mNextYearMonthDayRectFs.get(index).set(rectF.left, rectF.top + mDayContentHeight, rectF.right, rectF.bottom + mDayContentHeight);
                mLastYearMonthDayRectFs.get(index).set(rectF.left, rectF.top - mDayContentHeight, rectF.right, rectF.bottom - mDayContentHeight);
            }
        }
        mDayContentRectF.set(mDayRectFs.get(0).left, mDayRectFs.get(0).top,
                mDayRectFs.get(CalendarUtils.DAY_CELL_NUM - 1).right, mDayRectFs.get(CalendarUtils.DAY_CELL_NUM - 1).bottom);
    }


    public static int getDefaultSize(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(defaultSize, specSize);
                break;
            default:
        }
        return result;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mContentScroller.computeScrollOffset()) {
            mOffsetX = mContentScroller.getCurrX();
            mOffsetY = mContentScroller.getCurrY();
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (mIsScrolling) {
            mIsScrolling = false;
            mOffsetX = 0;
            mOffsetY = 0;
            updateMonthData(mCurrentYear, mCurrentMonth, mScrollToType);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mIsDrawing = true;
        drawBackground(canvas);
        drawDataStr(canvas);
        drawSwitchButton(canvas);
        drawWeek(canvas);
        drawAllDay(canvas);
        drawOuterLine(canvas);
        mIsDrawing = false;
    }

    /**
     * 背景
     */
    private void drawBackground(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackground);
        canvas.drawRoundRect(mViewRectF, mRadius, mRadius, mPaint);
    }

    /**
     * 年月
     */
    private void drawDataStr(Canvas canvas) {
        if (mConfig.isShowBorder()) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(mTitleRectF, mPaint);
        }
        mTextPaint.setColor(mConfig.getCurrentMonthDayTextColor());
        mTextPaint.setTextSize(mTitleTextSize);
        canvas.drawText(mTitleText, mTitleTextRectF.centerX(), getTextBaseline(mTitleTextRectF), mTextPaint);
    }

    /**
     * 切换按钮
     */
    private void drawSwitchButton(Canvas canvas) {
        mPaint.setColor(mConfig.getCurrentMonthDayTextColor());
        int y = CalendarUtils.getInstance().dip2px(mContext, 4.5f);
        int x = CalendarUtils.getInstance().dip2px(mContext, 5.5f);
        canvas.drawLine(mLastRectF.centerX() - mLastRectF.width() / 4,
                mLastRectF.centerY(), mLastRectF.centerX() + x - mLastRectF.width() / 4, mLastRectF.centerY() - y, mPaint);
        canvas.drawLine(mLastRectF.centerX() - mLastRectF.width() / 4,
                mLastRectF.centerY(), mLastRectF.centerX() + x - mLastRectF.width() / 4, mLastRectF.centerY() + y, mPaint);

        canvas.drawLine(mNextRectF.centerX() + mNextRectF.width() / 4,
                mNextRectF.centerY(), mNextRectF.centerX() - x + mNextRectF.width() / 4, mNextRectF.centerY() - y, mPaint);
        canvas.drawLine(mNextRectF.centerX() + mNextRectF.width() / 4,
                mNextRectF.centerY(), mNextRectF.centerX() - x + mNextRectF.width() / 4, mNextRectF.centerY() + y, mPaint);

    }

    /**
     * 星期
     */
    private void drawWeek(Canvas canvas) {
        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            RectF rectF = mWeekRectFs.get(index);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mWeekBackgroundColor);
            canvas.drawRect(rectF, mPaint);
            if (mConfig.isShowBorder()) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(mConfig.getBorderColor());
                canvas.drawRect(rectF, mPaint);
            }
            mTextPaint.setTextSize(mWeekTextSize);
            canvas.drawText(mConfig.isSundayAtFirst() ? CalendarUtils.WEEKS.get(index) : CalendarUtils.WEEKS_1.get(index),
                    rectF.centerX(), getTextBaseline(rectF), mTextPaint);

        }
    }

    /**
     * 每天
     */
    private void drawAllDay(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mViewHeight - mDayContentHeight, mViewWidth, mViewHeight);
        canvas.translate(mOffsetX, mOffsetY);
        for (int index = 0; index < CalendarUtils.DAY_CELL_NUM; index++) {
            drawDay(canvas, mCurrentDayBeans.get(index));
            drawDay(canvas, mNextMonthDayBeans.get(index));
            drawDay(canvas, mLastMonthDayBeans.get(index));
            drawDay(canvas, mNextYearMonthDayBeans.get(index));
            drawDay(canvas, mLastYearMonthDayBeans.get(index));
        }
        canvas.restore();
    }

    private void drawDay(Canvas canvas, DayBean dayBean) {
        if (mConfig.isShowBorder()) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mConfig.getBorderColor());
            canvas.drawRect(dayBean.getRectF(), mPaint);
        }
        if (dayBean.isEmpty()) {
            return;
        }
        mTextPaint.setColor(dayBean.isCurrentMonth() ? mConfig.getCurrentMonthDayTextColor() : mConfig.getOtherMonthTextColor());
        mTextPaint.setTextSize(mDayTextSize);
        //画选择日期和当前日期
        if (dayBean.isCurrentMonth() &&
                ((mConfig.isShowSelectDate() && dayBean.getDateStr().equals(mSelectDateStr)) ||
                        (mConfig.isShowCurrentDate() && CalendarUtils.getInstance().isSameDay(dayBean, mInitDate)))) {
            mTextPaint.setColor(mConfig.getSelectTextColor());
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mConfig.getSelectBackgroundColor());
            canvas.drawCircle(dayBean.getRectF().centerX(), dayBean.getRectF().centerY(),
                    Math.min(dayBean.getRectF().width(), dayBean.getRectF().height()) / 3f, mPaint);
        }
        canvas.drawText(String.valueOf(dayBean.getDay()), dayBean.getRectF().centerX(), getTextBaseline(dayBean.getRectF()), mTextPaint);
    }

    /**
     * 外围线
     */
    private void drawOuterLine(Canvas canvas) {
        if (mConfig.isShowBorder()) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mConfig.getBorderColor());
            canvas.drawRoundRect(mViewRectF, mRadius, mRadius, mPaint);
        }
    }

    /**
     * TextBaseline
     */
    private float getTextBaseline(RectF rectF) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        return rectF.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsScrolling || mIsDrawing) {
            Log.d(TAG, "onTouchEvent:  can not touch.");
            return false;
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleUp(event);
                break;
            default:
                Log.d(TAG, "onTouchEvent:  event  Action =" + event.getAction());
        }
        return true;
    }

    /**
     * 更新月份信息
     *
     * @param year 当前展示的年 月 日
     * @param type 类型
     */
    private void updateMonthData(int year, int month, Type type) {
        int[] date = new int[]{year, month};
        switch (type) {
            case LAST:
                date = CalendarUtils.getInstance().getLastMonthOfYear(year, month);
                break;
            case NEXT:
                date = CalendarUtils.getInstance().getNextMonthOfYear(year, month);
                break;
            case LAST_YEAR:
                date[0] = year - 1;
                break;
            case NEXT_YEAR:
                date[0] = year + 1;
                break;
            default:
        }
        mCurrentDayBeans.clear();
        mCurrentDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(date[0], date[1],
                mConfig.isContainOtherMonthDate(), mConfig.isSundayAtFirst()));
        mCurrentYear = date[0];
        mCurrentMonth = date[1];
        mTitleText = mCurrentYear + "年" + mCurrentMonth + "月";

        int[] dateNext = CalendarUtils.getInstance().getNextMonthOfYear(mCurrentYear, mCurrentMonth);
        mNextMonthDayBeans.clear();
        mNextMonthDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(dateNext[0], dateNext[1],
                mConfig.isContainOtherMonthDate(), mConfig.isSundayAtFirst()));

        int[] dateLast = CalendarUtils.getInstance().getLastMonthOfYear(mCurrentYear, mCurrentMonth);
        mLastMonthDayBeans.clear();
        mLastMonthDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(dateLast[0], dateLast[1],
                mConfig.isContainOtherMonthDate(), mConfig.isSundayAtFirst()));

        mNextYearMonthDayBeans.clear();
        mNextYearMonthDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(mCurrentYear + 1, mCurrentMonth,
                mConfig.isContainOtherMonthDate(), mConfig.isSundayAtFirst()));
        mLastYearMonthDayBeans.clear();
        mLastYearMonthDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(mCurrentYear - 1, mCurrentMonth,
                mConfig.isContainOtherMonthDate(), mConfig.isSundayAtFirst()));

        for (int index = 0; index < mDayRectFs.size(); index++) {
            mCurrentDayBeans.get(index).setRectF(mDayRectFs.get(index));
            mNextMonthDayBeans.get(index).setRectF(mNextMonthDayRectFs.get(index));
            mLastMonthDayBeans.get(index).setRectF(mLastMonthDayRectFs.get(index));

            mNextYearMonthDayBeans.get(index).setRectF(mNextYearMonthDayRectFs.get(index));
            mLastYearMonthDayBeans.get(index).setRectF(mLastYearMonthDayRectFs.get(index));
        }
        Log.d(TAG, "updateMonth: mTitleText = " + mTitleText);
    }

    private void handleDown(MotionEvent event) {
        mTouchDownX = event.getX();
        mTouchDownY = event.getY();
        mIsClick = true;
        mIsMoved = false;
    }

    private void handleMove(MotionEvent event) {
        mOffsetX = event.getX() - mTouchDownX;
        mOffsetY = event.getY() - mTouchDownY;
        if (Math.abs(mOffsetY) >= mDayContentHeight) {
            mOffsetY = mOffsetY > 0 ? mDayContentHeight : -mDayContentHeight;
        }

        if (Math.abs(mOffsetX) >= mViewWidth) {
            mOffsetX = mOffsetX > 0 ? mViewWidth : -mViewWidth;
        }

        if (Math.abs(mOffsetX) > mTouchSlop || Math.abs(mOffsetY) > mTouchSlop) {
            mIsClick = false;
            mIsMoved = true;
            Log.d(TAG, "handleMove: 111111 = ");
        } else {
            Log.d(TAG, "handleMove: 2222 = ");
        }
        if (mIsScrolling ||
                !mDayContentRectF.contains(mTouchDownX, mTouchDownY)) {
            return;
        }

        if (mScrollOrientation == ScrollOrientation.None) {
            if (Math.abs(mOffsetX) > Math.abs(mOffsetY)) {
                mScrollOrientation = ScrollOrientation.Horizontal;
            } else if (Math.abs(mOffsetX) < Math.abs(mOffsetY)) {
                mScrollOrientation = ScrollOrientation.Vertical;
            }
        }
        if (mScrollOrientation == ScrollOrientation.Horizontal) {
            mOffsetY = 0;
        } else if (mScrollOrientation == ScrollOrientation.Vertical) {
            mOffsetX = 0;
        } else {
            return;
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void handleUp(MotionEvent event) {
        if (mIsClick) {
            mOffsetX = 0;
            mOffsetY = 0;
            if (isActionDownInsideRectF(mNextRectF)) {
                if (mConfig.isBtnSwitchMonthScroll()) {
                    startScrollOnClick(Type.NEXT);
                } else {
                    updateMonthData(mCurrentYear, mCurrentMonth, Type.NEXT);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else if (isActionDownInsideRectF(mLastRectF)) {
                if (mConfig.isBtnSwitchMonthScroll()) {
                    startScrollOnClick(Type.LAST);
                } else {
                    updateMonthData(mCurrentYear, mCurrentMonth, Type.LAST);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else if (!mIsMoved && isActionDownInsideRectF(mDayContentRectF)) {
                if (mOnClickDayListener != null) {
                    for (DayBean dayBean : mCurrentDayBeans) {
                        if (dayBean.isCurrentMonth() && dayBean.isContains(mTouchDownX, mTouchDownY)) {
                            //双击清空选中状态
                            if (dayBean.getDateStr().equals(mSelectDateStr)) {
                                mSelectDateStr = "";
                            } else {
                                mSelectDateStr = dayBean.getDateStr();
                                mOnClickDayListener.onClickDay(dayBean.getYear(), dayBean.getMonth(), dayBean.getDay());
                            }
                            ViewCompat.postInvalidateOnAnimation(this);
                            break;
                        }
                    }
                }
            }
        } else if (isActionDownInsideRectF(mDayContentRectF)) {
            startScroll(event);
        }
        mIsClick = false;
        mIsMoved = false;
        mScrollOrientation = ScrollOrientation.None;
    }

    private boolean isActionDownInsideRectF(RectF rectF) {
        return rectF.contains(mTouchDownX, mTouchDownY);
    }

    /**
     * 点击 last next button 滑动
     *
     * @param type
     */
    private void startScrollOnClick(Type type) {
        mScrollToType = type;
        int dx = 0;
        if (type == Type.NEXT) {
            dx = (int) -mViewWidth;
        } else if (type == Type.LAST) {
            dx = (int) mViewWidth;
        }
        if (dx != 0) {
            mIsScrolling = true;
            mContentScroller.startScroll(0, 0, dx, 0, 500);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 触摸滑动
     *
     * @param event
     */
    private void startScroll(MotionEvent event) {
        mIsScrolling = true;
        mVelocityTracker.computeCurrentVelocity(1000);
        if (mScrollOrientation == ScrollOrientation.Horizontal) {
            int dx = 0;
            if (mVelocityTracker.getXVelocity() < -200 || mOffsetX < -mViewWidth * 0.3) {
                dx = (int) -(mViewWidth + mOffsetX);
                mScrollToType = Type.NEXT;
            } else if (mVelocityTracker.getXVelocity() > 200 || mOffsetX > mViewWidth * 0.3) {
                dx = (int) (mViewWidth - mOffsetX);
                mScrollToType = Type.LAST;
            } else {
                dx = (int) -mOffsetX;
                mScrollToType = Type.NONE;
            }
            mContentScroller.startScroll((int) mOffsetX, 0, dx, 0, 500);
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (mScrollOrientation == ScrollOrientation.Vertical) {
            int dY = 0;
            if (mVelocityTracker.getYVelocity() < -200 || mOffsetY < -mDayContentHeight * 0.3) {
                dY = (int) -(mDayContentHeight + mOffsetY);
                mScrollToType = Type.NEXT_YEAR;
            } else if (mVelocityTracker.getYVelocity() > 200 || mOffsetY > mDayContentHeight * 0.3) {
                dY = (int) (mDayContentHeight - mOffsetY);
                mScrollToType = Type.LAST_YEAR;
            } else {
                dY = (int) -mOffsetY;
                mScrollToType = Type.NONE;
            }
            mContentScroller.startScroll(0, (int) mOffsetY, 0, dY, 500);
            ViewCompat.postInvalidateOnAnimation(this);
        }
        mVelocityTracker.clear();
    }

    public interface OnClickDayListener {
        /**
         * 选择日期
         */
        void onClickDay(int year, int month, int day);
    }

    public void setOnClickDayListener(OnClickDayListener onClickDayListener) {
        mOnClickDayListener = onClickDayListener;
    }

    public void setBackground(@ColorInt int background) {
        mBackground = background;
        ViewCompat.postInvalidateOnAnimation(this);
    }


    /**
     * 星期背景色
     *
     * @param weekBackgroundColor
     */
    public void setWeekBackgroundColor(@ColorInt int weekBackgroundColor) {
        mWeekBackgroundColor = weekBackgroundColor;
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
