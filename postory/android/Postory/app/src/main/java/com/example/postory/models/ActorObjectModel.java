package com.example.postory.models;

import com.google.gson.JsonObject;

public class ActorObjectModel {

    private UserModel actor;
    private JsonObject object;
    private String type;

    public UserModel getActor() {
        return actor;
    }

    public void setActor(UserModel actor) {
        this.actor = actor;
    }

    public JsonObject getObject() {
        return object;
    }

    public void setObject(JsonObject object) {
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
