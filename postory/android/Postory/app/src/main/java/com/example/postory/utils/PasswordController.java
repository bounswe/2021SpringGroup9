package com.example.postory.utils;

import java.util.ArrayList;
import java.util.List;

public class PasswordController {
    final int MIN_PASSWORD_LENGTH = 10;
    String password;

    public PasswordController(String password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean lengthEnough() {
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public boolean containsCapital() {
        return password.matches(".*[A-Z].*");
    }

    public boolean containsNumber() {
        return password.matches(".*\\d.*");
    }

    public boolean containsSpecial() {
        return password.matches(".*[^a-zA-Z0-9].*");
    }


    public boolean allControls() {
        return lengthEnough() &&
                containsCapital() &&
                containsNumber() &&
                containsSpecial();
    }

    public List<String> getErrors() {
        List<String> errorList = new ArrayList<String>();
        if (!this.lengthEnough())
            errorList.add("Password should be 8 characters or longer");
        if (!this.containsCapital())
            errorList.add("Password should contain an uppercase letter");
        if (!this.containsNumber())
            errorList.add("Password should contain a number");
        if (!this.containsNumber())
            errorList.add("Password should contain a special character");

        return errorList;
    }
}
