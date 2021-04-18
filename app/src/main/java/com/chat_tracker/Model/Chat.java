package com.chat_tracker.Model;

public class Chat {
    private String token1;
    private String token2;
    private String magContent;
    private String userSender;
    private long send_date;

//    public Chat(String token1, String token2, String magContent, long send_date) {
//        this.token1 = token1;
//        this.token2 = token2;
//        this.magContent = magContent;
//        this.send_date = send_date;
//
//    }

    public Chat(String token1, String token2, String magContent, String userSender, long send_date) {
        this.token1 = token1;
        this.token2 = token2;
        this.magContent = magContent;
        this.userSender = userSender;
        this.send_date = send_date;

    }

    public Chat() {
    }

    public long getSend_date() {
        return send_date;
    }

    public void setSend_date(long send_date) {
        this.send_date = send_date;
    }

    public String getToken1() {
        return token1;
    }

    public void setToken1(String token1) {
        this.token1 = token1;
    }

    public String getToken2() {
        return token2;
    }

    public void setToken2(String token2) {
        this.token2 = token2;
    }

    public String getMagContent() {
        return magContent;
    }

    public void setMagContent(String magContent) {
        this.magContent = magContent;
    }

    public String getUserSender() {
        return userSender;
    }

    public void setUserSender(String userSender) {
        this.userSender = userSender;
    }
}
