package com.example.kidszoid;

public class HelperClass {
    String name, email, vehicle, color, id, plate, password;

    public HelperClass() {
    }

    public HelperClass(String name, String email, String vehicle, String color, String id, String plate, String password) {
        this.name = name;
        this.email = email;
        this.vehicle = vehicle;
        this.color = color;
        this.id = id;
        this.plate = plate;
        this.password = password;
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

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
