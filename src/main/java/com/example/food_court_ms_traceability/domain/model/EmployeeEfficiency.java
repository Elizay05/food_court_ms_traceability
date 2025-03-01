package com.example.food_court_ms_traceability.domain.model;

public class EmployeeEfficiency {
    private String chefId;
    private long minutes;
    private long seconds;

    public EmployeeEfficiency(String chefId, long minutes, long seconds) {
        this.chefId = chefId;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public String getChefId () {
        return chefId;
    }

    public void setChefId(String chefId) {
        this.chefId = chefId;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }
}
