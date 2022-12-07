package com.simple.simpleqrcode.Models;

public class QuoteModel {
    String name;
    String qty;
    String code;
    String mrp;
    String agtMrp;

    public QuoteModel(String name, String qty, String code, String mrp, String agtMrp) {
        this.name = name;
        this.qty = qty;
        this.code = code;
        this.mrp = mrp;
        this.agtMrp = agtMrp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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

    public QuoteModel() {
    }
}
