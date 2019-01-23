package com.termites.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.M;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.SpdReadData;
import com.speedata.libuhf.interfaces.OnSpdReadListener;
import com.speedata.libuhf.utils.StringUtils;
import com.termites.R;
import com.termites.tools.EmptyViewListView;
import com.termites.tools.ShowToast;
import com.termites.tools.Tools;
import com.termites.tools.adapter.InspectionManagerAdapter;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.EpcStatusBean;
import com.termites.tools.javabean.InspectionBean;
import com.termites.tools.rfid.DeviceTools;
import com.termites.tools.rfid.NewDeviceTools;
import com.termites.ui.base.BaseWithTitleBackActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static com.termites.tools.rfid.DeviceTools.IsPlayAudio;

/**
 * Created by LF on 16/10/20.
 */

public class InspectionManagerActivity extends BaseWithTitleBackActivity implements View.OnClickListener {
    private EmptyViewListView inspection_lv;
    private InspectionManagerAdapter mInspectionManagerAdapter;
    // 开始检测按钮
    private RelativeLayout inspection_bottom_start_rl;
    private TextView inspection_bottom_text;
    private TextView check_bottom_text;
    private DataHelper dataHelper;
    private ArrayList<InspectionBean> inspectionBeanArrayList = new ArrayList<>();
    private MyThread myThread;
    private boolean isStart = false;
    private volatile boolean isLongClick = false;
    private IUHFService iuhfService;


