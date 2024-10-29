package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

import com.google.firebase.Timestamp;

import org.json.JSONObject;

public class Record {
    private String recordId;
    private String diagnosis;
    private String treatment;
    private Timestamp date;
    private JSONObject extraData;

    public Record() {
    }

    public Record(String recordId, String diagnosis, String treatment, Timestamp date, JSONObject extraData) {
        this.recordId = recordId;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.date = date;
        this.extraData = extraData;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public JSONObject getExtraData() {
        return extraData;
    }

    public void setExtraData(JSONObject extraData) {
        this.extraData = extraData;
    }
}
