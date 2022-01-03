package com.example.postory.models;

public class ActorObjectGeneralModel {
    private UserModel actor;
    private UserModel receiverUser;
    private Post receiverPost;
    private String type;

    public ActorObjectGeneralModel(UserModel actor, UserModel receiverUser, Post receiverPost, String type) {
        this.actor = actor;
        this.receiverUser = receiverUser;
        this.receiverPost = receiverPost;
        this.type = type;
    }

    public UserModel getActor() {
        return actor;
    }

    public void setActor(UserModel actor) {
        this.actor = actor;
    }

    public UserModel getReceiverUser() {
        return receiverUser;
    }

    public void setReceiverUser(UserModel receiverUser) {
        this.receiverUser = receiverUser;
    }

    public Post getReceiverPost() {
        return receiverPost;
    }

    public void setReceiverPost(Post receiverPost) {
        this.receiverPost = receiverPost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
