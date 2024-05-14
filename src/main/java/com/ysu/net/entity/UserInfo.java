package com.ysu.net.entity;

public class UserInfo {
    String name;
    Integer floor;
    // 年龄 性别 房间号 病床号
    Integer age;
    String sex;
    Integer roomId;
    Integer bedId;
    // 血检（5） 尿检（5） 心率 呼吸频率 体温
    double heartRate;
    double breathRate;
    double temperature;

    double xuetang;

    public double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public double getBreathRate() {
        return breathRate;
    }

    public void setBreathRate(double breathRate) {
        this.breathRate = breathRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
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
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public Integer getRoomId() {
        return roomId;
    }
    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
    public Integer getBedId() {
        return bedId;
    }
    public void setBedId(Integer bedId) {
        this.bedId = bedId;
    }
}
