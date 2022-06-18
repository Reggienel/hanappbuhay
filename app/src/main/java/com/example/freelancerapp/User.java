package com.example.freelancerapp;

public class User {
    public String userid;
    public String username;
    public String email;
    public String password;
    public String phonenum;
    public String service_type;
    public String availability;
    public String serviceprice;
    public String location;
    public String profile_image_uri;
    public String date_posted;
    public String rating;
    public String userratingcount;
    public Double  balance;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userid, String username, String email, String phonenum, String password) {
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.phonenum = phonenum;
        this.password = password;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid= userid;
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

    public String getServiceprice() { return serviceprice; }

    public void setServiceprice(String serviceprice) { this.serviceprice = serviceprice; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public String getProfile_image_uri() { return profile_image_uri; }

    public void setProfile_image_uri(String profile_image_uri) { this.profile_image_uri = profile_image_uri; }

    public String getDate_posted() { return date_posted; }

    public void setDate_posted(String date_posted) { this.date_posted = date_posted; }

    public String getRating() { return rating; }

    public void setRating(String rating) { this.rating = rating; }

    public String getUserratingcount() { return  userratingcount; }

    public void setUserratingcount(String userratingcount) { this. userratingcount =  userratingcount; }

    public Double getBalance(){ return balance;}

    public void setBalance(Double  balance) { this.balance = balance; }

}

