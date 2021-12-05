package com.example.postory.models;

import java.util.List;

public class UserModel {


    public List<Object> getFollowerUsers() {
        return followerUsers;
    }

    public void setFollowerUsers(List<Object> followerUsers) {
        this.followerUsers = followerUsers;
    }
    String username;
    String name;
    String surname;
    String email;
    List<Object> followedUsers;
    List<Object> followerUsers;
    List<Object> posts;
    List<Object> savedPosts;
    List<Object> likedPosts;
    List<Object> comments;
    boolean isBanned;
    boolean isAdmin;
    boolean isPrivate;
    boolean isActive;

}
