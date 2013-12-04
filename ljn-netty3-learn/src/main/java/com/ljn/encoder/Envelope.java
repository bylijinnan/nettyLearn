package com.ljn.encoder;
public class Envelope {
    private int version;
    private int type;
    private byte[] payload;
 
    public Envelope() {
    }
 
    public Envelope(int version, int type, byte[] payload) {
        this.version = version;
        this.type = type;
        this.payload = payload;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
 
}