    @Override
    protected void onResume() {
        super.onResume();
        if (LocalcacherConfig.isCloseTest) {
            if (LocalcacherConfig.isUseNewDeviceCode) {
                NewDeviceTools.initNative();
            } else {
                DeviceTools.getDeviceToolsInstance(getApplicationContext()).openReader();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inspection_manager);
        dataHelper = new DataHelper(this);

        initView();

//        iuhfService = OurApplication.mReader;
//        iuhfService.setOnReadListener(new OnSpdReadListener() {
//            @Override
//            public void getReadData(SpdReadData var1) {
//                int status = var1.getStatus();
//                if (status == 0) {
//                    byte[] epcData = var1.getEPCData();
//                    String hexString = StringUtils.byteToHexString(epcData, var1.getEPCLen());
//                    String hexStringToString = null;
//                    try {
//                        hexStringToString = Tools.hexStringToString(hexString);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    if (TextUtils.isEmpty(hexStringToString)) {
//                        hexStringToString = "dekan";
//                    }
//                    byte[] spdata = var1.getReadData();
//                    if (hexString.equalsIgnoreCase("E200680B0000000000000000") || spdata == null) {
//                        Message msg = new Message();
//                        msg.arg1 = 0x2;
//                        myHandler.sendMessage(msg);
//                        Log.d("ZM", "handler发送02");
//                        return;
//                    }
//                    EpcStatusBean epcStatusBean = new EpcStatusBean();
//
//                    //判断高位是否为1
//                    if ((byte) (spdata[0] & 0x80) == (byte) 0x80) {
//                        if (IsPlayAudio) {
//                            DeviceTools.getDeviceToolsInstance(getApplicationContext())
//                                    .playerAudio2(getApplicationContext());
//                        }
//                        epcStatusBean.setState("无");
//                    } else {
//                        if (IsPlayAudio) {
//                            DeviceTools.getDeviceToolsInstance(getApplicationContext())
//                                    .playerAudio1(getApplicationContext());
//                        }
//                        epcStatusBean.setState("有");
//                    }
//                    epcStatusBean.setCurrentEpc(hexStringToString);
//                    Message msg = new Message();
//                    msg.arg1 = 0x1;
//                    msg.obj = epcStatusBean;
//                    myHandler.sendMessage(msg);
//                    Log.d("ZM", "handler发送01");
//                }else {
//                    Message msg = new Message();
//                    msg.arg1 = 0x2;
//                    myHandler.sendMessage(msg);
//                    Log.d("ZM", "handler发送02");
//                }
//            }
//        });
    }

    private void initView() {
        setTitleTxt("巡检管理");
        setTitleRightTxt("查询");
        setTitleRightTextVisility(View.VISIBLE);

        inspection_lv = $_Act(R.id.inspection_lv);
        inspection_lv.setEmptyView(MethodConfig.getEmptyViewWithText(this, "暂无巡检数据", R.drawable.emptydata_inspection_icon_2));
        mInspectionManagerAdapter = new InspectionManagerAdapter(getActivity());
        inspection_lv.setAdapter(mInspectionManagerAdapter);

        inspection_bottom_text = $_Act(R.id.inspection_bottom_text);

        inspection_bottom_start_rl = $_Act(R.id.inspection_bottom_start_rl);
        inspection_bottom_start_rl.setOnClickListener(this);
        inspection_bottom_start_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLongClick) {
                    isLongClick = true;
                    Log.d("ZM", "长按 isLongClick:" + isLongClick);
                    inspection_bottom_text.setText("停止巡检");
                    inspection_bottom_start_rl.setBackgroundColor(getResources().getColor(R.color.text_red));
                    DeviceTools.getDeviceToolsInstance(getApplicationContext())
                            .longClickStatus(myHandler, true);
                }
                return true;
            }
        });

        check_bottom_text = $_Act(R.id.check_bottom_text);
        check_bottom_text.setOnClickListener(this);
    }

    @Override
    public void onClickTitleRightTxt(TextView mTextView) {
        super.onClickTitleRightTxt(mTextView);
        if (isLongClick) {
            isLongClick = false;
            DeviceTools.getDeviceToolsInstance(getApplicationContext()).longClickStop();
        }
        pauseReaderEPC();
        Intent intent = new Intent(getActivity(), EquipmentOrInspectionHistoryRecordActivity.class);
        intent.putExtra(LocalcacherConfig.KEY_HistoryRecordType, "inspection");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inspection_bottom_start_rl:
                if (inspection_bottom_text.getText().toString().equals("开始巡检")) {
                    inspection_bottom_text.setText("停止巡检");
                    inspection_bottom_start_rl.setBackgroundColor(getResources().getColor(R.color.text_red));
                    Log.d("ZM", "单次Click开始");
                    if (LocalcacherConfig.isCloseTest) {
                        if (LocalcacherConfig.isUseNewDeviceCode) {
                            NewDeviceTools.readMessage(mHandler);
                        } else {
                            // 开始巡检
                            DeviceTools.getDeviceToolsInstance(getApplicationContext())
                                    .longClickStatus(myHandler, false);
//                            readArea();
                        }
                    } else {
                        InspectionBean bean = new InspectionBean();
                        if (mInspectionManagerAdapter.getCount() > 2) {
                            bean.setInspecId("573HA0010001");
                            bean.setInspectionTermiteState("有");

                        } else {
                            bean.setInspecId("573HA0010002");
                            bean.setInspectionTermiteState("无");
                        }
                        bean.setShowId(mInspectionManagerAdapter.getCount() + 1);
                        bean.setInspectionUploadState("未上传");
                        bean.setInspectionTime(MethodConfig.getSystemTime());
                        pauseReaderEPC();
                        // 巡检到一条数据就往数据库存一条数据
                        insertInspectionData(bean);
                    }
                } else if (inspection_bottom_text.getText().toString().equals("停止巡检")) {
                    Log.d("ZM", "单次Click停止 isLongClick:" + isLongClick);
                    if (isLongClick) {
                        isLongClick = false;
                        DeviceTools.getDeviceToolsInstance(getApplicationContext()).longClickStop();
                    }
                    pauseReaderEPC();
                    inspection_bottom_text.setText("开始巡检");
                    inspection_bottom_start_rl.setBackgroundColor(getResources().getColor(R.color.text_green));
                }
                break;
            case R.id.check_bottom_text:
                if (isLongClick) {
                    isLongClick = false;
                    DeviceTools.getDeviceToolsInstance(getApplicationContext()).longClickStop();
                }
                pauseReaderEPC();
                CheckDialog checkDialog = new CheckDialog(this);
                checkDialog.setTitle("查找");
                checkDialog.setCancelable(false);
                checkDialog.show();
                break;
            default:
                break;
        }
    }

    private void readArea() {
        iuhfService.newReadArea(1, 32, 1, "00000000");
    }

    class MyThread extends Thread {
        @Override
        public void run() {
//            EpcStatusBean epcStatusBean = DeviceTools.getDeviceToolsInstance().readEPCStatus(getActivity());
//            if (epcStatusBean != null && !TextUtils.isEmpty(epcStatusBean.getCurrentEpc())) {
//                Message msg = new Message();
//                msg.arg1 = 0x1;
//                msg.obj = epcStatusBean;
//                myHandler.sendMessage(msg);
//            } else {
//                pauseReaderEPC();
//                ShowToast.getInstance().show("监测装置未发现", 0);
//            }
        }
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0x1) {
                EpcStatusBean epcStatusBean = (EpcStatusBean) msg.obj;
                Log.w(DeviceTools.LogTag, "巡检数据: " +
                        "\n当前监测点编号: " + epcStatusBean.getCurrentEpc() +
                        "\n白蚁状态: " + epcStatusBean.getState());
                InspectionBean bean = new InspectionBean();
                bean.setInspectionTermiteState(epcStatusBean.getState());
                bean.setInspecId(epcStatusBean.getCurrentEpc());
                bean.setShowId(mInspectionManagerAdapter.getCount() + 1);
                bean.setInspectionUploadState("未上传");
                bean.setInspectionTime(MethodConfig.getSystemTime());
                // 巡检到一条数据就往数据库存一条数据
                insertInspectionData(bean);
                if (!isLongClick) {
                    pauseReaderEPC();
                    Log.d("ZM", "handleMessage: 01短按");
                } else {
                    Log.d("ZM", "handleMessage: 01长按继续巡检");
                }
            } else if (msg.arg1 == 0x2) {
                if (!isLongClick) {
                    pauseReaderEPC();
                    ShowToast.getInstance().show("读取EPC的状态失败,请重新!", 0);
                    Log.d("ZM", "handleMessage: 02短按");
                } else {
                    ShowToast.getInstance().show("读取EPC的状态失败", 0);
                    Log.d("ZM", "handleMessage: 02长按继续巡检");
                }
            }
        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.w(NewDeviceTools.LogTag, "巡检数据: " + msg.obj.toString());
            InspectionBean bean = new InspectionBean();
            bean.setInspectionTermiteState("白蚁状态");
            bean.setInspecId("当前检测点编号");
            bean.setShowId(mInspectionManagerAdapter.getCount() + 1);
            bean.setInspectionUploadState("未上传");
            bean.setInspectionTime(MethodConfig.getSystemTime());
            // 巡检到一条数据就往数据库存一条数据
