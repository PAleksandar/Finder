package com.foodfinder.acount;

import java.io.Serializable;

public class Account implements Serializable {

    private String userId;
    private String userName;
    private String email;
    private String password;
    private String birthday;
    private String phone;
    private String profileImage;
    private boolean isActive;
    private boolean isDriver;

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }

    public Account(String userId, String userName, String email, String password, String birthday, String phone, String profileImage, boolean isActive, boolean isDriver) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phone = phone;
        this.profileImage = profileImage;
        this.isActive = isActive;
        this.isDriver=isDriver;
    }

    public Account(){}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
