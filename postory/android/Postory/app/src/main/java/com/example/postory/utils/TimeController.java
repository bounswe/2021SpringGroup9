package com.example.postory.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeController {

    static final int YEAR_PRECISION = 1;
    static final int MONTH_PRECISION = 2;
    static final int DAY_PRECISION = 3;
    static final int TIME_PRECISION = 4;

    int startYear;
    int endYear;
    int startMonth;
    int endMonth;
    int startDay;
    int endDay;
    int startHour;
    int endHour;
    int startMinute;
    int endMinute;
    int precision;
    SimpleDateFormat dateFormat;
    Date startDate;
    Date endDate;

    public TimeController(int startYear, int endYear, int startMonth, int endMonth,
                          int startDay, int endDay, int startHour, int endHour,
                          int startMinute, int endMinute) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.precision = TIME_PRECISION;
    }

    public TimeController(int startYear, int endYear, int startMonth, int endMonth
            , int startDay, int endDay) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = startDay;
        this.endDay = endDay;
        this.precision = DAY_PRECISION;
    }

    public TimeController(int startYear, int endYear, int startMonth, int endMonth) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.precision = MONTH_PRECISION;
    }

    public TimeController(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
        this.precision = YEAR_PRECISION;
    }

    public boolean checkValidity() {
        return startDate.compareTo(endDate) <= 0;
    }

    @SuppressLint("SimpleDateFormat")
    public void createDate() {
        switch (this.precision) {
            case YEAR_PRECISION:
                dateFormat = new SimpleDateFormat("yyyy");
                try {
                    startDate = dateFormat.parse("" + startYear);
                    endDate = dateFormat.parse("" + endYear);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case MONTH_PRECISION:
                dateFormat = new SimpleDateFormat("yyyy-MM");
                try {
                    startDate = dateFormat.parse("" + startYear + "-" + startMonth);
                    endDate = dateFormat.parse("" + endYear + "-" + endMonth);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case DAY_PRECISION:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    startDate = dateFormat.parse("" + startYear + "-" + startMonth + "-" + startDay);
                    endDate = dateFormat.parse("" + endYear + "-" + endMonth + "-" + endDay);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case TIME_PRECISION:
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    startDate = dateFormat.parse("" + startYear + "-" + startMonth + "-" + startDay + "-" + startHour + ":" + startMinute);
                    endDate = dateFormat.parse("" + endYear + "-" + endMonth + "-" + endDay + "-" + endHour + ":" + endMinute);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.precision);
        }
        Log.d("timecontroller",dateFormat.format(startDate));
        Log.d("timecontroller",dateFormat.format(endDate));
    }
}
