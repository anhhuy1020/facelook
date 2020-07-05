package com.example.facelock.Network.Package.Receive;


import com.example.facelock.Network.Package.BasePackage;


public class ResponsePackage extends BasePackage {
    private short error;
    public ResponsePackage(short cmdId) {
        super(cmdId);
    }

    public short getError() {
        return error;
    }

    public void setError(short error) {
        this.error = error;
    }

    @Override
    public int getLengthData() {
        /*2 byte for cmdId (@type{Short}) and 2 byte for error(@type{Short})(*/
        if(getRawData() == null) return 4;
        return getRawData().length + 4;
    }
}
