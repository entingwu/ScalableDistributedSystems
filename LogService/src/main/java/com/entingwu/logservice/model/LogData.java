package com.entingwu.logservice.model;

public class LogData {

    private String id;
    private int responseTime;
    private int errorNum;
    
    public LogData() {}
    
    public LogData(String id, int responseTime, int errorNum) {
        this.id = id;
        this.responseTime = responseTime;
        this.errorNum = errorNum;
    }
    
    public LogData(int responseTime, int errorNum) {
        this.responseTime = responseTime;
        this.errorNum = errorNum;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }
}
