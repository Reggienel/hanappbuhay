package com.example.freelancerapp;

public class UserAppointment {
    public String id;
    public String name;
    public String service;
    public String date;
    public String time;
    public String payment;
    public String phonenum;
    public String serviceprice;
    public String meetup;
    public String profile_image_uri;
    public String availability;
    public String rating;

    public UserAppointment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

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

    public String getPayment() { return payment; }

    public void setPayment(String payment) { this.payment = payment; }

    public String getPhonenum() { return phonenum; }

    public void setPhonenum(String phonenum) { this.phonenum = phonenum; }

    public String getServiceprice() { return serviceprice; }

    public void setServiceprice(String serviceprice) { this.serviceprice = serviceprice; }

    public String getMeetup() { return meetup; }

    public void setMeetup(String meetup) { this.meetup = meetup; }

    public String getProfile_image_uri() { return profile_image_uri; }

    public void setProfile_image_uri(String profile_image_uri) { this.profile_image_uri = profile_image_uri; }

    public String getAvailability() { return availability; }

    public void setAvailabity(String availability) { this.availability = availability; }

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }
}

