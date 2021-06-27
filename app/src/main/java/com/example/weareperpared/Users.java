package com.example.weareperpared;

public class Users {

    String cellphoneNum,name,address,password,residentNum,userType;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResidentNum() {
        return residentNum;
    }

    public void setResidentNum(String residentNum) {
        this.residentNum = residentNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphoneNum() {
        return cellphoneNum;
    }

    public void setCellphoneNum(String cellphoneNum) {
        this.cellphoneNum = cellphoneNum;
    }
}
