package com.dss.e_garage;

public class ModelGarage {
    String Gname,Gmob,Gadd,Gupi,Glatlang,Gppurl;

    public ModelGarage() {
    }

    public ModelGarage(String gname, String gmob, String gupi, String glatlang, String gppurl) {
        Gname = gname;
        Gmob = gmob;
        Gupi = gupi;
        Glatlang = glatlang;
        Gppurl = gppurl;
    }

    public String getGname() {
        return Gname;
    }

    public String getGmob() {
        return Gmob;
    }

    public String getGupi() {
        return Gupi;
    }

    public String getGlatlang() {
        return Glatlang;
    }

    public String getGppurl() {
        return Gppurl;
    }

    public void setGname(String gname) {
        Gname = gname;
    }

    public void setGmob(String gmob) {
        Gmob = gmob;
    }

    public void setGadd(String gadd) {
        Gadd = gadd;
    }

    public void setGupi(String gupi) {
        Gupi = gupi;
    }

    public void setGlatlang(String glatlang) {
        Glatlang = glatlang;
    }

    public void setGppurl(String gppurl) {
        Gppurl = gppurl;
    }
}
