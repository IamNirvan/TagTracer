package com.tagtracer.enums;

public enum ERFIDReaderSession {
    SESSION("session"),
    TARGET("target");

    private final String name;

    private ERFIDReaderSession(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
