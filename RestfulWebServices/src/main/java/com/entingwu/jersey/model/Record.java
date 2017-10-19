package com.entingwu.jersey.model;

public class Record  {
    
    private static final int[] VERTICAL = {200, 300, 400, 500};
    private String id;
    private String resortId;
    private String dayNum;
    private String skierId;
    private int liftId;
    private String timestamp;
    
    public Record() {}
    
    public Record(String resortId, String dayNum, String skierId, 
            int liftId, String timestamp) {
        this.resortId = resortId;
        this.dayNum = dayNum;
        this.skierId = skierId;
        this.liftId = liftId;
        this.timestamp = timestamp;
    }
    
    public Record(String id, String resortId, String dayNum, String skierId, 
            int liftId, String timestamp) {
        this.id = id;
        this.resortId = resortId;
        this.dayNum = dayNum;
        this.skierId = skierId;
        this.liftId = liftId;
        this.timestamp = timestamp;
    }
    
    public int getVertical() {
        int index = (liftId - 1) / 10;
        return VERTICAL[index];
    }

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
        return "id = " + id + ", resortID = " + resortId + 
                ", dayNum = " + dayNum + ", skierID = " + skierId + 
                ", liftID = " + liftId + ", timestamp = " + timestamp;
    }
}
