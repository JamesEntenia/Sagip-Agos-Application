package com.example.weareperpared;

public class UserHelper {

    String name, address, cellphoneNum, needRescue, Lat, Lng, password,userType,myRescuer,assignedTo,assignedNextTo,online,currentLocation,readNotif;

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public UserHelper(){}



    public UserHelper(String name, String address, String cellphoneNum, String needRescue, String Lat, String Lng, String password, String userType, String myRescuer, String online,String currentLocation,String readNotif){

        this.name = name;
        this.address = address;
        this.cellphoneNum = cellphoneNum;
        this.needRescue = needRescue;
        this.Lat = Lat;
        this.Lng = Lng;
        this.userType = userType;
        this.password = password;
        this.myRescuer = myRescuer;
        this.online = online;
        this.currentLocation = currentLocation;
        this.readNotif = readNotif;

    }



    public UserHelper(String name, String address, String cellphoneNum, String needRescue, String Lat, String Lng, String password,
                      String userType, String myRescuer, String assignedTo, String assignedNextTo, String online,String readNotif){

        this.name = name;
        this.address = address;
        this.cellphoneNum = cellphoneNum;
        this.needRescue = needRescue;
        this.Lat = Lat;
        this.Lng = Lng;
        this.userType = userType;
        this.password = password;
        this.myRescuer = myRescuer;
        this.assignedTo = assignedTo;
        this.assignedNextTo = assignedNextTo;
        this.online = online;
        this.readNotif = readNotif;

    }
    public String getOnline() {
        return online;
    }

    public String getReadNotif() {
        return readNotif;
    }

    public void setReadNotif(String readNotif) {
        this.readNotif = readNotif;
    }

    public void setOnline(String online) {
        this.online = online;
    }
    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedNextTo() {
        return assignedNextTo;
    }

    public void setAssignedNextTo(String assignedNextTo) {
        this.assignedNextTo = assignedNextTo;
    }
    public String getMyRescuer() {
        return myRescuer;
    }

    public void setMyRescuer(String myRescuer) {
        this.myRescuer = myRescuer;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getNeedRescue() {
        return needRescue;
    }

    public void setNeedRescue(String needRescue) {
        this.needRescue = needRescue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCellphoneNum() {
        return cellphoneNum;
    }

    public void setCellphoneNum(String cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }
}