//            insertInspectionData(bean);
        }
    };

    public void insertInspectionData(InspectionBean bean) {
        dataHelper.insertInspectionData(bean);
        getInspectionData(bean);
    }

    public void getInspectionData(InspectionBean bean) {
        if (mInspectionManagerAdapter.getCount() == 0) {
            inspectionBeanArrayList.add(bean);
            mInspectionManagerAdapter.clear();
            mInspectionManagerAdapter.addAll(inspectionBeanArrayList);
        } else {
            mInspectionManagerAdapter.addNewsItem(bean);
        }
        Log.w(DeviceTools.LogTag, "数据库巡检数据长度: " + dataHelper.getInspectionAllData().size());
    }

    public void pauseReaderEPC() {
//        if (myThread != null) {
//            myThread = null;
//        }
//        // 暂停巡检
//        if (LocalcacherConfig.isCloseTest) {
//            if (LocalcacherConfig.isUseNewDeviceCode) {
//                NewDeviceTools.stopReadMessage();
//            } else {
//                DeviceTools.getDeviceToolsInstance().pauseReaderEPC();
//            }
//        }
        inspection_bottom_start_rl.postDelayed(new Runnable() {
            @Override
            public void run() {
                inspection_bottom_text.setText("开始巡检");
                inspection_bottom_start_rl.setBackgroundColor(getResources().getColor(R.color.text_green));
            }
        }, 500);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (LocalcacherConfig.isCloseTest) {
//            if (LocalcacherConfig.isUseNewDeviceCode) {
//                NewDeviceTools.stopReadMessage();
//            } else {
//                DeviceTools.getDeviceToolsInstance().closeReader();
//            }
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (LocalcacherConfig.isCloseTest) {
//            if (LocalcacherConfig.isUseNewDeviceCode) {
//                NewDeviceTools.open();
//            } else {
//                DeviceTools.getDeviceToolsInstance().getReader();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHelper.close();

        if (myThread != null) {
            myThread = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DeviceTools.getDeviceToolsInstance(getApplicationContext()).longClickStop();
        if (LocalcacherConfig.isCloseTest) {
            if (LocalcacherConfig.isUseNewDeviceCode) {
                NewDeviceTools.destory();
            } else {
                DeviceTools.getDeviceToolsInstance(getApplicationContext()).closeReader();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        if (keyCode == 24) {
            if (LocalcacherConfig.isCloseTest) {
                if (IsPlayAudio) {
                    DeviceTools.getDeviceToolsInstance(getApplicationContext())
                            .playerAudio(getActivity());
                }
            }
            onClick(inspection_bottom_start_rl);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
