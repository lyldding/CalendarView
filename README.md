# CalendarView
自定义Android日历，只有一个CalendarView，非常小巧。核心代码400行左右，主要就是画。

功能：支持左右滑动切换月份，上下滑动切换年份，设置背景，边框，周末或周一为第一天，显示其他月份日期等功能。  具体参考Api或者自定义。

CSDN：https://blog.csdn.net/lylddingHFFW/article/details/88227283

GitHub:https://github.com/lyldding/CalendarView

```
	dependencies {
	        implementation 'com.github.lyldding:CalendarView:1.0.0'
	}
```

<div align=center>
<img src="https://img-blog.csdnimg.cn/20190306101607874.gif" width="50%" height="50%" />
</div>


大致思路：（主要分为5个区域）
1，初始化时计算每个区域每个Item的具体位置，并缓存。
 2，红色年月为显示区域，在更新时同步更新其他四个区域的日期，并缓存。
 3，处理移动和绘制。

<div align=center>
<img src="https://img-blog.csdnimg.cn/20190306135642811.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x5bGRkaW5nSEZGVw==,size_16,color_FFFFFF,t_70" width="50%" height="50%" />
</div>

代码简述：（具体看源码吧，核心代码400行左右）
1，计算各个Item的具体位置，并更新月份信息
```
private void computeData() {
    mViewRectF.set(0 + mStrokeWidth / 2f, 0 + mStrokeWidth / 2f, mViewWidth - mStrokeWidth / 2f, mViewHeight - mStrokeWidth / 2f);
    mViewWidth = (int) mViewRectF.width();
    mItemWidth = mViewWidth / CalendarUtils.WEEK_COLUMN;
    computeTitleData();
    computeWeekData();
    computeDayData();
    updateMonthData(mCurrentYear, mCurrentMonth, Type.NONE);
}
```
2，绘制各个部分：背景，年月和切换按钮，星期，日期。

```
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
```


