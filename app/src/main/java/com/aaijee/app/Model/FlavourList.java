package com.aaijee.app.Model;

public class FlavourList {

    String f_id;
    String flavour_name;

    public FlavourList(String f_id, String flavour_name) {
        this.f_id = f_id;
        this.flavour_name = flavour_name;
    }

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getFlavour_name() {
        return flavour_name;
    }

    public void setFlavour_name(String flavour_name) {
        this.flavour_name = flavour_name;
    }
}
