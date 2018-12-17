package buskinggo.seoul.com.buskinggo.dto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class BuskingDTO {
    private int buskingNo;
    private String buskerName;
    private int buskerNo;
    private String photo;
    private String buskingDate;
    private String buskingTime;
    private String place;
    private Double latitude;
    private Double longitude;
    private String introduce;
    private int wantSum;
    private int myWant;


    public BuskingDTO(int buskingNo, String buskingDate, String buskingTime, String place){
        this.buskingNo = buskingNo;
        this.buskingDate = buskingDate;
        this.buskingTime = buskingTime;
        this.place = place;
    }
    public BuskingDTO(int buskingNo, String buskerName, String photo, String place, String buskingDate, String buskingTime){
        this.buskingNo = buskingNo;
        this.buskerName = buskerName;
        this.photo = photo;
        this.place = place;
        this.buskingDate = buskingDate;
        this.buskingTime = buskingTime;
    }

    public BuskingDTO(String photo, int buskerNo, String buskerName, String buskingDate, String buskingTime, String place, String latitude, String longitude, String introduce, int wantSum, int myWant){
        this.buskerName = buskerName;
        this.photo = photo;
        this.buskerNo = buskerNo;
        this.place = place;
        this.buskingDate = buskingDate;
        this.buskingTime = buskingTime;
        this.introduce = introduce;
        this.wantSum = wantSum;
        this.myWant = myWant;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }


    public int getBuskerNo() {
        return buskerNo;
    }

    public void setBuskerNo(int buskerNo) {
        this.buskerNo = buskerNo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBuskingDate() {
        return buskingDate;
    }

    public void setBuskingDate(String buskingDate) {
        this.buskingDate = buskingDate;
    }

    public String getBuskingTime() {
        return buskingTime;
    }

    public void setBuskingTime(String buskingTime) {
        this.buskingTime = buskingTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getBuskingNo() {
        return buskingNo;
    }

    public void setBuskingNo(int buskingNo) {
        this.buskingNo = buskingNo;
    }

    public String getBuskerName() {
        return buskerName;
    }

    public void setBuskerName(String buskerName) {
        this.buskerName = buskerName;
    }

    public int getWantSum() {
        return wantSum;
    }

    public void setWantSum(int wantSum) {
        this.wantSum = wantSum;
    }

    public int getMyWant() {
        return myWant;
    }

    public void setMyWant(int myWant) {
        this.myWant = myWant;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
