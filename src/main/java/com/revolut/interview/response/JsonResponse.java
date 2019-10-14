package com.revolut.interview.response;

import com.google.gson.JsonElement;

import lombok.Data;

@Data
public class JsonResponse {

    private Status      status;
    private String      message;
    private JsonElement data;

    public JsonResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public JsonResponse(Status status, JsonElement data) {
        this.status = status;
        this.data = data;
    }

    public JsonResponse(Status status, String message, JsonElement data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
