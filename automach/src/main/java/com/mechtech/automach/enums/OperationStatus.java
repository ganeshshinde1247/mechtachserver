package com.mechtech.automach.enums;

public enum OperationStatus {
    Success("Success"),
    Error("Error"),
    Warning("Warning"),
    Info("Info");

    private final String value;

    OperationStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
