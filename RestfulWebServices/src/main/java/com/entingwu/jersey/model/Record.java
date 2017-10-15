package com.entingwu.jersey.model;

public class Record  {
    
    private String resortID;
    private int dayNum;
    private String skierID;
    private String liftID;
    private String time;

    public Record() {}

    public String getResortID() {
        return resortID;
    }

    public void setResortID(String resortID) {
        this.resortID = resortID;
    }

    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    public String getSkierID() {
        return skierID;
    }

    public void setSkierID(String skierID) {
        this.skierID = skierID;
    }

    public String getLiftID() {
        return liftID;
    }

    public void setLiftID(String liftID) {
        this.liftID = liftID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "resortID = " + resortID + "'\', dayNum = " + dayNum + 
                "'\', skierID = " + skierID + "'\', liftID = " + liftID + 
                "'\', time = " + time;
    }
}
