package com.termites.tools.rfid;

import android.os.Handler;

/**
 * Created by LF on 16/11/14.
 */

public class NewDeviceTools {
    public static boolean isReading = false;
    private static MyThread myThread = null;
    private static Handler mHandler = null;
    public static final String LogTag = "NewDeviceTools";

    public static void initNative() {
//        Native.init();
    }

    public static void open() {
//        Native.JNI_rfidPowerOn();
    }

    public static void wirteMessage(String message) {
//        if (TextUtils.isEmpty(message)) {
//            return;
//        }
//        open();
//        Native.JNI_sendMessage(message);
    }

    public static void readMessage(Handler handler) {
//        mHandler = handler;
//        if (myThread == null) {
//            myThread = new MyThread();
//        }
//        open();
//        myThread.startRead();
    }

    public static void stopReadMessage() {
//        if (myThread != null) {
//            myThread.stopRead();
//            myThread = null;
//            mHandler = null;
//        }
    }

    public static void destory() {
//        stopReadMessage();
//        Native.JNI_rfidPowerOff();
    }

    protected static class MyThread extends Thread {

        @Override
        public void run() {
//            while (isReading) {
//                if (mHandler == null) {
//                    isReading = false;
//                    break;
//                }
//                String message = Native.JNI_getMessage().toString();
//                Message msg = new Message();
//                msg.obj = message;
//                mHandler.sendMessage(msg);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                // 循环读取信息一秒钟之后,结束循环,停止当前读取操作
//                isReading = false;
//                break;
//            }
        }

        public void stopRead() {
            isReading = false;
        }

        public void startRead() {
//            this.start();
//            isReading = true;
        }
    }
}
