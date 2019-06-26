package com.foodfinder.acount;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Account implements Serializable {

    private String userId;
    private String userName;
    private String lastName;
    private String email;
    private String password;
    private Date birthday;
    private String phone;
    private String profileImage;
    private boolean isActive;
    private boolean isDriver;
    private List<String> friends;
    private List<Float> ranks;




    public Account(String userId, String userName,String lastName, String email, String password, Date birthday, String phone, String profileImage, boolean isActive, boolean isDriver, List<String> friends, List<Float> ranks) {
        this.userId = userId;
        this.userName = userName;
        this.lastName=lastName;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phone = phone;
        this.profileImage = profileImage;
        this.isActive = isActive;
        this.isDriver=isDriver;
        this.friends=friends;
        this.ranks=ranks;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public List<Float> getRanks() {
        return ranks;
    }

    public void setRanks(List<Float> ranks) {
        this.ranks = ranks;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }
}
