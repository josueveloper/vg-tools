package com.vgtools.enums;

public enum KeyboardKey {
    ENTER("\n"),
    CTRLA(new String(new byte[] { 1 }));

    private final String value;

    KeyboardKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
