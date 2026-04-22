package com.smartcampus.api.model;

import java.util.logging.Logger;

public class ErrorResponse {
    private static final Logger LOGGER = Logger.getLogger(ErrorResponse.class.getName());

    private int status;
    private String code;
    private String message;

    public ErrorResponse() {
        LOGGER.fine("ErrorResponse instance created");
    }

    public ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
        LOGGER.fine("ErrorResponse created with code: " + code);
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
