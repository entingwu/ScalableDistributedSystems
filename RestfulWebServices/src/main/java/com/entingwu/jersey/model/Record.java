package com.entingwu.jersey.model;

public class Record  {
    
    private String id;
    private String resort_id;
    private int day_num;
    private String skier_id;
    private String lift_id;
    private String timestamp;
    
    public Record() {}
    
    public Record(String resort_id, int day_num, String skier_id, 
            String lift_id, String timestamp) {
        this.resort_id = resort_id;
        this.day_num = day_num;
        this.skier_id = skier_id;
        this.lift_id = lift_id;
        this.timestamp = timestamp;
    }
    
    public Record(String id, String resort_id, int day_num, String skier_id, 
            String lift_id, String timestamp) {
        this.id = id;
        this.resort_id = resort_id;
        this.day_num = day_num;
        this.skier_id = skier_id;
        this.lift_id = lift_id;
        this.timestamp = timestamp;
    }

    public String getResortID() {
        return resort_id;
    }

    public void setResortID(String resort_id) {
        this.resort_id = resort_id;
    }

    public int getDayNum() {
        return day_num;
    }

    public void setDayNum(int day_num) {
        this.day_num = day_num;
    }

    public String getSkierID() {
        return skier_id;
    }

    public void setSkierID(String skier_id) {
        this.skier_id = skier_id;
    }

    public String getLiftID() {
        return lift_id;
    }

    public void setLiftID(String lift_id) {
        this.lift_id = lift_id;
    }

    public String getTime() {
        return timestamp;
    }

    public void setTime(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "id = " + id + "resortID = " + resort_id + 
                ", dayNum = " + day_num + ", skierID = " + skier_id + 
                ", liftID = " + lift_id + ", timestamp = " + timestamp;
    }
}
