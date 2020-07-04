package com.example.facelook.Network;

import android.os.AsyncTask;

import com.example.facelook.Event.EventManager;
import com.example.facelook.Event.EventType;
import com.example.facelook.Network.Package.Send.BaseRequest;
import com.example.facelook.Network.Package.Send.RequestPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by huyhq4 on 3/24/2020.
 */
public class NetworkController {
    public static String SERVER_IP = "192.168.1.3";
    public static int SERVER_PORT = 5005;
    private static NetworkController _instance = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private SendModule sendModule = null;
    private ReceiveModule receiveModule = null;
    private volatile Boolean connected = false;

    private NetworkController() {
    }


    public static NetworkController getInstance() {
        if (_instance == null) {
            _instance = new NetworkController();
        }
        return _instance;
    }

    public SendModule getSendModule() {
        return sendModule;
    }

    public ReceiveModule getReceiveModule() {
        return receiveModule;
    }


    public boolean connectServer() {
        if (isConnected()) return true;
//        FrameManager.getInstance().getCurrentFrame().setCursor(Cursor.WAIT_CURSOR);
        int times = 1;
        while (!isConnected() && times > 0) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT); // Connect to server
                connected = true;
                if (receiveModule == null) {
                    receiveModule = new ReceiveModule(socket.getInputStream());
                    receiveModule.start();
                } else {
                }

                if (sendModule == null) {
                    sendModule = new SendModule(socket.getOutputStream());
                    sendModule.start();
                } else {
                }

//                EventManager.disPatchEvent(EventType.CONNECT_SUCCESS, null);
//                FrameManager.getInstance().getCurrentFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return connected;
            } catch (IOException ie) {
                System.err.println("Can't connect to server: " + SERVER_IP + ":" + SERVER_PORT + "!");
            }
            try {
                Thread.sleep(100);
                times -= 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!isConnected()) {
            socket = null;
            EventManager.disPatchEvent(EventType.CONNECT_FAIL, null);
        }
        return isConnected();

    }

    public Boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (!connected) {
            onDisconnect();
        }
    }

    public void onDisconnect() {
        System.out.println("onDisconnect");
        socket = null;
        sendModule= null;
        receiveModule=null;
    }

    public void send(BaseRequest request){
        request.preparePackage();
        send(request.getReqPackage());
    }
    public void send(RequestPackage pkg) {
        sendModule.enqueueRequest(pkg);
    }

    public void sendCommon(short cmdId){
        RequestPackage pkg = new RequestPackage(cmdId);
        send(pkg);
    }
}
