package com.ysu.net.entity;

public class DoctorInfo {
    String ip;

    String name;

    Integer floor;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public DoctorInfo() {
    }

    public DoctorInfo(String ip, String name, Integer floor) {
        this.ip = ip;
        this.name = name;
        this.floor = floor;
    }
}
