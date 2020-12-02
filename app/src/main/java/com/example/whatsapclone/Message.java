package com.example.whatsapclone;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message {
    private String from, message,date,time,to;
    private String type,messageId;
    public Message()
    {

    }

    public Message(String from, String type, String message,String date,String time,String to, String messageId) {
        this.from = from;
        this.type = type;
        this.message =message;
        this.date=date;
        this.time=time;
        this.to=to;
        this.messageId=messageId;
    }

    public void setTo(String to)
    {
        this.to=to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTo()
    {
        return  to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void seType(String messageType) {
        this.type = messageType;
    }
}
