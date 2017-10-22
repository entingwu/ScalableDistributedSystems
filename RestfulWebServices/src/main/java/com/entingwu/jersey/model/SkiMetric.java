package com.entingwu.jersey.model;

public class SkiMetric  {

    private String id;
    private String skierId;
    private String dayNum;
    private int totalVertical;
    private int liftNum;
    
    public SkiMetric() {}
  
    public SkiMetric(String id, String skierId, String dayNum, 
            int totalVertical, int liftNum) {
        this.id = id;
        this.skierId = skierId;
        this.dayNum = dayNum;
        this.totalVertical = totalVertical;
        this.liftNum = liftNum;
    }

    public String getSkierID() {
        return skierId;
    }

    public void setSkierID(String skierId) {
        this.skierId = skierId;
    }

    public String getDayNum() {
        return dayNum;
    }

    public void setDayNum(String dayNum) {
        this.dayNum = dayNum;
    }

    public int getTotalVertical() {
        return totalVertical;
    }

    public void setTotalVertical(int totalVertical) {
        this.totalVertical = totalVertical;
    }

    public int getLiftNum() {
        return liftNum;
    }

    public void setLiftNum(int liftNum) {
        this.liftNum = liftNum;
    }
    
    @Override
    public String toString() {
        return "id = " + id + ", skierId = " + skierId + 
                ", dayNum = " + dayNum + ", totalVertical = " + totalVertical + 
                ", liftNum = " + liftNum;
    }
}
