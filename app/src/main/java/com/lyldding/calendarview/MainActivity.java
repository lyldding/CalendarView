package com.lyldding.calendarview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CalendarView.OnClickDayListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((CalendarView) findViewById(R.id.calender)).setOnClickDayListener(this);
    }

    @Override
    public void onClickDay(int year, int month, int day) {
        Toast.makeText(this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
    }
}
