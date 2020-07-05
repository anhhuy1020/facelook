package com.example.facelock.Network;

import com.example.facelock.Event.EventManager;
import com.example.facelock.Event.EventType;
import com.example.facelock.Network.Package.Receive.ResponsePackage;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;


/**
 * Created by huyhq4 on 3/24/2020.
 */
public class ReceiveModule extends Thread {
    private DataInputStream dataInputStream;

    public ReceiveModule(InputStream is) {
        super();
        this.setDataInputStream(is);
    }

    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    public void setDataInputStream(InputStream is) {
        if (is == null) {
            System.out.println("null inpusteam");
            this.dataInputStream = null;
            return;
        }
        this.dataInputStream = new DataInputStream(is);
    }

    @Override
    public void run() {
        int ping;
        try {
            while (NetworkController.getInstance().isConnected()) {
                ping = dataInputStream.read();
                if (ping == -1) continue;
                int lengthData = dataInputStream.readInt();
                if (lengthData >= 4) {
                    ResponsePackage pkg = new ResponsePackage(dataInputStream.readShort());
                    pkg.setError(dataInputStream.readShort());
                    lengthData -= 4;
                    if (lengthData > 0) {
                        byte[] bytes = new byte[lengthData];
                        int retCode = dataInputStream.read(bytes, 0, lengthData);
                        if (retCode == -1) {
                            System.err.println("Read data fail");
                            return;
                        }
                        if (retCode != lengthData) {
                            System.err.println("Read data wrong size: " + retCode + " | " + lengthData);
                            return;
                        }
                        pkg.setData(bytes);
                    }
                    processPackage(pkg);
                }
            }
        } catch (Exception e) {
            System.out.println("Disconnect!");
            NetworkController.getInstance().setConnected(false);
            EventManager.disPatchEvent(EventType.DISCONNECT, null);

        }
    }

    public void processPackage(ResponsePackage pkg) {
        System.out.println(">===Receive package: " + pkg.getCmdId() + "|" + pkg.getError());
        HashMap map = new HashMap();
        map.put(ResponsePackage.class, pkg);
        EventManager.disPatchEvent(EventType.SERVER_RESPONSE, map);
    }

}
