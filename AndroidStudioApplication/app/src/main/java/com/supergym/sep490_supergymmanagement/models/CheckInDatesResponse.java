// CheckInDatesResponse.java
package com.supergym.sep490_supergymmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CheckInDatesResponse {
    @SerializedName("checkInDates")
    private List<String> checkInDates;

    public List<String> getCheckInDates() {
        return checkInDates;
    }

    public void setCheckInDates(List<String> checkInDates) {
        this.checkInDates = checkInDates;
    }
}
