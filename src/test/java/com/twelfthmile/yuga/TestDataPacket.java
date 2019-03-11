package com.twelfthmile.yuga;

import com.twelfthmile.yuga.types.Response;

@SuppressWarnings("unused")
class TestDataPacket {
    String input;
    Response response;
    boolean accepted;

    public TestDataPacket() {
    }

    public TestDataPacket(String input, Response response, boolean accepted) {
        this.input = input;
        this.response = response;
        this.accepted = accepted;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}