package com.termites.tools.rfid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.termites.tools.Tools;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.Tag_Data;
import com.termites.tools.ShowToast;
import com.termites.tools.javabean.EpcStatusBean;
import com.termites.ui.OurApplication;

import java.io.UnsupportedEncodingException;

/**
 * 手持仪与硬件交互工具类
 * Created by LF on 16/10/26.
 */

public class DeviceTools {

    private static final Object lock = new Object();
    private static DeviceTools deviceTools;
    public static String LogTag = "DeviceTools";

    public static boolean IsPlayAudio = true;
    private final static int MAX_COUNT = 10;
    private static boolean IsReading = false;
    private static boolean ReadStatus = false;
    private static boolean SelectStatus = false;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private LongClickThread longClickThread;
    private JiaoYanThread jiaoYanThread;

    public static DeviceTools getDeviceToolsInstance(Context context) {
        mContext = context;
        if (deviceTools == null) {
            synchronized (lock) {
                if (deviceTools == null) {
                    deviceTools = new DeviceTools();
                }
            }
        }
        return deviceTools;
    }

    public synchronized IUHFService getReader() {
        try {
            if (OurApplication.mReader == null) {
                Log.d("ZM", "mReader == null");
                OurApplication.getInstances().getmReader();
//                OurApplication.mReader.OpenDev();
//                int antennaPower = OurApplication.mReader.set_antenna_power(30);
//                if (antennaPower == 0) {
//                    ShowToast.getInstance().show("功率设置成功", 0);
//                } else {
//                    ShowToast.getInstance().show("功率设置失败", 0);
//                }
            }
        } catch (Exception e) {
//            ShowToast.getInstance().show("功率设置异常", 0);
        }
        return OurApplication.mReader;
    }

    public synchronized void closeReader() {
        if (OurApplication.mReader != null) {
            OurApplication.mReader.CloseDev();
        }
    }

    public synchronized void openReader() {
        if (OurApplication.mReader != null) {
            OurApplication.mReader.OpenDev();
        }
    }

