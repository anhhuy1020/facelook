package com.example.facelock.Network;

import java.nio.charset.Charset;
public class BinaryUtility {
    private static Charset charset = Charset.forName("UTF-8");

    public static String toString(byte[] b) {
        int len = b.length;
        return new String(b, 0, len, charset);
    }


    public static byte[] toByte(String value) {
        return value.getBytes(charset);
    }


}
