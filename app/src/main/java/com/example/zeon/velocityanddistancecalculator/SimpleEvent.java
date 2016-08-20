package com.example.zeon.velocityanddistancecalculator;

/**
 * Created by Zeon on 20/8/2559.
 */
public class SimpleEvent {
    public static final int OPEN_SERVICE = 1;
    public static final int CLOSR_SERVICE = 2;
    private int status;

    public SimpleEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
