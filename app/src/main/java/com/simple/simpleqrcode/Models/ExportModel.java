package com.simple.simpleqrcode.Models;

public class ExportModel {
    String key;
    String name;

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.key = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExportModel(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public ExportModel() {
    }
}
