package com.example.postory.utils;

import com.example.postory.models.OtherUser;
import com.example.postory.models.UserGeneralModel;
import com.example.postory.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public  class UserModelConverter {
    public static UserGeneralModel convert(UserModel thisUserHelper){
        UserGeneralModel thisUser = new UserGeneralModel();
        thisUser.setAdmin(thisUserHelper.isAdmin());
        thisUser.setBanned(thisUserHelper.isBanned());
        thisUser.setEmail(thisUserHelper.getEmail());
        thisUser.setName(thisUserHelper.getName());
        thisUser.setSurname(thisUserHelper.getSurname());
        thisUser.setUsername(thisUserHelper.getUsername());
        thisUser.setId(thisUserHelper.getId());
        thisUser.setIs_active(thisUserHelper.isIs_active());
        thisUser.setUserPhoto(thisUserHelper.getUserPhoto());
        thisUser.setPrivate(thisUserHelper.isPrivate());
        thisUser.setComments(thisUserHelper.getComments());
        thisUser.setLikedPosts(thisUserHelper.getLikedPosts());
        thisUser.setSavedPosts(thisUserHelper.getSavedPosts());
        thisUser.setPosts(thisUserHelper.getPosts());

        List<Integer> followerList = new ArrayList<>();
        List<Integer> followedList = new ArrayList<>();
        for(OtherUser follower : thisUserHelper.getFollowerUsers()){
            followerList.add(follower.getId());
        }
        for(OtherUser followed : thisUserHelper.getFollowedUsers()){
            followedList.add(followed.getId());
        }
        thisUser.setFollowerUsers(followerList);
        thisUser.setFollowedUsers(followedList);
        return thisUser;
    }

}
