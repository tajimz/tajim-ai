package com.tajimz.tajimai.models;

public class ChatModel {
    String ai, user;
    Boolean isAi;
    Boolean shouldTyping;
    public ChatModel(String ai, String user, Boolean isAi, Boolean shouldTyping){
        this.ai = ai;
        this.user = user;
        this.isAi = isAi;
        this.shouldTyping = shouldTyping;
    }
    public String getAi(){
        return ai;
    }
    public Boolean getShouldTyping(){
        return shouldTyping;
    }
    public String getUser(){
        return user;
    }

    public Boolean getIsAi(){
        return isAi;
    }

    public void setShouldNotTyping(){
        this.shouldTyping = false;
    }
}