    /**
     * 读取EPC当前的标签(监测点编号)
     */
    public synchronized String readEPC() {
        IUHFService reader = null;
        try {
            reader = getReader();
            int maxCount = MAX_COUNT;
            int indexCount = 0;
            while (indexCount < maxCount) {
                String readArea = reader.read_area(1, "2", "6", "00000000");
                indexCount++;
                if (TextUtils.isEmpty(readArea)) {
                    continue;
                }
                String epc = null;
                try {
                    epc = Tools.hexStringToString(readArea);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                boolean validEpc = true;
                for (int i = 0; i < epc.length(); i++) {
                    char letter = epc.charAt(i);
                    if ((letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z') || (letter >= '0' && letter <= '9')) {
                    } else {
                        validEpc = false;
                        break;
                    }
                }
                if (validEpc) {
                    return epc;
                } else {
                    Thread.sleep(20);
                    continue;
                }
            }

        } catch (Exception e) {
            ShowToast.getInstance().show("标签读取异常", 0);
        }
        return null;
    }


    /**
     * 向EPC写入标签(监测点编号)
     */
    public synchronized int writeEPC(String Epc) {
        IUHFService reader = null;
        try {
            reader = getReader();
            int maxCount = MAX_COUNT;
            int indexCount = 0;
            while (indexCount < maxCount) {
                Thread.sleep(20);
                indexCount++;
                String toHexString = Tools.stringToHexString(Epc);
                return reader.write_area(1, "2",
                        "00000000", "6", toHexString);
            }
        } catch (Exception e) {
            ShowToast.getInstance().show("标签写入异常", 0);
        }
        return -1;
    }

    /**
     * 暂停读取EPC
     */
    public synchronized void pauseReaderEPC() {
        IsReading = false;
    }


//    public void readEPCStatus(final Handler handler) {
////        InventoryThread inventoryThread = new InventoryThread(handler);
////        inventoryThread.start();
//
//        final IUHFService reader = getReader();
//        reader.select_card(1, "", false);
//        Log.d("ZM", "readEPCStatus: ");
//        reader.setListener(new IUHFService.Listener() {
//            @Override
//            public void update(Tag_Data var1) {
//                final String epc = var1.epc;
//                Log.d("ZM", "盘点到数据: " + epc);
//                if (!TextUtils.isEmpty(epc)) {
//                    try {
//                        if (Tools.hexStringToString(epc).length() == 12) {
//                            reader.newInventoryStop();
//                            Log.d("ZM", "盘停止盘点就去读卡阶段: " + epc);
//                            ReadEPCThread readEPCThread = new ReadEPCThread(handler, reader, epc, mContext);
//                            readEPCThread.start();
//                        }
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        reader.newInventoryStart();
//        Log.d("ZM", "readEPCStatus: end");
//
//    }

    public void longClickStatus(final Handler handler, boolean isLong) {
        if (longClickThread == null) {
            longClickThread = new LongClickThread(handler, isLong);
            longClickThread.start();
        }
    }

    public void longClickStop() {
        if (longClickThread != null) {
            longClickThread.interrupt();
            longClickThread = null;
        }

    }

    public void JiaoYanStart(final Handler handler, String epc) {
        if (jiaoYanThread == null) {
            jiaoYanThread = new JiaoYanThread(handler, epc);
            jiaoYanThread.start();
        }
    }

    public void JiaoYanStop() {
        if (jiaoYanThread != null) {
            jiaoYanThread.interrupt();
            jiaoYanThread = null;
        }

    }


//    class InventoryThread extends Thread {
//        private Handler handler;
//
//        public InventoryThread(Handler handler) {
//            this.handler = handler;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            final IUHFService reader = getReader();
//            reader.select_card(1, "", false);
//            Log.d("ZM", "readEPCStatus: ");
//            reader.setListener(new IUHFService.Listener() {
//                @Override
//                public void update(Tag_Data var1) {
//                    final String epc = var1.epc;
//                    Log.d("ZM", "盘点到数据: " + epc);
//                    if (!TextUtils.isEmpty(epc)) {
//                        try {
//                            if (Tools.hexStringToString(epc).length() == 12) {
//                                reader.newInventoryStop();
//                                Log.d("ZM", "盘停止盘点就去读卡阶段: " + epc);
//                                ReadEPCThread readEPCThread = new ReadEPCThread(handler, reader, epc, mContext);
//                                readEPCThread.start();
//                            }
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            reader.newInventoryStart();
//            Log.d("ZM", "readEPCStatus: end");
//        }
//    }


    class LongClickThread extends Thread {
        private Handler handler;
        private boolean isLong;

        public LongClickThread(Handler handler, boolean isLong) {
            this.handler = handler;
            this.isLong = isLong;
        }

        @Override
        public void run() {
            super.run();
            final IUHFService reader = getReader();
            while (!interrupted()) {
//                SystemClock.sleep(400);
                reader.select_card(1, "", false);
                Log.d("ZM", "readEPCStatus: ");
//                SystemClock.sleep(200);
                String epc = reader.read_area(1, "2", "6", "00000000");
                Log.d("ZM", "读到的EPC: " + epc);
                if (!TextUtils.isEmpty(epc)) {
                    try {
                        if (Tools.hexStringToString(epc).length() == 12) {
                            Log.d("ZM", "符合的EPC: " + epc);
                            readEPCStatus(reader, mContext, epc, handler, isLong);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    class JiaoYanThread extends Thread {
        private Handler handler;
        private String epc;

        public JiaoYanThread(Handler handler, String epc) {
            this.handler = handler;
            this.epc = epc;
        }

        @Override
        public void run() {
            super.run();
            final IUHFService reader = getReader();
            while (!interrupted()) {
                reader.select_card(1, "", false);
                Log.d("ZM", "readEPCStatus: ");
                if (!TextUtils.isEmpty(epc)) {
                    try {
                        if (Tools.hexStringToString(epc).length() == 12) {
                            Log.d("ZM", "符合的EPC: " + epc);
                            readEPCStatus(reader, mContext, epc, handler);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


//    class ReadEPCThread extends Thread {
//        private Handler handler;
//        private IUHFService reader;
//        private String epc;
//        private Context context;
//
//        public ReadEPCThread(Handler handler, IUHFService reader, String epc, Context context) {
//            this.handler = handler;
//            this.reader = reader;
//            this.epc = epc;
//            this.context = context;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//            readEPCStatus(reader, context, epc, handler);
//        }
//    }

    /**
     * 读取EPC的状态(检测白蚁)
     * status = false 无
     * status = false 有
     */
    private synchronized void readEPCStatus(IUHFService reader, Context context,
                                            String currentEPC, Handler handler, boolean isLong) {
        IsReading = true;
        EpcStatusBean epcStatusBean = new EpcStatusBean();
        int selectCard = reader.select_card(1, currentEPC, true);
        Log.d("ZM", "选卡: " + currentEPC + "--" + selectCard);

        int count = 0;
        try {
            while (IsReading && count < 10) {
                count++;
//                SystemClock.sleep(200);
                String area = reader.read_area(1, "32", "1",
                        "00000000");
                Log.d("ZM", "读卡: " + area + "--" + count);
                if (TextUtils.isEmpty(area)) {
                    continue;
                }
                byte[] spdata = Tools.HexString2Bytes(area);
                if (spdata == null) {
                    continue;
                }
                if (spdata != null && spdata.length > 1) {
                    if (currentEPC.equalsIgnoreCase("E200680B0000000000000000") || TextUtils.isEmpty(currentEPC)) {
                        Message msg = new Message();
                        msg.arg1 = 0x2;
                        handler.sendMessage(msg);
                        Log.d("ZM", "handler发送02");
                        if (!isLong) {
                            longClickStop();
                        }
                        return;
                    }
                    //判断高位是否为1
                    if ((byte) (spdata[0] & 0x80) == (byte) 0x80) {
                        if (IsPlayAudio) {
                            this.playerAudio2(context);
                        }
                        epcStatusBean.setState("无");
                    } else {
                        if (IsPlayAudio) {
                            this.playerAudio1(context);
                        }
                        epcStatusBean.setState("有");
                    }
                    epcStatusBean.setCurrentEpc(Tools.hexStringToString(currentEPC));
                    Message msg = new Message();
                    msg.arg1 = 0x1;
                    msg.obj = epcStatusBean;
                    handler.sendMessage(msg);
                    Log.d("ZM", "handler发送01");
                    if (!isLong) {
                        longClickStop();
                    }
                    return;
                }
//                }
            }
            Message msg = new Message();
            msg.arg1 = 0x2;
            handler.sendMessage(msg);
            Log.d("ZM", "handler发送02");
            if (!isLong) {
                longClickStop();
            }
        } catch (Exception e) {
//            ShowToast.getInstance().show("读取EPC的状态异常", 0);
            Message msg = new Message();
            msg.arg1 = 0x2;
            handler.sendMessage(msg);
            Log.d("ZM", "读取EPC的状态异常handler发送02");
            if (!isLong) {
                longClickStop();
            }
        }
    }


    /**
     * 读取EPC的状态(检测白蚁)
     * status = false 无
     * status = false 有
     */
    private synchronized void readEPCStatus(IUHFService reader, Context context,
                                            String currentEPC, Handler handler) {
        IsReading = true;
        EpcStatusBean epcStatusBean = new EpcStatusBean();
        int selectCard = reader.select_card(1, currentEPC, true);
        Log.d("ZM", "选卡: " + currentEPC + "--" + selectCard);

        int count = 0;
        try {
            while (IsReading && count < 10) {
                count++;
                String area = reader.read_area(1, "32", "1",
                        "00000000");
                Log.d("ZM", "读卡: " + area + "--" + count);
                if (TextUtils.isEmpty(area)) {
                    continue;
                }
                byte[] spdata = Tools.HexString2Bytes(area);
                if (spdata == null) {
                    continue;
                }
                if (spdata != null && spdata.length > 1) {
                    if (currentEPC.equalsIgnoreCase("E200680B0000000000000000") || TextUtils.isEmpty(currentEPC)) {
                        Message msg = new Message();
                        msg.arg1 = 0x2;
                        handler.sendMessage(msg);
                        Log.d("ZM", "handler发送02");
                        JiaoYanStop();
                        return;
                    }
                    //判断高位是否为1
                    if ((byte) (spdata[0] & 0x80) == (byte) 0x80) {
                        if (IsPlayAudio) {
                            this.playerAudio2(context);
                        }
                        epcStatusBean.setState("无");
                    } else {
                        if (IsPlayAudio) {
                            this.playerAudio1(context);
                        }
                        epcStatusBean.setState("有");
                    }
                    epcStatusBean.setCurrentEpc(Tools.hexStringToString(currentEPC));
                    Message msg = new Message();
                    msg.arg1 = 0x1;
                    msg.obj = epcStatusBean;
                    handler.sendMessage(msg);
                    Log.d("ZM", "handler发送01");
                    JiaoYanStop();
                    return;
                }
            }
            Message msg = new Message();
            msg.arg1 = 0x2;
            handler.sendMessage(msg);
            Log.d("ZM", "handler发送02");
            JiaoYanStop();
        } catch (Exception e) {
            Message msg = new Message();
            msg.arg1 = 0x2;
            handler.sendMessage(msg);
            Log.d("ZM", "读取EPC的状态异常handler发送02");
            JiaoYanStop();
        }
    }


    private static MediaPlayer mediaPlayer = null;
    private static MediaPlayer mediaPlayer1 = null;
    private static MediaPlayer mediaPlayer2 = null;

    /**
     * 扫描到白蚁时播放的音频文件
     */
    public void playerAudio1(Context context) {
        try {
            if (mediaPlayer2 != null) {
                mediaPlayer2.release();
            }
            if (mediaPlayer1 != null) {
                mediaPlayer1.release();
            }
            mediaPlayer1 = new MediaPlayer();
            AssetFileDescriptor afd = context.getResources().getAssets().openFd("video.mp3");
            mediaPlayer1.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer1.prepare();
            mediaPlayer1.start();
        } catch (Exception e) {
            ShowToast.getInstance().show("音频文件播放异常", 0);
        }

    }

    /**
     * 没有扫描到白蚁时播放的音频文件
     */
    public void playerAudio2(Context context) {
        try {
            if (mediaPlayer1 != null) {
                mediaPlayer1.release();
            }
            if (mediaPlayer2 != null) {
                mediaPlayer2.release();
            }
            mediaPlayer2 = new MediaPlayer();
            AssetFileDescriptor afd = context.getResources().getAssets().openFd("1688.mp3");
            mediaPlayer2.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer2.prepare();
            mediaPlayer2.start();
        } catch (Exception e) {
            ShowToast.getInstance().show("音频文件播放异常", 0);
        }
    }

    /**
     * 物理按键开启扫描时播放的音频文件
     */
    public void playerAudio(Context context) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            if (mediaPlayer1 != null) {
                mediaPlayer1.release();
            }
            if (mediaPlayer2 != null) {
                mediaPlayer2.release();
            }
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context.getResources().getAssets().openFd("scankey.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            ShowToast.getInstance().show("音频文件播放异常", 0);
        }
    }

}
