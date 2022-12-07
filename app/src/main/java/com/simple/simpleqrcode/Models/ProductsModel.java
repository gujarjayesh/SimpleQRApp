package com.simple.simpleqrcode.Models;

public class ProductsModel {

    String name;
//    string qty
    String code;
    String mrp;
    String agtMrp;
    String cstMrp;
    String key;

    public ProductsModel(String name, String code, String mrp, String agtMrp, String cstMrp, String key) {
        this.name = name;
        this.code = code;
        this.mrp = mrp;
        this.agtMrp = agtMrp;
        this.cstMrp = cstMrp;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getAgtMrp() {
        return agtMrp;
    }

    public void setAgtMrp(String agtMrp) {
        this.agtMrp = agtMrp;
    }

    public String getCstMrp() {
        return cstMrp;
    }

    public void setCstMrp(String cstMrp) {
        this.cstMrp = cstMrp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ProductsModel() {
    }
}
