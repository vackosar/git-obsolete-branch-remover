package com.vackosar.gitobsoletebranchremover.boundary;

import com.google.inject.Singleton;

@Singleton
public class Console {

    public char[] readPassword(String message) {
        return System.console().readPassword(message);
    }

    public String readLine(String message) {
        return System.console().readLine(message);
    }
}
