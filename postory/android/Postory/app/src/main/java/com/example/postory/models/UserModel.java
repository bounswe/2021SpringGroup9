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

    public List<Object> getFollowedUsers() {
        return followedUsers;
    }

    public void setFollowedUsers(List<Object> followedUsers) {
        this.followedUsers = followedUsers;
    }

    public List<Object> getFollowerUsers() {
        return followerUsers;
    }

    public void setFollowerUsers(List<Object> followerUsers) {
        this.followerUsers = followerUsers;
    }

    public List<Object> getPosts() {
        return posts;
    }

    public void setPosts(List<Object> posts) {
        this.posts = posts;
    }

    public List<Object> getSavedPosts() {
        return savedPosts;
    }

    public void setSavedPosts(List<Object> savedPosts) {
        this.savedPosts = savedPosts;
    }

    public List<Object> getLikedPosts() {
        return likedPosts;
    }

    public void setLikedPosts(List<Object> likedPosts) {
        this.likedPosts = likedPosts;
    }

    public List<Object> getComments() {
        return comments;
    }

    public void setComments(List<Object> comments) {
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

    public List<Object> getImages() {
        return images;
    }

    public void setImages(List<Object> images) {
        this.images = images;
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
    boolean is_active;
    List<Object> images;

}
