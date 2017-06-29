package com.example.neo.googlemap_new;

/**
 * Created by neo on 27/06/2017.
 */

public class Account {
    private String userName;
    private String password;
    private String carPlate;
    private String phoneNum;
    public Account( String userName, String password, String carPlate, String phoneNum){
        this.userName = userName;
        this.password = password;
        this.carPlate = carPlate;
        this.phoneNum = phoneNum;

    }
    public String getUserName(){return this.userName;}
    public String getPassword(){return this.password;}
    public String getCarPlate(){return this.carPlate;}
    public String getPhoneNum(){return this.phoneNum;}
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setCarPlate(String carPlate){
        this.carPlate = carPlate;
    }
    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }
}
