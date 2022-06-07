package com.example.freelancerapp;

public class UserAppointment {
    public String id;
    public String name;
    public String service;
    public String date;
    public String time;
//    public String password;
//    public String phonenum;
//    public String service_type;
//    public String availability;
//    public String appointment;

    public UserAppointment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

//    public UserAppointment(String userid, String username, String email, String phonenum, String password) {
//        this.userid = userid;
//        this.username = username;
//        this.email = email;
//        this.phonenum = phonenum;
//        this.password = password;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getService() { return service; }

    public void setService(String service) { this.service = service; }

    public String getDate() {
        return  date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public UserAppointment(String serviceType) {
//        this. service_type = serviceType;
//    }
//
//    public String getServicetype() { return service_type; }
//
//    public void setServicetype(String service_type) { this.service_type = service_type; }
//
//    public String getAvailability() { return availability; }
//
//    public void setAvailabity(String availability) { this.availability = availability; }

//    public String getAppointment() { return appointment; }
//
//    public void setAppointment(String appointment) { this.appointment = appointment; }
}

