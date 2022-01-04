package com.example.postory.models;

public class CommentModel {
    private String username;
    private String userProfilePic;
    private String comment;

    public CommentModel(String username, String userProfilePic, String comment) {
        this.username = username;
        this.userProfilePic = userProfilePic;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
