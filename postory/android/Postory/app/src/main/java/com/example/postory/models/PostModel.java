package com.example.postory.models;

public class PostModel {

    private String postPicture;
    private String profilePicture;
    private String postText;
    private String userName;

    public PostModel(String postPicture, String profilePicture, String postText, String userName) {
        this.postPicture = postPicture;
        this.profilePicture = profilePicture;
        this.postText = postText;
        this.userName = userName;
    }

    public String getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(String postPicture) {
        this.postPicture = postPicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
