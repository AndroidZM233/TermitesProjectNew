package com.termites.tools;

import java.io.UnsupportedEncodingException;


public class Tools {

    //byte 转十六进制
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    //十六进制转byte
    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();

        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /* byte[]转Int */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 0xFF;
        addr |= ((bytes[1] << 8) & 0xFF00);
        addr |= ((bytes[2] << 16) & 0xFF0000);
        addr |= ((bytes[3] << 25) & 0xFF000000);
        return addr;

    }

    /* Int转byte[] */
    public static byte[] intToByte(int i) {
        byte[] abyte0 = new byte[4];
        abyte0[0] = (byte) (0xff & i);
        abyte0[1] = (byte) ((0xff00 & i) >> 8);
        abyte0[2] = (byte) ((0xff0000 & i) >> 16);
        abyte0[3] = (byte) ((0xff000000 & i) >> 24);
        return abyte0;
    }

    public static int[] hexStringToByteArrayBigEndian(String s) {
        int[] numArray = new int[s.length() / 2];
        int startIndex = 0;
        int num = 0;
        while (num < s.length()) {
            numArray[num / 2] = Integer.parseInt(s.substring(startIndex, startIndex + 2), 16);
            startIndex += 2;
            num += 2;
        }
        return numArray;
    }

    public static String hexStringToString(String str) throws UnsupportedEncodingException {
        return shortArrayToString(hexStringToByteArrayBigEndian(str));
    }

    public static String shortArrayToString(int[] data) {
        StringBuilder results = new StringBuilder();
        for (int num : data) {
            String hex = Integer.toString(num);
            results.append((char) num);
        }
        return results.toString();
    }


    public static int[] hexStringToByteArray(String s) {
        int[] numArray = new int[s.length() / 2];
        int startIndex = 0;
        int num = 0;
        while (num < s.length()) {
            numArray[num / 2 - 1] = Integer.parseInt(s.substring(startIndex, startIndex + 2), 16);
            startIndex += 2;
            num += 2;
        }
        return numArray;
    }

    public static String byteArrayToHexString(short[] data) {
        StringBuilder results = new StringBuilder(data.length * 3);
        for (int num : data) {
            String hex = Integer.toHexString(num);
            hex = "00".substring(hex.length()) + hex;
            results.append(hex);
        }
        return results.toString().toUpperCase();
    }

    public static String stringToHexString(String str) throws UnsupportedEncodingException {
        return byteArrayToHexString(stringToShort(str));
    }

    public static short[] stringToShort(String str) throws UnsupportedEncodingException {
        return stringToShort(str, "UTF-8");
    }

    public static short[] stringToShort(String str, String charEncode) throws UnsupportedEncodingException {
        short[] destObj = null;
        if (null == str || str.trim().equals("")) {
            destObj = new short[0];
            return destObj;
        } else {
            byte[] bytes = str.getBytes(charEncode);
            destObj = new short[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                destObj[i] = bytes[i];
            }
        }
        return destObj;
    }


}
