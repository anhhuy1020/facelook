package com.example.facelook.Network.Package;


/**
 * Created by huyhq4 on 3/26/2020.
 */
public abstract class BasePackage {
    public static final int MAX_PACKAGE_BUFFER_SIZE = 5012;

    private short cmdId;
    private byte[] data;

    public BasePackage(short cmdId) {
        this.cmdId = cmdId;
    }

    public short getCmdId() {
        return cmdId;
    }

    public void setCmdId(short cmdId) {
        this.cmdId = cmdId;
    }

    public byte[] getRawData() {
        if(data == null) data = new byte[0];
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLengthData() {
        return getRawData().length + 2;
    }
}
