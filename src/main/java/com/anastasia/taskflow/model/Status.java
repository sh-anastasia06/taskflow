package com.anastasia.taskflow.model;

public enum Status {
    TODO("To do"),
    IN_PROGRESS("In progress"),
    DONE("Done"),
    CANCELLED("Cancelled");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
