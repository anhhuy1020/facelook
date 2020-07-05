package com.example.facelock.Network.Package.Send;

import com.example.facelock.Network.CmdConstant;

import java.nio.ByteBuffer;

public class ReqTrain extends BaseRequest {
    private String name;

    public ReqTrain() {
        super(CmdConstant.TRAIN);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public byte[] createData(){
        ByteBuffer bf = makeBuffer();
        putStr(bf, this.name);
        return packBuffer(bf);
    }
}
