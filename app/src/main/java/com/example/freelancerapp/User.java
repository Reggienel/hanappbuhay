package com.example.freelancerapp;

public class User {

    public String username;
    public String email;
    public String password;
    public String phonenum;
    public String service_type;
    public String availability;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String phonenum, String password) {
        this.username = username;
        this.email = email;
        this.phonenum = phonenum;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenum() { return phonenum; }

    public void setPhonenum(String phonenum) { this.phonenum = phonenum; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String serviceType) {
        this. service_type = serviceType;
    }

    public String getServicetype() { return service_type; }

    public void setServicetype(String service_type) { this.service_type = service_type; }

    public String getAvailability() { return availability; }

    public void setAvailabity(String availability) { this.availability = availability; }
}

