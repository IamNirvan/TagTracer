package com.tagtracer.rfid.tag;

public class RFIDTag {
    private String tid;
    private String rssi;

    public String getTid() { return tid; }
    public String getRssi() { return rssi; }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public RFIDTag(String tid, String rssi) {
        this.tid = tid;
        this.rssi = rssi;
    }
}
