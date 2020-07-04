package com.example.facelook.Network;

import com.example.facelook.Network.Package.Send.RequestPackage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by huyhq4 on 3/24/2020.
 */
public class SendModule extends Thread {
    private final int REQUEST_SIZE = 50;
    private DataOutputStream dataOutputStream = null;
    private BlockingQueue requestQueue;

    public SendModule(OutputStream os){
        super();
        this.setOutputStream(os);
        this.requestQueue = new LinkedBlockingQueue(REQUEST_SIZE);
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setOutputStream(OutputStream os) {
        if (os == null){
            this.dataOutputStream = null;
            return;
        }
        this.dataOutputStream = new DataOutputStream(os);
    }

    @Override
    public void run() {
        try {
            while (NetworkController.getInstance().isConnected()) {
                if (!requestQueue.isEmpty()) {
                    RequestPackage pkg = (RequestPackage) requestQueue.take();
                    System.out.println("SendPackage: " + pkg.getCmdId());
                    dataOutputStream.write(0);
                    dataOutputStream.writeInt(pkg.getLengthData());
                    dataOutputStream.writeShort(pkg.getCmdId());
                    dataOutputStream.write(pkg.getRawData());
                }
            }
            //disconnect server => clear tat ca request
            requestQueue.clear();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        requestQueue.clear();
    }

    public void enqueueRequest(RequestPackage pkg) {
        System.out.println("enqueueRequest: " + pkg.getCmdId());
        if (requestQueue.size() >= REQUEST_SIZE){
            System.out.println("enqueueRequest fail because request size is full!");
            return;
        }
        requestQueue.add(pkg);
    }
}
