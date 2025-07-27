package com.example.home_security_api.model;

public class FileData {

    private String name;
    private int cameraNr;
    private String type;
    private double size;
    private String path;
    private String created;

    public FileData() {}

    public String getName() {
        return name;
    }

    public FileData setName(String name) {
        this.name = name;
        return this;
    }
    
    public int getCameraNr() {
        return cameraNr;
    }

    public FileData setCameraNr(int cameraNr) {
        this.cameraNr = cameraNr;
        return this;
    }

    public String getType() {
        return type;
    }

    public FileData setType(String type) {
        this.type = type;
        return this;
    }

    public double getSize() {
        return size;
    }

    public FileData setSize(double size) {
        this.size = size;
        return this;
    }

    public String getPath() {
        return path;
    }

    public FileData setPath(String path) {
        this.path = path;
        return this;
    }

    public String getCreated() {
        return created;
    }

    public FileData setCreated(String created) {
        this.created = created;
        return this;
    }
}
