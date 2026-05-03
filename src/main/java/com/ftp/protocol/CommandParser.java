package com.ftp.protocol;

public class CommandParser {
    private final String command;
    private final String argument;

    public CommandParser(String line) {
        line = line.trim();
        int spaceIndex = line.indexOf(' ');
        if (spaceIndex == -1) {
            this.command = line.toUpperCase();
            this.argument = "";
        } else {
            this.command = line.substring(0, spaceIndex).toUpperCase();
            this.argument = line.substring(spaceIndex + 1).trim();
        }
    }

    public String getCommand() {
        return command;
    }

    public String getArgument() {
        return argument;
    }

    public boolean hasArgument() {
        return !argument.isEmpty();
    }
}
