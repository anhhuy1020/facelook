package com.example.facelook.Network.Package.Send;

import com.example.facelook.Network.BinaryUtility;
import com.example.facelook.Network.Package.BasePackage;


import java.nio.ByteBuffer;

/**
 * Created by huyhq4 on 5/11/2020.
 */
public class BaseRequest {
    private RequestPackage reqPackage;

    public BaseRequest(short cmdId) {
        this.reqPackage = new RequestPackage(cmdId);
    }

    public RequestPackage getReqPackage() {
        return reqPackage;
    }

    public void setReqPackage(RequestPackage reqPackage) {
        this.reqPackage = reqPackage;
    }

    protected ByteBuffer makeBuffer() {
        ByteBuffer bf = ByteBuffer.allocate(BasePackage.MAX_PACKAGE_BUFFER_SIZE);
        return bf;
    }


    protected void putStr(ByteBuffer bf, String value) {
        byte[] tempByte = BinaryUtility.toByte(value);
        Integer length = tempByte.length;
        bf.putShort(length.shortValue());
        bf.put(tempByte);
    }


    protected byte[] packBuffer(ByteBuffer bf) {
        int length = bf.position();
        byte result[] = new byte[length];
        bf.flip();
        bf.get(result, 0, length);
        return result;
    }

    public void preparePackage(){
        this.reqPackage.setData(createData());
    }

    //Override me
    public byte[] createData(){
        return new byte[0];
    }
}
