package com.example.facelook.Network.Package.Receive;

import com.example.facelook.Network.BinaryUtility;
import com.example.facelook.Network.ErrorConstant;

import java.nio.ByteBuffer;

/**
 * Created by huyhq4 on 5/11/2020.
 */
public class BaseResponse {
    private ResponsePackage resPackage;
    private short error;

    public BaseResponse(ResponsePackage resPackage) {
        this(resPackage, ErrorConstant.SUCCESS);
    }

    public BaseResponse(ResponsePackage resPackage, short error) {
        this.resPackage = resPackage;
        this.error = error;
        unpackData();
    }

    //Override me
    public void unpackData(){
    }

    protected ByteBuffer makeBuffer() {
        ByteBuffer buffer = ByteBuffer.wrap(getResPackage().getRawData());
        return buffer;
    }

    protected String readString(ByteBuffer buffer) {
        String data = "";
        try {
            short length = buffer.getShort();
            byte[] b = new byte[length];
            buffer.get(b);
            data = BinaryUtility.toString(b);
        } catch (Exception e) {

        }
        return data;
    }

    public ResponsePackage getResPackage() {
        return resPackage;
    }

    public void setResPackage(ResponsePackage resPackage) {
        this.resPackage = resPackage;
    }

    public short getError() {
        return error;
    }

    public void setError(short error) {
        this.error = error;
    }
}
