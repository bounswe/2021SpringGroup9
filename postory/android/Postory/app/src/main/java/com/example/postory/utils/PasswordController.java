package com.example.postory.utils;

public class PasswordController {
    final int MIN_PASSWORD_LENGTH = 10;
    String password;

    public PasswordController(String password){
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean containsCapital(){
        return password.matches(".*[A-Z].*");
    }
    public boolean containsNumber(){
        return password.matches(".*\\d.*");
    }
    public boolean lengthEnough(){
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    public boolean allControls(){
        return  containsCapital() &&
                containsNumber() &&
                lengthEnough();
    }

}
