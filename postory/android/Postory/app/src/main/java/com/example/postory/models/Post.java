package com.example.postory.models;

import java.util.Date;
import java.util.List;

public class Post {
    public int id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<List<Object>> getLocations() {
        return locations;
    }

    public void setLocations(List<List<Object>> locations) {
        this.locations = locations;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public Date getStoryDate() {
        return storyDate;
    }

    public void setStoryDate(Date storyDate) {
        this.storyDate = storyDate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String title;
    public String story;
    public String owner;
    public String username;
    public String userPhoto;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public List<String> tags;
    public List<List<Object>> locations;
    public List<List<Object>> likeList;

    public List<List<Object>> getLikeList() {
        return likeList;
    }

    public void setLikeList(List<List<Object>> likeList) {
        this.likeList = likeList;
    }

    public List<List<Object>> getComments() {
        return comments;
    }

    public void setComments(List<List<Object>> comments) {
        this.comments = comments;
    }

    public List<List<Object>> comments;
    public List<String> images;
    public Date postDate;
    public Date editDate;
    public Date storyDate;
    public int viewCount;
    public List<Integer> year;
    public List<Integer> month;
    public List<Integer> day;
    public List<Integer> hour;
    public List<Integer> minute;

    public List<Integer> getYear() {
        return year;
    }

    public void setYear(List<Integer> year) {
        this.year = year;
    }

    public List<Integer> getMonth() {
        return month;
    }

    public void setMonth(List<Integer> month) {
        this.month = month;
    }

    public List<Integer> getDay() {
        return day;
    }

    public void setDay(List<Integer> day) {
        this.day = day;
    }

    public List<Integer> getHour() {
        return hour;
    }

    public void setHour(List<Integer> hour) {
        this.hour = hour;
    }

    public List<Integer> getMinute() {
        return minute;
    }

    public void setMinute(List<Integer> minute) {
        this.minute = minute;
    }
}
