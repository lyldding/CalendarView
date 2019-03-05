package com.lyldding.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2019/2/1
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private int mTitleTextSize;
    private int mWeekTextSize;
    private int mDayTextSize;
    private int mTextColor = 0xff267782;
    private int mOtherMonthTextColor = 0xff77adbc;
    private int mWeekBackgroundColor = 0xff88baff;
    private int mBackground = 0xff33d7ff;
    private int mSelectBackgroundColor = 0xff1157cb;
    private int mSelectTextColor = Color.RED;
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
    private Rect mDayTextRect;
    private List<RectF> mDayRectFs;
    private List<RectF> mNextMonthDayRectFs;
    private List<RectF> mLastMonthDayRectFs;
    private List<RectF> mNextYearDayRectFsV;
    private List<RectF> mLastYearDayRectFsV;
    private List<RectF> mWeekRectFs;
    private Context mContext;
    private String mTitleText;

    private float mTouchDownX;
    private float mTouchDownY;

    private List<DayBean> mDayBeans;
    private List<DayBean> mNextDayBeans;
    private List<DayBean> mLastDayBeans;
    private List<DayBean> mNextDayCellsV;
    private List<DayBean> mLastDayCellsV;
    private OnClickDayListener mOnClickDayListener;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int[] mInitDate;

    private boolean mIsClick;
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
    private boolean mIsShowSelectDate = true;
    private boolean mIsShowCurrentDate = true;
    private boolean mIsShowBorder;
    private boolean mIsBtnSwitchMonthScroll = true;
    private boolean mIsEnableClickOtherMonthDate;

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
        All,
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

        mRadius = CalendarUtils.getInstance().dip2px(context, 2.5f);
        mStrokeWidth = CalendarUtils.getInstance().dip2px(context, 1);
        mWeekHeight = CalendarUtils.getInstance().dip2px(context, 20);
        mTitleHeight = CalendarUtils.getInstance().dip2px(context, 30);

        mTitleTextSize = CalendarUtils.getInstance().dip2px(context, 15);
        mWeekTextSize = CalendarUtils.getInstance().dip2px(context, 9);
        mDayTextSize = CalendarUtils.getInstance().dip2px(context, 12);


        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        mContentScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);


        mViewRectF = new RectF();
        mTitleRectF = new RectF();
        mTitleTextRectF = new RectF();
        mLastRectF = new RectF();
        mNextRectF = new RectF();
        mDayContentRectF = new RectF();

        mDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastMonthDayRectFs = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextYearDayRectFsV = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastYearDayRectFsV = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);

        mWeekRectFs = new ArrayList<>(CalendarUtils.WEEK_COLUMN);

        mDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastDayBeans = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mNextDayCellsV = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);
        mLastDayCellsV = new ArrayList<>(CalendarUtils.DAY_CELL_NUM);


        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            mWeekRectFs.add(new RectF());
        }
        for (int row = 0; row < CalendarUtils.DAY_CELL_NUM; row++) {
            mDayRectFs.add(new RectF());
            mNextMonthDayRectFs.add(new RectF());
            mLastMonthDayRectFs.add(new RectF());

            mNextYearDayRectFsV.add(new RectF());
            mLastYearDayRectFsV.add(new RectF());
        }

        mInitDate = CalendarUtils.getInstance().getCurrentDate();
        mCurrentYear = mInitDate[0];
        mCurrentMonth = mInitDate[1];


        mViewHeight = CalendarUtils.getInstance().dip2px(getContext(), 244);
        mViewWidth = CalendarUtils.getInstance().dip2px(getContext(), 327);

        mDayTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize((int) mViewWidth, widthMeasureSpec),
                getDefaultSize(mViewHeight, heightMeasureSpec));
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        Log.d(TAG, " mViewHeight = " + mViewHeight + " mViewWidth = " + mViewWidth);
        measure();
        Log.e(TAG, "onMeasure: ok");
    }

    private void measure() {
        mViewRectF.set(0 + mStrokeWidth / 2f, 0 + mStrokeWidth / 2f, mViewWidth - mStrokeWidth / 2f, mViewHeight - mStrokeWidth / 2f);
        mViewWidth = (int) mViewRectF.width();
        mItemWidth = mViewWidth / CalendarUtils.WEEK_COLUMN;
        mDayContentHeight = mViewHeight - mTitleHeight - mWeekHeight;
        mDayItemHeight = mDayContentHeight / CalendarUtils.DAY_ROWS;

        mTitleTextRectF.set(mViewWidth / 2 - CalendarUtils.getInstance().dip2px(mContext, 85) / 2, 0, mViewWidth / 2 + CalendarUtils.getInstance().dip2px(mContext, 85) / 2, mTitleHeight);
        mNextRectF.set(mTitleTextRectF.right, 0, mTitleTextRectF.right + CalendarUtils.getInstance().dip2px(mContext, 15), mTitleHeight);
        mLastRectF.set(mTitleTextRectF.left - CalendarUtils.getInstance().dip2px(mContext, 15), 0, mTitleTextRectF.left, mTitleHeight);

        mTitleRectF.set(0, 0, mViewWidth, mTitleHeight);

        //week 参数
        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            float startX = index * mItemWidth + mStrokeWidth * 0.5f;
            mWeekRectFs.get(index).set(startX, mTitleHeight, startX + mItemWidth, mTitleHeight + mWeekHeight);
        }

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

                mNextYearDayRectFsV.get(index).set(rectF.left, rectF.top + mDayContentHeight, rectF.right, rectF.bottom + mDayContentHeight);
                mLastYearDayRectFsV.get(index).set(rectF.left, rectF.top - mDayContentHeight, rectF.right, rectF.bottom - mDayContentHeight);
            }
        }

        mDayContentRectF.set(mDayRectFs.get(0).left, mDayRectFs.get(0).top,
                mDayRectFs.get(CalendarUtils.DAY_CELL_NUM - 1).right, mDayRectFs.get(CalendarUtils.DAY_CELL_NUM - 1).bottom);

        updateMonth(mCurrentYear, mCurrentMonth, Type.NONE);
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
            updateMonth(mCurrentYear, mCurrentMonth, mScrollToType);
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
        if (mIsShowBorder) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(mTitleRectF, mPaint);
        }
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTitleTextSize);
        canvas.drawText(mTitleText, mTitleTextRectF.centerX(), getTextBaseline(mTitleTextRectF), mTextPaint);
    }

    /**
     * 切换按钮
     */
    private void drawSwitchButton(Canvas canvas) {
        //切换按钮
        mPaint.setColor(mTextColor);
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
        //Week
        for (int index = 0; index < CalendarUtils.WEEK_COLUMN; index++) {
            RectF rectF = mWeekRectFs.get(index);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mWeekBackgroundColor);
            canvas.drawRect(rectF, mPaint);
            if (mIsShowBorder) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.WHITE);
                canvas.drawRect(rectF, mPaint);
            }
            mTextPaint.setTextSize(mWeekTextSize);
            canvas.drawText(CalendarUtils.WEEKS.get(index), rectF.centerX(), getTextBaseline(rectF), mTextPaint);

        }
    }

    /**
     * 每天
     */
    private void drawAllDay(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, mViewHeight - mDayContentHeight, mViewWidth, mViewHeight);
        canvas.translate(mOffsetX, mOffsetY);
        //day
        for (int index = 0; index < CalendarUtils.DAY_CELL_NUM; index++) {
            drawDay(canvas, mDayBeans.get(index));
            drawDay(canvas, mNextDayBeans.get(index));
            drawDay(canvas, mLastDayBeans.get(index));
            drawDay(canvas, mNextDayCellsV.get(index));
            drawDay(canvas, mLastDayCellsV.get(index));
        }
        canvas.restore();
    }

    private void drawDay(Canvas canvas, DayBean dayBean) {
        if (mIsShowBorder) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(dayBean.getRectF(), mPaint);
        }

        mTextPaint.setColor(dayBean.isCurrentMonth() ? mTextColor : mOtherMonthTextColor);
        mTextPaint.setTextSize(mDayTextSize);
        if (dayBean.isCurrentMonth() &&
                ((mIsShowSelectDate && dayBean.getDateStr().equals(mSelectDateStr)) ||
                        (mIsShowCurrentDate && CalendarUtils.getInstance().isSameDay(dayBean, mInitDate)))) {
            mTextPaint.setColor(mSelectTextColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mSelectBackgroundColor);
            String day = dayBean.getDay() + "";
            float textWidth = mTextPaint.measureText(day);
            mTextPaint.getTextBounds(day, 0, 1, mDayTextRect);
            float radiusMin = (float) (Math.sqrt(textWidth * textWidth + mDayTextRect.height() * mDayTextRect.height()) / 2f);
            float radiusMax = Math.min(mItemWidth, mDayItemHeight) / 2f;
            canvas.drawCircle(dayBean.getRectF().centerX(), dayBean.getRectF().centerY(),
                    radiusMin > radiusMax ? radiusMax : (radiusMin + radiusMax) / 2f, mPaint);
        }
        canvas.drawText(String.valueOf(dayBean.getDay()), dayBean.getRectF().centerX(), getTextBaseline(dayBean.getRectF()), mTextPaint);
    }

    /**
     * 外围线
     */
    private void drawOuterLine(Canvas canvas) {
        if (mIsShowBorder) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mTextColor);
            canvas.drawRoundRect(mViewRectF, mRadius, mRadius, mPaint);
        }
    }

    /**
     * TextBaseline
     */
    private float getTextBaseline(RectF rectF) {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        return (rectF.centerY() - top / 2 - bottom / 2);
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
    private void updateMonth(int year, int month, Type type) {
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
        mDayBeans.clear();
        mDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(date[0], date[1]));
        mCurrentYear = date[0];
        mCurrentMonth = date[1];
        mTitleText = mCurrentYear + "年" + mCurrentMonth + "月";

        int[] dateNext = CalendarUtils.getInstance().getNextMonthOfYear(mCurrentYear, mCurrentMonth);
        mNextDayBeans.clear();
        mNextDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(dateNext[0], dateNext[1]));

        int[] dateLast = CalendarUtils.getInstance().getLastMonthOfYear(mCurrentYear, mCurrentMonth);
        mLastDayBeans.clear();
        mLastDayBeans.addAll(CalendarUtils.getInstance().getMonthDate(dateLast[0], dateLast[1]));

        mNextDayCellsV.clear();
        mNextDayCellsV.addAll(CalendarUtils.getInstance().getMonthDate(mCurrentYear + 1, mCurrentMonth));
        mLastDayCellsV.clear();
        mLastDayCellsV.addAll(CalendarUtils.getInstance().getMonthDate(mCurrentYear - 1, mCurrentMonth));

        for (int index = 0; index < mDayRectFs.size(); index++) {
            mDayBeans.get(index).setRectF(mDayRectFs.get(index));
            mNextDayBeans.get(index).setRectF(mNextMonthDayRectFs.get(index));
            mLastDayBeans.get(index).setRectF(mLastMonthDayRectFs.get(index));

            mNextDayCellsV.get(index).setRectF(mNextYearDayRectFsV.get(index));
            mLastDayCellsV.get(index).setRectF(mLastYearDayRectFsV.get(index));
        }
        Log.d(TAG, "updateMonth: mTitleText = " + mTitleText);
    }

    private void handleDown(MotionEvent event) {
        mTouchDownX = event.getX();
        mTouchDownY = event.getY();
        mIsClick = true;
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
                if (mIsBtnSwitchMonthScroll) {
                    startScrollOnClick(Type.NEXT);
                } else {
                    updateMonth(mCurrentYear, mCurrentMonth, Type.NEXT);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else if (isActionDownInsideRectF(mLastRectF)) {
                if (mIsBtnSwitchMonthScroll) {
                    startScrollOnClick(Type.LAST);
                } else {
                    updateMonth(mCurrentYear, mCurrentMonth, Type.LAST);
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else if (isActionDownInsideRectF(mDayContentRectF)) {
                if (mOnClickDayListener != null) {
                    for (DayBean dayBean : mDayBeans) {
                        if (dayBean.isCurrentMonth() && dayBean.isContains(mTouchDownX, mTouchDownY)) {
                            mSelectDateStr = dayBean.getDateStr();
                            mOnClickDayListener.onClickDay(dayBean.getYear(), dayBean.getMonth(), dayBean.getDay());
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

    interface OnClickDayListener {
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
    }

    /**
     * 文字颜色
     *
     * @param textColor
     */
    public void setTextColor(@ColorInt int textColor) {
        mTextColor = textColor;
    }

    /**
     * 选中日期背景色
     *
     * @param selectBackgroundColor
     */
    public void setSelectBackgroundColor(@ColorInt int selectBackgroundColor) {
        mSelectBackgroundColor = selectBackgroundColor;
    }

    /**
     * 选中日期字体颜色
     *
     * @param selectTextColor
     */
    public void setSelectTextColor(@ColorInt int selectTextColor) {
        mSelectTextColor = selectTextColor;
    }

    /**
     * @param show true 显示当前日期
     */
    public void setShowCurrentDate(boolean show) {
        mIsShowCurrentDate = show;
    }

    /**
     * @param show true 显示选中日期日期
     */
    public void setShowSelectedDate(boolean show) {
        mIsShowSelectDate = show;
    }

    /**
     * 星期背景色
     *
     * @param weekBackgroundColor
     */
    public void setWeekBackgroundColor(@ColorInt int weekBackgroundColor) {
        mWeekBackgroundColor = weekBackgroundColor;
    }

    /**
     * @param isShow true 显示边框
     */
    public void setShowBorder(boolean isShow) {
        mIsShowBorder = isShow;
    }

    /**
     * @param isScroll true 切换上下月 按钮时滑动
     */
    public void setBtnSwitchMonthScroll(boolean isScroll) {
        mIsBtnSwitchMonthScroll = isScroll;
    }

    public void setTitleTextSize(int titleTextSize) {
        mTitleTextSize = titleTextSize;
    }

    public void setWeekTextSize(int weekTextSize) {
        mWeekTextSize = weekTextSize;
    }

    public void setDayTextSize(int dayTextSize) {
        mDayTextSize = dayTextSize;
    }
}
