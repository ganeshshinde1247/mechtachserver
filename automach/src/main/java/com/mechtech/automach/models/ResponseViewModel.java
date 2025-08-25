package com.mechtech.automach.models;

public class ResponseViewModel {
    private Object data;
    private HttpResult result;

    public ResponseViewModel(Object data, HttpResult result) {
        this.data = data;
        this.result = result;
    }

    // Getters and setters
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public HttpResult getResult() {
        return result;
    }

    public void setResult(HttpResult result) {
        this.result = result;
    }
}
