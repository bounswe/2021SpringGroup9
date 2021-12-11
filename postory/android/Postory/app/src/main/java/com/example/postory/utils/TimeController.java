package com.example.postory.utils;

import java.text.SimpleDateFormat;

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
    int startTime;
    int endTime;
    int precision;

    public TimeController(int startYear, int endYear, int startMonth, int endMonth
                         ,int startDay, int endDay, int startTime, int endTime, int precision){
        this.startYear = startYear;
        this.endYear = endYear;
        this.startMonth = startMonth;
        this.endMonth = endMonth;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.precision = precision;
    }
}
