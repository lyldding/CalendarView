package com.lyldding.calendarview;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author https://github.com/lyldding/CalendarView
 * @date 2019/3/1
 */
public class CalenderDialogFragment extends DialogFragment implements CalendarView.OnClickDayListener {

    private OnClickDayListener mOnClickDayListener;
    private boolean mIsDismissAfterClickDay;

    public static CalenderDialogFragment newInstance() {
        return new CalenderDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        CalendarView calendarView = new CalendarView(getContext());
        calendarView.setOnClickDayListener(this);
        dialog.setContentView(calendarView);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attrs = window.getAttributes();
            attrs.dimAmount = 0f;
            window.setAttributes(attrs);
        }
        return dialog;
    }

    @Override
    public void onClickDay(int year, int month, int day) {
        if (mOnClickDayListener != null) {
            mOnClickDayListener.onClickDay(year, month, day);
        }
        if (mIsDismissAfterClickDay) {
            dismiss();
        }
    }

    public interface OnClickDayListener {
        void onClickDay(int year, int month, int day);
    }

    public void setOnClickDayListener(OnClickDayListener onClickDayListener) {
        mOnClickDayListener = onClickDayListener;
    }

    public void setDismissAfterClickDay(boolean dismissAfterClickDay) {
        mIsDismissAfterClickDay = dismissAfterClickDay;
    }
}
