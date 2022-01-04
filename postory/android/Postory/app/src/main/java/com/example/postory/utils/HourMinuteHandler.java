package com.example.postory.utils;

public class HourMinuteHandler {

    public static String intToString(int i){
        if(i < 10)
            return "0"+i;
        else
            return ""+i;
    }

    public static String combine(int hour, int minute){
        return intToString(hour) +
                ":" +
                intToString(minute);
    }
    public static String combine(int hour, String minute){
        return intToString(hour) +
                ":" +
                minute;
    }
    public static String combine(String hour, int minute){
        return hour +
                ":" +
                intToString(minute);
    }
    public static String combine(String hour, String minute){
        return hour +
                ":" +
                minute;
    }
}
