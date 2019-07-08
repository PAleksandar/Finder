package com.foodfinder.acount;

import java.io.Serializable;

public class Request implements Serializable {

    String sender;
    String receiver;
    String message;
    boolean isFinished;
    boolean isArrived;
    Position start;
    Position end;
    String id;

    public Request(){};


    public Request(String sender, String receiver, String message, boolean isFinished, boolean isArrived, Position start, Position end, String id) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isFinished = isFinished;
        this.isArrived = isArrived;
        this.start = start;
        this.end = end;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getEnd() {
        return end;
    }

    public void setEnd(Position end) {
        this.end = end;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean getArrived() {
        return isArrived;
    }

    public void setArrived(boolean arrived) {
        isArrived = arrived;
    }
}
