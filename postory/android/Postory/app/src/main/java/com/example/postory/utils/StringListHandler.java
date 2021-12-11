package com.example.postory.utils;

import java.util.List;

public class StringListHandler {

    private StringListHandler(){}

    public static String listToSingleString(List<String> strings){
        StringBuilder single = new StringBuilder();
        for(String string : strings){
            single.append(string);
            single.append("\n");
        }
        return single.toString();
    }
}
