package com.example.kidszoid;

public class HelperClass {
    String name, email, phone, password, kidname, grade;

    public HelperClass() {
    }

    public HelperClass(String name, String email, String phone, String password, String kidname, String grade) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.kidname =kidname;
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKidname(String kidname) {
        return kidname;
    }

    public void setgrade(String grade) {
        this.grade = grade;
    }
}
