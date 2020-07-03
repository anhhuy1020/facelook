package com.example.facelook.network;

import java.io.IOException;
import java.net.Socket;

public class NetworkController {
    public static String SERVER_IP = "127.0.0.1";
    public static int SERVER_PORT = 5005;
    private Socket socket = null;


    private static NetworkController _instance = new NetworkController();

    private NetworkController() {
    }

    public static NetworkController getInstance(){
        return _instance;
    }

    public void connectServer(){
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
