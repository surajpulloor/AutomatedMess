package com.project.ams.automatedmess;

public class CustomerProfile {
    public String name;
    public String address;
    public String mobileNo;
    public String phoneNo;

    public CustomerProfile() {
    }

    public CustomerProfile(String name, String address, String mobileNo, String phoneNo) {
        this.name = name;
        this.address = address;
        this.mobileNo = mobileNo;

        this.phoneNo = phoneNo;
    }
}
