package com.lyldding.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
 * @date 2019/2/28
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";
    private int mTextColor = 0xff453869;
    private int mTextOtherMonthColor = 0xffafadbc;
    private int mWeekBackgroundColor = 0xffd2baff;
    private int mBackground = 0xffe5d7ff;
    private int mSelectColor = 0xff8157cb;
    private int mRadius;
    private int mStrokeWidth;
    private int mItemWidth;
    private int mDayHeight;
    private int mWeekHeight;
    private int mTitleHeight;
    private Paint mPaint;
    private Paint mTextPaint;
    private int mViewHeight;
    private int mViewWidth;
    private RectF mViewRectF;
    private RectF mTitleRectF;
    private RectF mTitleTextRectF;
    private RectF mLastRectF;
    private RectF mNextRectF;
    private RectF mDayContentRectF;
    private Rect mDayTextRect;
    private List<RectF> mDayRectFs;
    private List<RectF> mNextDayRectFs;
    private List<RectF> mLastDayRectFs;
    private List<RectF> mWeekRectFs;
    private Context mContext;
    private String mTitleText;

    private float mTouchDownX;
    private float mTouchDownY;

    private List<DayCell> mDayCells;
    private List<DayCell> mNextDayCells;
    private List<DayCell> mLastDayCells;
    private OnClickDayListener mOnClickDayListener;

    private int mCurrentYear;
    private int mCurrentMonth;
    private int[] mInitDate;

    private boolean mIsClick;
    private boolean mIsDrawing;
    private boolean mIsScrolling;
    private int mTouchSlop;
    private float mOffsetX;
    private Type mScrollToType;

    private Scroller mContentScroller;
    private VelocityTracker mVelocityTracker;

    enum Type {
        /**
         * 上个月，下个月，当前月
         */
        LAST,
        NEXT,
        NONE
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

        mRadius = CalendarManager.getInstance().dip2px(context, 2.5f);
        mStrokeWidth = CalendarManager.getInstance().dip2px(context, 1);
        mItemWidth = CalendarManager.getInstance().dip2px(context, 45);
        mDayHeight = CalendarManager.getInstance().dip2px(context, 30);
        mWeekHeight = CalendarManager.getInstance().dip2px(context, 20);
        mTitleHeight = CalendarManager.getInstance().dip2px(context, 30);

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

        mDayRectFs = new ArrayList<>(CalendarManager.DAY_CELL_NUM);
        mNextDayRectFs = new ArrayList<>(CalendarManager.DAY_CELL_NUM);
        mLastDayRectFs = new ArrayList<>(CalendarManager.DAY_CELL_NUM);

        mWeekRectFs = new ArrayList<>(CalendarManager.WEEK_COLUMN);

        mDayCells = new ArrayList<>(CalendarManager.DAY_CELL_NUM);
        mNextDayCells = new ArrayList<>(CalendarManager.DAY_CELL_NUM);
        mLastDayCells = new ArrayList<>(CalendarManager.DAY_CELL_NUM);


        for (int index = 0; index < CalendarManager.WEEK_COLUMN; index++) {
            mWeekRectFs.add(new RectF());
        }
        for (int row = 0; row < CalendarManager.DAY_CELL_NUM; row++) {
            mDayRectFs.add(new RectF());
            mNextDayRectFs.add(new RectF());
            mLastDayRectFs.add(new RectF());
        }

        mInitDate = CalendarManager.getInstance().getCurrentDate();
        mCurrentYear = mInitDate[0];
        mCurrentMonth = mInitDate[1];


        mViewHeight = CalendarManager.getInstance().dip2px(getContext(), 244);
        mViewWidth = CalendarManager.getInstance().dip2px(getContext(), 327);

        mDayTextRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(mViewWidth, widthMeasureSpec),
                getDefaultSize(mViewHeight, heightMeasureSpec));
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        Log.d(TAG, " mViewHeight = " + mViewHeight + " mViewWidth = " + mViewWidth);
        mViewRectF.set(0 + mStrokeWidth / 2f, 0 + mStrokeWidth / 2f, mViewWidth - mStrokeWidth / 2f, mViewHeight - mStrokeWidth / 2f);
        mItemWidth = (mViewWidth - mStrokeWidth) / CalendarManager.WEEK_COLUMN;
        mDayHeight = (mViewHeight - mTitleHeight - mWeekHeight) / CalendarManager.DAY_ROWS;

        mTitleTextRectF.set(mViewWidth / 2 - CalendarManager.getInstance().dip2px(mContext, 85) / 2, 0, mViewWidth / 2 + CalendarManager.getInstance().dip2px(mContext, 85) / 2, mTitleHeight);
        mNextRectF.set(mTitleTextRectF.right, 0, mTitleTextRectF.right + CalendarManager.getInstance().dip2px(mContext, 15), mTitleHeight);
        mLastRectF.set(mTitleTextRectF.left - CalendarManager.getInstance().dip2px(mContext, 15), 0, mTitleTextRectF.left, mTitleHeight);

        mTitleRectF.set(0, 0, mViewWidth, mTitleHeight);

        for (int index = 0; index < CalendarManager.WEEK_COLUMN; index++) {
            float startX = index * mItemWidth + mStrokeWidth * 1.5f;
            mWeekRectFs.get(index).set(startX, mTitleHeight, startX + mItemWidth, mTitleHeight + mWeekHeight);
        }

        for (int row = 0; row < CalendarManager.DAY_ROWS; row++) {
            int startY = mTitleHeight + mWeekHeight + row * mDayHeight;
            for (int column = 0; column < CalendarManager.WEEK_COLUMN; column++) {
                int index = row * CalendarManager.WEEK_COLUMN + column;
                float startX = column * mItemWidth + mStrokeWidth * 1.5f;
                RectF rectF = mDayRectFs.get(index);
                rectF.set(startX, startY, startX + mItemWidth, startY + mDayHeight);
                mNextDayRectFs.get(index).set(rectF.left + mViewWidth, rectF.top, rectF.right + mViewWidth, rectF.bottom);
                mLastDayRectFs.get(index).set(rectF.left - mViewWidth, rectF.top, rectF.right - mViewWidth, rectF.bottom);
            }
        }

        mDayContentRectF.set(mDayRectFs.get(0).left, mDayRectFs.get(0).top,
                mDayRectFs.get(CalendarManager.DAY_CELL_NUM - 1).right, mDayRectFs.get(CalendarManager.DAY_CELL_NUM - 1).bottom);

        updateMonth(mCurrentYear, mCurrentMonth, Type.NONE);

        Log.e(TAG, "onMeasure: ok");
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
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (mIsScrolling) {
            mIsScrolling = false;
            mOffsetX = 0;
            updateMonth(mCurrentYear, mCurrentMonth, mScrollToType);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mIsDrawing = true;
        drawTitle(canvas);
        drawButton(canvas);
        drawWeek(canvas);
        drawDay(canvas);
        drawOuter(canvas);
        mIsDrawing = false;
    }

    private void drawTitle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(mTitleRectF, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackground);
        canvas.drawRect(mTitleRectF, mPaint);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(CalendarManager.getInstance().dip2px(mContext, 15));
        canvas.drawText(mTitleText, mTitleTextRectF.centerX(), getBaseline(mTitleTextRectF), mTextPaint);

    }

    private void drawButton(Canvas canvas) {
        //切换按钮
        mPaint.setColor(mTextColor);
        int y = CalendarManager.getInstance().dip2px(mContext, 4.5f);
        int x = CalendarManager.getInstance().dip2px(mContext, 5.5f);
        canvas.drawLine(mLastRectF.centerX() - mLastRectF.width() / 4,
                mLastRectF.centerY(), mLastRectF.centerX() + x - mLastRectF.width() / 4, mLastRectF.centerY() - y, mPaint);
        canvas.drawLine(mLastRectF.centerX() - mLastRectF.width() / 4,
                mLastRectF.centerY(), mLastRectF.centerX() + x - mLastRectF.width() / 4, mLastRectF.centerY() + y, mPaint);

        canvas.drawLine(mNextRectF.centerX() + mNextRectF.width() / 4,
                mNextRectF.centerY(), mNextRectF.centerX() - x + mNextRectF.width() / 4, mNextRectF.centerY() - y, mPaint);
        canvas.drawLine(mNextRectF.centerX() + mNextRectF.width() / 4,
                mNextRectF.centerY(), mNextRectF.centerX() - x + mNextRectF.width() / 4, mNextRectF.centerY() + y, mPaint);

    }

    private void drawWeek(Canvas canvas) {
        //Week
        for (int index = 0; index < CalendarManager.WEEK_COLUMN; index++) {
            RectF rectF = mWeekRectFs.get(index);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.WHITE);
            canvas.drawRect(rectF, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mWeekBackgroundColor);
            canvas.drawRect(rectF, mPaint);

            mTextPaint.setTextSize(CalendarManager.getInstance().dip2px(mContext, 9));
            canvas.drawText(CalendarManager.WEEKS.get(index), rectF.centerX(), getBaseline(rectF), mTextPaint);

        }
    }

    private void drawDay(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, 0);
        //day
        for (int index = 0; index < CalendarManager.DAY_CELL_NUM; index++) {
            drawDayItem(canvas, mDayCells.get(index));
            drawDayItem(canvas, mNextDayCells.get(index));
            drawDayItem(canvas, mLastDayCells.get(index));
        }
        canvas.restore();
    }

    private void drawDayItem(Canvas canvas, DayCell dayCell) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        canvas.drawRect(dayCell.getRectF(), mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackground);
        canvas.drawRect(dayCell.getRectF(), mPaint);

        mTextPaint.setColor(dayCell.isCurrentMonth() ? mTextColor : mTextOtherMonthColor);
        mTextPaint.setTextSize(CalendarManager.getInstance().dip2px(mContext, 12));
        if (dayCell.isCurrentMonth() && CalendarManager.getInstance().isSameDay(dayCell, mInitDate)) {
            mTextPaint.setColor(Color.WHITE);
            mPaint.setColor(mSelectColor);
            String day = dayCell.getDay() + "";
            float textWidth = mTextPaint.measureText(day);
            mTextPaint.getTextBounds(day, 0, 1, mDayTextRect);
            float radiusMin = (float) (Math.sqrt(textWidth * textWidth + mDayTextRect.height() * mDayTextRect.height()) / 2f);
            float radiusMax = Math.min(mItemWidth, mDayHeight) / 2f;
            canvas.drawCircle(dayCell.getRectF().centerX(), dayCell.getRectF().centerY(),
                    radiusMin > radiusMax ? radiusMax : (radiusMin + radiusMax) / 2f, mPaint);
        }
        canvas.drawText(String.valueOf(dayCell.getDay()), dayCell.getRectF().centerX(), getBaseline(dayCell.getRectF()), mTextPaint);
    }

    private void drawOuter(Canvas canvas) {
        //外围
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mTextColor);
        canvas.drawRoundRect(mViewRectF, mRadius, mRadius, mPaint);
    }

    private float getBaseline(RectF rectF) {
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
                mTouchDownX = event.getX();
                mTouchDownY = event.getY();
                mIsClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(event);
                break;
            default:
                Log.d(TAG, "onTouchEvent:  event  Action =" + event.getAction());
        }
        return true;
    }

    private void updateMonth(int year, int month, Type type) {
        int[] date = new int[]{year, month};
        switch (type) {
            case LAST:
                date = CalendarManager.getInstance().getLastMonthOfYear(year, month);
                break;
            case NEXT:
                date = CalendarManager.getInstance().getNextMonthOfYear(year, month);
                break;
            default:
        }
        mDayCells.clear();
        mDayCells.addAll(CalendarManager.getInstance().getMonthDate(date[0], date[1]));
        mCurrentYear = date[0];
        mCurrentMonth = date[1];
        mTitleText = mCurrentYear + "年" + mCurrentMonth + "月";

        int[] dateNext = CalendarManager.getInstance().getNextMonthOfYear(mCurrentYear, mCurrentMonth);
        mNextDayCells.clear();
        mNextDayCells.addAll(CalendarManager.getInstance().getMonthDate(dateNext[0], dateNext[1]));

        int[] dateLast = CalendarManager.getInstance().getLastMonthOfYear(mCurrentYear, mCurrentMonth);
        mLastDayCells.clear();
        mLastDayCells.addAll(CalendarManager.getInstance().getMonthDate(dateLast[0], dateLast[1]));

        for (int index = 0; index < mDayRectFs.size(); index++) {
            mDayCells.get(index).setRectF(mDayRectFs.get(index));
            mNextDayCells.get(index).setRectF(mNextDayRectFs.get(index));
            mLastDayCells.get(index).setRectF(mLastDayRectFs.get(index));
        }
        Log.d(TAG, "updateMonth: mTitleText = " + mTitleText);
    }

    private void handleActionMove(MotionEvent event) {
        mOffsetX = event.getX() - mTouchDownX;
        if (Math.abs(mOffsetX) > mTouchSlop) {
            mIsClick = false;
        }
        if (mIsScrolling || !mDayContentRectF.contains(mTouchDownX, mTouchDownY)) {
            return;
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void handleActionUp(MotionEvent event) {
        mOffsetX = event.getX() - mTouchDownX;
        if (mIsClick) {
            mOffsetX = 0;
            if (isActionDownInsideRectF(mNextRectF)) {
                updateMonth(mCurrentYear, mCurrentMonth, Type.NEXT);
                ViewCompat.postInvalidateOnAnimation(this);
            } else if (isActionDownInsideRectF(mLastRectF)) {
                updateMonth(mCurrentYear, mCurrentMonth, Type.LAST);
                ViewCompat.postInvalidateOnAnimation(this);
            } else if (isActionDownInsideRectF(mDayContentRectF)) {
                if (mOnClickDayListener != null) {
                    for (DayCell dayCell : mDayCells) {
                        if (dayCell.isContains(mTouchDownX, mTouchDownY)) {
                            mOnClickDayListener.onClickDay(dayCell.getYear(), dayCell.getMonth(), dayCell.getDay());
                            break;
                        }
                    }
                }
            }
        } else if (isActionDownInsideRectF(mDayContentRectF)) {
            startScroll(event);
        }
        mIsClick = false;
    }

    private boolean isActionDownInsideRectF(RectF rectF) {
        return rectF.contains(mTouchDownX, mTouchDownY);
    }

    private void startScroll(MotionEvent event) {
        mIsScrolling = true;
        mVelocityTracker.computeCurrentVelocity(1000);
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
        Log.d(TAG, "startScroll: dx  = " + dx);
        ViewCompat.postInvalidateOnAnimation(this);
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
}
