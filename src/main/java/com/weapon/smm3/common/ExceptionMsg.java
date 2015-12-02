package com.weapon.smm3.common;

/**
 * Created by weapon on 2015-11-25.
 */
public class ExceptionMsg extends Exception {

    //
    public String message;

    public ExceptionMsg(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
