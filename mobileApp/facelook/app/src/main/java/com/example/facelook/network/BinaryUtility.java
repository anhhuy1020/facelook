package com.example.facelook.Network;

import java.nio.charset.Charset;

/**
 * Created by huyhq4 on 5/10/2020.
 */
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
