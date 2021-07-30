package com.example.kidszoid;

public class helperkid {

    String name, kidname,grade;


    public helperkid(String name, String kidname, String grade) {
        this.name = name;
        this.kidname = kidname;
        this.grade = grade;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getKidname(String kidname) {
        return kidname;
    }
    public void setkidname(String kidname) {
        this.kidname = kidname;
    }
    public String getgrade(String grade) {
        return grade;
    }

    public void setgrade(String grade) {
        this.grade = grade;
    }
}
