package com.at.amqrouter.bean;

/**
 * Created by Terry LIANG on 2017/9/20.
 */
public class GeneralResponse {
    private int statusCode;
    private String message;

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

    public static GeneralResponse buildResponse(int statusCode, String message) {
        GeneralResponse generalResponse = new GeneralResponse();
        generalResponse.setStatusCode(statusCode);
        generalResponse.setMessage(message);
        return generalResponse;
    }
}
