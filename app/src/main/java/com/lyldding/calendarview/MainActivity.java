package com.lyldding.calendarview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lyldding.calendarlibrary.CalendarConfig;
import com.lyldding.calendarlibrary.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements CalendarView.OnClickDayListener {

    CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalendarView = findViewById(R.id.calender);
        mCalendarView.setOnClickDayListener(this);

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withIsShowBorder(false)
                        .withIsBtnSwitchMonthScroll(true)
                        .withIsSundayAtFirst(true)
                        .withBackground(Color.WHITE)
                        .withIsContainOtherMonthDate(true).build());
            }
        });

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withIsShowBorder(true).build());
            }
        });

        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withIsBtnSwitchMonthScroll(false).build());
            }
        });

        findViewById(R.id.bt3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withBackground(Color.GREEN).build());
            }
        });

        findViewById(R.id.bt4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withIsSundayAtFirst(false).build());
            }
        });

        findViewById(R.id.bt5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.setConfig(CalendarConfig.Builder.newBuilder().withIsContainOtherMonthDate(false).build());
            }
        });

        findViewById(R.id.bt6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.postInvalidate();
            }
        });
    }

    @Override
    public void onClickDay(int year, int month, int day) {
        Toast.makeText(this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
    }
}
