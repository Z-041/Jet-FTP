package com.ftp.session;

import java.io.File;

public class FileSystemContext {
    private File currentDirectory;
    private final File rootDirectory;
    private String renameFrom;

    public FileSystemContext(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.currentDirectory = rootDirectory;
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public String getRenameFrom() {
        return renameFrom;
    }

    public void setRenameFrom(String renameFrom) {
        this.renameFrom = renameFrom;
    }

    public void clearRenameFrom() {
        this.renameFrom = null;
    }

    public void resetToRoot() {
        this.currentDirectory = rootDirectory;
        this.renameFrom = null;
    }
}
