package com.revolut.interview.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Response {
    private final String body;
    private final int status;

    public Response(final int status) {
        this(status, null);
    }

    public Response(final int status, final String body) {
        this.status = status;
        this.body = body;
    }

    public JsonElement jsonElement() {
        return new JsonParser().parse(body);
    }

    public JsonElement getData() {
        return new JsonParser().parse(body).getAsJsonObject().get("data");
    }

    public int getStatus() {
        return status;
    }
}
