package org.example.server.mesenger;

public class UserMessage {
    private String username;
    private String message;

    public UserMessage(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
