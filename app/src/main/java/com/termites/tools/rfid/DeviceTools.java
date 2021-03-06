package com.termites.tools.rfid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.termites.R;
import com.termites.tools.Tools;
import com.speedata.libuhf.IUHFService;
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
    private final static int MAX_COUNT = 15;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            reader.select_card(1, "", false);
            int indexCount = 0;
            while (indexCount < MAX_COUNT) {
                byte[] spdata = reader.read_area(1, 2, 8,
                        "00000000");
                indexCount++;
                if (spdata == null) {

                    continue;
                }
                String epc = null;
                try {
                    String readArea = Tools.Bytes2HexString(spdata, spdata.length);
                    String substring = readArea.substring(24);
                    if (substring.equals("00000000")) {
                        readArea = readArea.substring(0, 24);
                    }
                    epc = Tools.hexStringToString(readArea);
                    if (readArea.equalsIgnoreCase("E200680B0000000000000000")) {
                        return readArea;
                    }
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
    public synchronized int writeEPC(String select, String Epc) {
        IUHFService reader = null;
        try {
            reader = getReader();
            if (!select.equalsIgnoreCase("E200680B0000000000000000")) {
                select = Tools.stringToHexString(select);
            }

            int selectCard = reader.select_card(1, select, true);
            Log.d("ZM", "writeEPC: " + select + "--" + selectCard);
            int indexCount = 0;
            while (indexCount < MAX_COUNT) {
                indexCount++;
                String toHexString = Tools.stringToHexString(Epc);
                return reader.write_area(1, "2",
                        "00000000", toHexString.length() / 4 + "", toHexString);
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
            int count = 0;
            while (!interrupted()) {
                count++;
                if (count > 5 && !isLong) {
                    Message msg = new Message();
                    msg.arg1 = 0x2;
                    handler.sendMessage(msg);
                    Log.d("ZM", "handler发送02");
                    if (!isLong) {
                        longClickStop();
                    }
                    return;
                }
                reader.select_card(1, "", false);
                Log.d("ZM", "readEPCStatus: ");
                byte[] spdata = reader.read_area(1, 2, 8,
                        "00000000");
                Log.d("ZM", "读到的EPC byte[]: " + spdata);
                if (spdata != null) {
                    try {
                        String epc = Tools.Bytes2HexString(spdata, spdata.length);
                        String substring = epc.substring(24);
                        if (substring.equals("00000000")) {
                            epc = epc.substring(0, 24);
                        }
                        Log.d("ZM", "读到的EPC: " + epc);
//                        if (Tools.hexStringToString(epc).length() == 12) {
                        Log.d("ZM", "符合的EPC: " + epc);
                        readEPCStatus(reader, mContext, epc, handler, isLong);
//                        }
                    } catch (Exception e) {
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
            int count = 0;
            while (!interrupted()) {
                count++;
                if (count < 5) {
                    reader.select_card(1, "", false);
                    Log.d("ZM", "readEPCStatus: ");
                    if (!TextUtils.isEmpty(epc)) {
                        if (epc.length() == 12) {
                            Log.d("ZM", "符合的EPC: " + epc);
                            readEPCStatus(reader, mContext, epc, handler);
                            return;
                        }
                    }
                } else {
                    Message msg = new Message();
                    msg.arg1 = 0x2;
                    handler.sendMessage(msg);
                    Log.d("ZM", "handler发送02");
                    JiaoYanStop();
                    return;
                }

            }

        }
    }


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
            while (count < 5) {
                count++;
                byte[] spdata = reader.read_area(1, 32, 1,
                        "00000000");

                if (spdata == null) {
                    Log.d("ZM", "白蚁标记为=null");
                    continue;
                }
                Log.d("ZM", "读卡: " + Tools.Bytes2HexString(spdata, spdata.length) + "--" + count);
                if (spdata.length == 2) {

                    //判断高位是否为1
                    if ((byte) (spdata[0] & 0x80) == (byte) 0x80) {
                        if (IsPlayAudio) {
                            this.playerAudio2(context);
                        }
                        epcStatusBean.setState(mContext.getString(R.string.no_have));
                    } else {
                        if (IsPlayAudio) {
                            this.playerAudio1(context);
                        }
                        epcStatusBean.setState(mContext.getString(R.string.have));
                    }

                    if (currentEPC.equalsIgnoreCase("E200680B0000000000000000")
                            || TextUtils.isEmpty(currentEPC)) {
                        epcStatusBean.setCurrentEpc(mContext.getString(R.string.unregistered));
                    } else {
                        String hexStringToString = Tools.hexStringToString(currentEPC);
                        epcStatusBean.setCurrentEpc(hexStringToString);
                    }

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
            }
            Message msg = new Message();
            msg.arg1 = 0x2;
            handler.sendMessage(msg);
            Log.d("ZM", "handler发送02");
            if (!isLong) {
                longClickStop();
            }
        } catch (Exception e) {
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
        try {
            currentEPC = Tools.stringToHexString(currentEPC);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int selectCard = reader.select_card(1, currentEPC, true);
        Log.d("ZM", "选卡: " + currentEPC + "--" + selectCard);

        int count = 0;
        try {
            while (count < 5) {
                count++;
                byte[] spdata = reader.read_area(1, 32, 1,
                        "00000000");
                if (spdata == null) {
                    Log.d("ZM", "白蚁标记为=null");
                    continue;
                }
                Log.d("ZM", "读卡: " + Tools.Bytes2HexString(spdata, spdata.length) + "--" + count);
                if (spdata.length == 2) {
                    //判断高位是否为1
                    if ((byte) (spdata[0] & 0x80) == (byte) 0x80) {
                        if (IsPlayAudio) {
                            this.playerAudio2(context);
                        }
                        epcStatusBean.setState(mContext.getString(R.string.no_have));
                    } else {
                        if (IsPlayAudio) {
                            this.playerAudio1(context);
                        }
                        epcStatusBean.setState(mContext.getString(R.string.have));
                    }
                    if (currentEPC.equalsIgnoreCase("E200680B0000000000000000")
                            || TextUtils.isEmpty(currentEPC)) {
                        epcStatusBean.setCurrentEpc(mContext.getString(R.string.unregistered));
                    } else {
                        String hexStringToString = Tools.hexStringToString(currentEPC);
                        epcStatusBean.setCurrentEpc(hexStringToString);
                    }
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
            ShowToast.getInstance().show(mContext.getString(R.string.voice_error), 0);
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
            ShowToast.getInstance().show(mContext.getString(R.string.voice_error2), 0);
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
            ShowToast.getInstance().show(mContext.getString(R.string.voice_error2), 0);
        }
    }

}
