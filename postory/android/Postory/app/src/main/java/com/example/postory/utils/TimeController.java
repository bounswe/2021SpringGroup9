package com.example.postory.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeController {

    public static final int YEAR_PRECISION = 1;
    public static final int MONTH_PRECISION = 2;
    public static final int DAY_PRECISION = 3;
    public static final int TIME_PRECISION = 4;

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

    public static int getYearPrecision() {
        return YEAR_PRECISION;
    }

    public static int getMonthPrecision() {
        return MONTH_PRECISION;
    }

    public static int getDayPrecision() {
        return DAY_PRECISION;
    }

    public static int getTimePrecision() {
        return TIME_PRECISION;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private int precision;
    private SimpleDateFormat dateFormat;
    private Date startDate;
    private Date endDate;

    public int getPrecision() {
        return precision;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
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
                    startDate = dateFormat.parse("" + startYear + "-" + startMonth + "-" + startDay + " " + startHour + ":" + startMinute);
                    endDate = dateFormat.parse("" + endYear + "-" + endMonth + "-" + endDay + " " + endHour + ":" + endMinute);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.precision);
        }

    }
}
