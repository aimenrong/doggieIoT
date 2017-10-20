package com.at.registry.restful;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
public class GenernalResponse {
    private int statusCode;
    private String message;

    public GenernalResponse() {

    }

    public GenernalResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static GenernalResponse build(int statusCode, String message) {
        return new GenernalResponse(statusCode, message);
    }
}
