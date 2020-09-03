package com.example.wsr_app;

public class Upload {
    private String mLat,mLon,mTime,mImageUrl;

    public Upload() {
    }
    public Upload(String latitude,String longitude,String time, String imageUrl) {
        mLat = latitude;
        mLon = longitude;
        mTime = time;
        mImageUrl = imageUrl;
    }
    public Upload(String latitude,String longitude,String time) {
        mLat = latitude;
        mLon = longitude;
        mTime = time;
    }

    public String getLat(){ return mLat; }
    public void setLat(String latitude){
        mLat = latitude;
    }

    public String getLon(){
        return mLon;
    }
    public void setLon(String longitude){
        mLon = longitude;
    }

    public String gettime(){
        return mTime;
    }
    public void settime(String time){
        mTime = time;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String toString() {
        return mLat+" "+mLon+" "+mTime+" "+mImageUrl;
    }



}
