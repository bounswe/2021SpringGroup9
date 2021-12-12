package com.example.postory.models;

import com.example.postory.R;

import java.util.Date;
import java.util.List;

public class PostModel {
    public PostModel(int id, String title, String story, String owner, List<String> tags, List<List<Object>> locations, List<String> images, Date postDate, Date editDate, Date storyDate, int viewCount) {
        this.title = title;
        this.story = story;
        this.owner = owner;
        this.tags = tags;
        this.locations = locations;
        this.images = images;
        this.postDate = postDate;
        this.editDate = editDate;
        this.storyDate = storyDate;
        this.viewCount = viewCount;
        this.id = id;
    }

    public int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String title;
    public String story;
    public String owner;
    public List<String> tags;

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

    public List<List<Object>> locations;
    public List<String> images;
    public Date postDate;
    public Date editDate;
    public Date storyDate;
    public int viewCount;

}