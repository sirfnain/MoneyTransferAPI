package com.revolut.interview.response;

public enum Status {
    SUCCESS ("Success"),
    ERROR ("Error");

    private String status;

    Status(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
