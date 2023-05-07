package com.example.coversong.data;

public class RecordModel {

    private String recordUri;

    RecordModel(){

    }

    public RecordModel(String recordUri) {
        this.recordUri = recordUri;
    }

    public String getRecordUri() {
        return recordUri;
    }

    public void setRecordUri(String recordUri) {
        this.recordUri = recordUri;
    }
}
