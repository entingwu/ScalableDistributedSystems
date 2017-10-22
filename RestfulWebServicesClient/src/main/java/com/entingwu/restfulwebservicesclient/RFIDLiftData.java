package com.entingwu.restfulwebservicesclient;

public class RFIDLiftData  {
    
    private String resortId;
    private String dayNum;
    private String skierId;
    private int liftId;
    private String timestamp;
    public boolean flag = false;

    public RFIDLiftData(String resortId, String dayNum, String skierId, 
            int liftId, String timestamp) {
        this.resortId = resortId;
        this.dayNum = dayNum;
        this.skierId = skierId;
        this.liftId = liftId;
        this.timestamp = timestamp;
    }
    
    public RFIDLiftData() {}

    public String getResortID() {
        return resortId;
    }

    public void setResortID(String resortId) {
        this.resortId = resortId;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public String getSkierID() {
        return skierId;
    }

    public void setSkierID(String skierId) {
        this.skierId = skierId;
    }

    public int getLiftID() {
        return liftId;
    }

    public void setLiftID(int liftId) {
        this.liftId = liftId;
    }

    public String getTime() {
        return timestamp;
    }

    public void setTime(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "resortID = " + resortId + ", dayNum = " + dayNum + 
                ", skierID = " + skierId + ", liftID = " + liftId + 
                ", timestamp = " + timestamp;
    }
}