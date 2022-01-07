package com.example.postory.utils;

import java.util.List;

/**
 * Class that converts list of strings to a single string with line breaks
 * @author niyaziulke
 */
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
