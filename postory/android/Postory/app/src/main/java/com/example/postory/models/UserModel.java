package com.example.postory.models;

import java.util.List;

public class UserModel {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(List<Integer> followedUsers) {
        this.followedUsers = followedUsers;
    }

    public List<Integer> getFollowerUsers() {
        return followerUsers;
    }

    public void setFollowerUsers(List<Integer> followerUsers) {
        this.followerUsers = followerUsers;
    }

    public List<Integer> getPosts() {
        return posts;
    }

    public void setPosts(List<Integer> posts) {
        this.posts = posts;
    }

    public List<Integer> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(List<Integer> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public List<Integer> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Integer> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<Integer> getComments() {
        return comments;
    }

    public void setComments(List<Integer> comments) {
        this.comments = comments;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    int id;
    String username;
    String name;
    String surname;
    String email;
    List<Integer> followedUsers;
    List<Integer> followerUsers;
    List<Integer> posts;
    List<Integer> savedPosts;
    List<Integer> likedPosts;
    List<Integer> comments;
    boolean isBanned;
    boolean isAdmin;
    boolean isPrivate;
    boolean is_active;
    String userPhoto;

}
