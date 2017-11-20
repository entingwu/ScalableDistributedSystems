package com.entingwu.logservice.cache;

public class LogData {

    private String id;
    private int responseTime;
    private int dbQueryTime;
    private int errorNum;
    private int isPost;// post = 1, get = 0
    
    public LogData() {}
    
    public LogData(String id, int responseTime, int dbQueryTime, int errorNum, int isPost) {
        this.id = id;
        this.responseTime = responseTime;
        this.dbQueryTime = dbQueryTime;
        this.errorNum = errorNum;
        this.isPost = isPost;
    }
    
    public LogData(int responseTime, int dbQueryTime, int errorNum, int isPost) {
        this.responseTime = responseTime;
        this.dbQueryTime = dbQueryTime;
        this.errorNum = errorNum;
        this.isPost = isPost;
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
    
    public int getDbQueryTime() {
        return dbQueryTime;
    }

    public void setDbQueryTime(int dbQueryTime) {
        this.dbQueryTime = dbQueryTime;
    }
    
    public int getIsPost() {
        return isPost;
    }

    public void setIsPost(int isPost) {
        this.isPost = isPost;
    }
}
