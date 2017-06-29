package com.example.neo.googlemap_new;


/**
 * Created by neo on 14/06/2017.
 */

public class MYLocation {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private int quota;
    private String opentime;

    public MYLocation(String name, String address, String opentime, int quota, String latitude, String longitude){
        this.name = name;
        this.address = address;
        this.quota = quota;
        this.opentime = opentime;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getName(){
        return this.name;
    }
    public String getAddress(){
        return this.address;
    }
    public String getOpentime(){return  this.opentime;}
    public int getQuota(){return this.quota;}
    public String getLatitude(){
        return this.latitude;
    }
    public String getLongitude(){
        return this.longitude;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setQuota(int quota){this.quota = quota;}
    public void setOpentime(String opentime){this.opentime = opentime;}
    public void setLatitude(String latitude){
        this.latitude = latitude;
    }
    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

}
