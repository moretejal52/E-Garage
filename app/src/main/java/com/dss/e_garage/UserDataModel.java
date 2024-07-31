package com.dss.e_garage;

public class UserDataModel  {
    String fname,lname,mob,ppurl;
    public UserDataModel(){
    }
    public UserDataModel(String fname, String lname, String mob, String ppurl) {
        this.fname = fname;
        this.lname = lname;
        this.mob = mob;
        this.ppurl = ppurl;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getMob() {
        return mob;
    }

    public String getPpurl() {
        return ppurl;
    }
}
