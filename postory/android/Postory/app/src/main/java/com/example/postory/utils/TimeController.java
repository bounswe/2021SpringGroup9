package com.example.postory.utils;

import android.annotation.SuppressLint;

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

    public TimeController(int startYear, int endYear, int startMonth, int endMonth,
                          int startDay, int endDay, int startHour, int endHour,
                          int startMinute, int endMinute){
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
            ,int startDay, int endDay){
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = startDay;
        this.endDay = endDay;
        this.precision = DAY_PRECISION;
    }

    public TimeController(int startYear, int endYear, int startMonth, int endMonth){
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.precision = MONTH_PRECISION;
    }

    public TimeController(int startYear, int endYear){
        this.startYear = startYear;
        this.endYear = endYear;
        this.precision = YEAR_PRECISION;
    }

    @SuppressLint("SimpleDateFormat")
    public Date createDate(){
        SimpleDateFormat dateFormat;
        switch(this.precision){
            case YEAR_PRECISION:
                dateFormat = new SimpleDateFormat("yyyy");
                break;

            case MONTH_PRECISION:
                dateFormat = new SimpleDateFormat("MM/yyyy");
                break;

            case DAY_PRECISION:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                break;
            case TIME_PRECISION:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");

                break;
        }
    }


    public boolean timeValid(){

        if (startYear < endYear) {
            return true;
        }
        else if(startYear > endYear) {
            return false;
        }






        }

}
