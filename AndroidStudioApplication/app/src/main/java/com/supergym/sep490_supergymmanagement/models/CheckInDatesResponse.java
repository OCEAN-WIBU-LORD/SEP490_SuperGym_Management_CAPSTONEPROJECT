// CheckInDatesResponse.java
package com.supergym.sep490_supergymmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CheckInDatesResponse {

    @SerializedName("checkInDates")
    private List<CheckInDateInfo> checkInDates;

    // Getters và Setters
    public List<CheckInDateInfo> getCheckInDates() {
        return checkInDates;
    }

    public void setCheckInDates(List<CheckInDateInfo> checkInDates) {
        this.checkInDates = checkInDates;
    }
}
