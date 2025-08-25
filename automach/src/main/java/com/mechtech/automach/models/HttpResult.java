package com.mechtech.automach.models;

public class HttpResult {
    private int statusCode;
    private String message;
    private String operationStatus;
    private String errorCode;

    public HttpResult(int statusCode, String message, String operationStatus, String errorCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.operationStatus = operationStatus;
        this.errorCode = errorCode;
    }

    // Getters and setters
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

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
