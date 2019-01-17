package com.termites.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.termites.R;
import com.termites.tools.PopWindowShowTwoButton;
import com.termites.tools.ShowInputMethodManager;
import com.termites.tools.ShowToast;
import com.termites.tools.SimpleEditTextTextWatcher;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.EpcStatusBean;
import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.InspectionBean;
import com.termites.tools.rfid.DeviceTools;
import com.termites.tools.rfid.NewDeviceTools;
import com.termites.ui.base.BaseWithTitleBackActivity;

import java.util.ArrayList;

/**
 * Created by LF on 16/10/20.
 */

public class EquipmentCheckInActivity extends BaseWithTitleBackActivity implements View.OnClickListener {
    //  地区编号
    private TextView equipment_area_num;
    // 设备编号
    private EditText equipment_device_num;
    private ImageView equipment_device_num_clear;
    // 项目编号
    private EditText equipment_project_num;
    private ImageView equipment_project_num_clear;
    // 经度
    private TextView equipment_longitude;
    // 纬度
    private TextView equipment_latitude;
    // 重新定位
    private Button equipment_reset_location;
    // 数据登记
    private Button equipment_chenckin;
    // 数据校验
    private Button equipment_check;
    //检验数据显示
    private TextView tv_jianyan;
    // 下载离线地图包
    private Button equipment_data_downloadmap;
    // 数据库工具类
    private DataHelper dataHelper;
    // 定位
    private LocationClient mLocationClient;
    private BDLocationListener bdLocationListener = new MyLocationListener();
    // 离线地图下载类
    private MKOfflineMap mOffline = null;
    private String mAddressStr = "";
    // 已下载的离线地图集合
    private ArrayList<MKOLUpdateElement> localMapList = null;
    // 反搜索
    private GeoCoder mSearch = null;
    // 当前定位的城市ID
    private int mCityID = 1;
    // 是否更改装置点编号的弹框
    private PopWindowShowTwoButton showTwoButton;
    // 当前经纬度
    private double mLongitude = 0.0d, mLatitude = 0.0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_equipment_checkin);
        if (LocalcacherConfig.isCloseTest) {
            if (LocalcacherConfig.isUseNewDeviceCode) {
                NewDeviceTools.initNative();
            } else {
                DeviceTools.getDeviceToolsInstance(getApplicationContext()).openReader();
            }
        }
        initView();
        dataHelper = new DataHelper(getActivity());
    }

    private void initView() {
        setTitleTxt("装置登记");
        setTitleRightTxt("查询");
        setTitleRightTextVisility(View.VISIBLE);

        equipment_area_num = $_Act(R.id.equipment_area_num);
        equipment_area_num.setText(TextUtils.isEmpty(LocalcacherConfig.getCustomAreaCode()) ?
                "未同步到地区编号" : LocalcacherConfig.getCustomAreaCode());

        equipment_device_num = $_Act(R.id.equipment_device_num);
        equipment_device_num_clear = $_Act(R.id.equipment_device_num_clear);
        equipment_device_num_clear.setOnClickListener(this);
        SimpleEditTextTextWatcher mDeviceWatcher = new SimpleEditTextTextWatcher(equipment_device_num, equipment_device_num_clear, false, true);
        equipment_device_num.addTextChangedListener(mDeviceWatcher);

        equipment_project_num = $_Act(R.id.equipment_project_num);
        equipment_project_num_clear = $_Act(R.id.equipment_project_num_clear);
        equipment_project_num_clear.setOnClickListener(this);
        SimpleEditTextTextWatcher mProjextWatcher = new SimpleEditTextTextWatcher(equipment_project_num, equipment_project_num_clear, false, true);
        equipment_project_num.addTextChangedListener(mProjextWatcher);

        equipment_longitude = $_Act(R.id.equipment_longitude);
        equipment_longitude.setOnClickListener(this);

        equipment_latitude = $_Act(R.id.equipment_latitude);
        equipment_latitude.setOnClickListener(this);

        equipment_reset_location = $_Act(R.id.equipment_reset_location);
        equipment_reset_location.setOnClickListener(this);

        equipment_chenckin = $_Act(R.id.equipment_chenckin);
        equipment_chenckin.setOnClickListener(this);

        equipment_check = $_Act(R.id.equipment_check);
        equipment_check.setOnClickListener(this);

        equipment_data_downloadmap = $_Act(R.id.equipment_data_downloadmap);
        equipment_data_downloadmap.setOnClickListener(this);

        tv_jianyan = $_Act(R.id.tv_jianyan);

        // 初始化定位
        initLocation();

        // 初始化离线地图
        iniyOfflineMap();

        // 初始化搜索
        initSearch();
    }

    private void initSearch() {
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
    }

    private void iniyOfflineMap() {
        mOffline = new MKOfflineMap();
        mOffline.init(mkOfflineMapListener);
        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }
    }

    private void initLocation() {
        /**
         * LocationMode.Hight_Accuracy 高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
         * LocationMode.Battery_Saving 低功耗定位模式：这种定位模式下，不会使用GPS进行定位，只会使用网络定位（WiFi定位和基站定位）；
         * LocationMode.Device_Sensors 仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
         */
        // 定位初始化
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(bdLocationListener);
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * getLocType
     * 61 ： GPS定位结果，GPS定位成功。
     * 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位。
     * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
     * 65 ： 定位缓存的结果。
     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
     * 161： 网络定位结果，网络定位成功。
     * 162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
     * 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
     * 502： AK参数错误，请按照说明文档重新申请AK。
     * 505：AK不存在或者非法，请按照说明文档重新申请AK。
     * 601： AK服务被开发者自己禁用，请按照说明文档重新申请AK。
     * 602： key mcode不匹配，您的AK配置过程中安全码设置有问题，请确保：SHA1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请AK。
     * 501～700：AK验证失败，请按照说明文档重新申请AK。
     */
    class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // 设置经度和纬度
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            equipment_longitude.setText(location.getLongitude() + "");
            equipment_latitude.setText(location.getLatitude() + "");

            LatLng ptCenter = new LatLng(location.getLatitude(), location.getLongitude());
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));

        }
    }

    public int searchCity(String cityNameView) {
        ArrayList<MKOLSearchRecord> records;
        // 假设定位到的地址是  浙江省杭州市
        int cityID;
        String name;
        if (cityNameView != null && cityNameView.length() > 3) {
            name = cityNameView.substring(0, 2);
            Log.w("OfflineDemo", "下载离线包的是" + name);
            records = mOffline.searchCity(name);
            if (records == null || records.size() != 1) {
                cityID = 1;
            } else {
                cityID = records.get(0).cityID;
            }
        } else {
            cityID = 1;
        }
        return cityID;
    }

    public void updateOfflineMap() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<>();
        }
    }

    private PopWindowShowTwoButton alertDialogShowTwoButton = null;

    MKOfflineMapListener mkOfflineMapListener = new MKOfflineMapListener() {

        @Override
        public void onGetOfflineMapState(int type, int state) {
            switch (type) {
                case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                    for (int i = 0; i < localMapList.size(); i++) {
                        if (mCityID == localMapList.get(i).cityID) {
                            return;
                        }
                    }
                    MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                    // 处理下载进度更新提示
                    if (update != null) {
                        showProgress("离线地图包下载中,请稍等..." + update.ratio + "%");
                        if (update.ratio - 100 >= 0) {
                            hideProgress();
                            toast("离线地图下载完成", R.drawable.toast_icon_suc);
                            updateOfflineMap();
                            mOffline.pause(mCityID);
                        }
                    }
                }
                break;
                case MKOfflineMap.TYPE_NEW_OFFLINE:
                    // 有新离线地图安装
                    Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                    break;
                case MKOfflineMap.TYPE_VER_UPDATE:
                    if (alertDialogShowTwoButton == null) {
                        alertDialogShowTwoButton = new PopWindowShowTwoButton(getActivity());
                        alertDialogShowTwoButton.setBtnCancelText("取消");
                        alertDialogShowTwoButton.setBtnSureText("确定");
                        alertDialogShowTwoButton.setOnClickListener(EquipmentCheckInActivity.this);
                    }
                    alertDialogShowTwoButton.show("离线地图包版本更新了,是否下载新的离线地图包?", state, equipment_data_downloadmap, Gravity.CENTER);
                    break;
            }
        }
    };

    OnGetGeoCoderResultListener onGetGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            mAddressStr = reverseGeoCodeResult.getAddress();
            if (TextUtils.isEmpty(mAddressStr)) {
                toast("定位失败,请检查网络或GPS是否已打开", 0);
                return;
            }
            if (!mAddressStr.equals(reverseGeoCodeResult.getAddress())) {
                toast("当前位置: " + mAddressStr, 0);
            }
        }
    };

    @Override
    public void onClickTitleBack(LinearLayout mLinearLayout) {
        ShowInputMethodManager.hideSoftInput(equipment_chenckin);
        super.onClickTitleBack(mLinearLayout);
    }

    @Override
    public void onClickTitleRightTxt(TextView mTextView) {
        super.onClickTitleRightTxt(mTextView);
        Intent intent = new Intent(getActivity(), EquipmentOrInspectionHistoryRecordActivity.class);
        intent.putExtra(LocalcacherConfig.KEY_HistoryRecordType, "checkin");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.equipment_project_num_clear:
                equipment_project_num.setText("");
                break;
            case R.id.equipment_device_num_clear:
                equipment_device_num.setText("");
                break;
            case R.id.equipment_reset_location:
            case R.id.equipment_longitude:
            case R.id.equipment_latitude:
                Intent intent = new Intent(getActivity(), MapViewActivity.class);
                startActivityForResult(intent, 200);
                overridePendingTransition(R.anim.out_from_top, R.anim.out_from);
                // 进入到地图页面之后就停止定位
                if (mLocationClient != null) {
                    mLocationClient.stop();
                }
                break;
            case R.id.equipment_chenckin:
                String areanum = equipment_area_num.getText().toString();
                String projectnum = equipment_project_num.getText().toString();
                String devicenum = equipment_device_num.getText().toString();

                if (areanum.contains("未同步到地区编号")) {
                    toast("请在登录界面进行数据同步");
                    return;
                }
                if (TextUtils.isEmpty(projectnum)) {
                    toast("请输入项目编号");
                    return;
                }
                if (!isNums(projectnum)) {
                    toast("项目编号格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(devicenum)) {
                    toast("请输入监测点编号");
                    return;
                }
                if (!isNums(projectnum)) {
                    toast("监测点编号格式不正确");
                    return;
                }
                if (mLongitude == 0.0d) {
                    toast("请打开地图进行定位");
                    return;
                }
                if (mLatitude == 0.0d) {
                    toast("请打开地图进行定位");
                    return;
                }
                //判断位数是否有效不足补0
                if (projectnum.length() > 4) {
                    toast("项目编号不能超过4位数字");
                    return;
                }
                if (devicenum.length() > 3) {
                    toast("监测点编号不能超过3位数字");
                    return;
                }
                if (projectnum.length() < 4) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int add = 4 - projectnum.length();
                    for (int i = 0; i < add; i++) {
                        stringBuilder.append("0");
                    }
                    projectnum = stringBuilder + projectnum;
                    equipment_project_num.setText(projectnum);
                }
                if (devicenum.length() < 3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    int add = 3 - devicenum.length();
                    for (int i = 0; i < add; i++) {
                        stringBuilder.append("0");
                    }
                    devicenum = stringBuilder + devicenum;
                    equipment_device_num.setText(devicenum);
                }

                if (LocalcacherConfig.isCloseTest) {
                    if (LocalcacherConfig.isUseNewDeviceCode) {
                        showProgress("正在检测数据,请稍后...");
                        NewDeviceTools.readMessage(mHandler);
                    } else {
                        // 先读取监测点装置的编号
                        String readEpc = DeviceTools.getDeviceToolsInstance(getApplicationContext())
                                .readEPC();
                        Log.w(DeviceTools.LogTag, "readEpc: " + readEpc);
                        judageCurrentEpc(readEpc);
                    }
                } else {
                    insertEquipmentDataBase(areanum, devicenum, projectnum, mLongitude + "", mLatitude + "", mAddressStr);
                }
                break;
            case R.id.equipment_check:
                if (LocalcacherConfig.isCloseTest) {
//                    String readEpc = DeviceTools.getDeviceToolsInstance().readEPC();
//                    DeviceTools.getDeviceToolsInstance(getApplicationContext())
//                            .readEPCStatus(myHandler);
                    areanum = equipment_area_num.getText().toString();
                    devicenum = equipment_device_num.getText().toString();
                    projectnum = equipment_project_num.getText().toString();
                    // 对监测点编号进行拼接
                    String inputEpc = areanum + projectnum + devicenum;
                    DeviceTools.getDeviceToolsInstance(getApplicationContext())
                            .JiaoYanStart(myHandler, inputEpc);

                } else {
                    showErrorDialog("当前监测点编号: " + "测试111111111" +
                            "\n白蚁状态: " + "无", equipment_check);
                }
                break;
            case R.id.equipment_data_downloadmap:
                if (!MethodConfig.isNetWorkAvailables(getActivity())) {
                    return;
                }
                mCityID = searchCity(mAddressStr);
                if (TextUtils.isEmpty(mAddressStr)) {
                    toast("当前定位失败,无法下载离线地图", 0);
                    return;
                }
                if (localMapList.size() > 0) {
                    for (int i = 0; i < localMapList.size(); i++) {
                        if (mCityID == localMapList.get(i).cityID) {
                            toast("当前省份的离线地图包已经下载过了", 0);
                            return;
                        }
                    }
                }
                // 根据当前获取到的城市去下载对应的离线地图包,如果没有对应的城市离线地图,则下载全国基础包
                mOffline.start(mCityID);
                break;
            case R.id.forgethandpwd_sure:
                alertDialogShowTwoButton.dismissPop();
                mOffline.update(alertDialogShowTwoButton.getPageIndex());
                break;
            case R.id.forgethandpwd_cancel:
                alertDialogShowTwoButton.dismissPop();
                break;
        }
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0x1) {
                EpcStatusBean epcStatusBean = (EpcStatusBean) msg.obj;
                if (epcStatusBean != null) {
                    showErrorDialog("当前监测点编号: " + epcStatusBean.getCurrentEpc() +
                            "\n白蚁状态: " + epcStatusBean.getState(), equipment_check);
                } else {
                    toast("校验失败,请重试!");
                }
            } else if (msg.arg1 == 0x2) {
                ShowToast.getInstance().show("读取EPC的状态失败,请重新!", 0);
            }
            DeviceTools.getDeviceToolsInstance(getApplicationContext()).pauseReaderEPC();
        }
    };


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            hideProgress();
            Log.w(NewDeviceTools.LogTag, "readEpc: " + msg.obj.toString());
            judageCurrentEpc(msg.obj.toString());
        }
    };

    public void judageCurrentEpc(String readEpc) {
        final String areanum = equipment_area_num.getText().toString();
        final String devicenum = equipment_device_num.getText().toString();
        final String projectnum = equipment_project_num.getText().toString();
        // 对监测点编号进行拼接
        String inputEpc = areanum + projectnum + devicenum;
        if (!TextUtils.isEmpty(readEpc)) {
            if (inputEpc.equals(readEpc)) {
                showErrorDialog(inputEpc + "与装置中的监测点编号相同,请重新输入", equipment_chenckin);
            } else {
                // 如果不相等,提示用户是否重新写入新的监测点编号
                if (showTwoButton == null) {
                    showTwoButton = new PopWindowShowTwoButton(getActivity());
                    showTwoButton.setBtnCancelText("取消");
                    showTwoButton.setBtnSureText("确定");
                    showTwoButton.setOnClickListener(onClickListener_showTwoButton);
                }
                showTwoButton.show("当前装置中的监测点编号为: " + readEpc + "\n" + "是否需要重新登记监测点编号?", equipment_chenckin, Gravity.CENTER);
            }
        } else {
            showProgress("正在登记装置数据,请稍后...");
            // 向监测点装置写入监测点的编号
            // 如果写入成功则保存本地数据库,写入失败则不保存
            final String writeEPC = "";
            int writeEPCState = -1;
            if (LocalcacherConfig.isUseNewDeviceCode) {
                NewDeviceTools.wirteMessage(inputEpc);
            } else {
                writeEPCState = DeviceTools.getDeviceToolsInstance(getApplicationContext()).writeEPC(inputEpc);
            }
            final int finalWriteEPCState = writeEPCState;
            equipment_chenckin.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    if (LocalcacherConfig.isUseNewDeviceCode) {
                        if (writeEPC != null) {
                            Log.w(DeviceTools.LogTag, writeEPC);
                            // 直接写入本地数据库
                            insertEquipmentDataBase(areanum, devicenum, projectnum, mLongitude + "", mLatitude + "", mAddressStr);
                        } else {
                            showErrorDialog("监测点编号登记失败", equipment_chenckin);
                        }
                    } else {
                        // 直接写入本地数据库
                        if (finalWriteEPCState == 0) {
                            insertEquipmentDataBase(areanum, devicenum, projectnum,
                                    mLongitude + "", mLatitude + "", mAddressStr);
                        } else {
                            ShowToast.getInstance().show("写入失败，请离标签近点再次尝试写入", 0);
                        }

                    }
                }
            }, 1000);
        }
    }

    View.OnClickListener onClickListener_showTwoButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String areanum = equipment_area_num.getText().toString();
            final String devicenum = equipment_device_num.getText().toString();
            final String projectnum = equipment_project_num.getText().toString();
            final String longitude = equipment_longitude.getText().toString();
            final String latitude = equipment_latitude.getText().toString();
            final String inputEpc = areanum + projectnum + devicenum;
            switch (v.getId()) {
                case R.id.forgethandpwd_sure:
                    showTwoButton.dismissPop();
                    showProgress("正在登记装置数据,请稍后...");
                    // 向监测点装置写入监测点的编号
                    // 如果写入成功则保存本地数据库,写入失败则不保存
                    final String writeEPC = "";
                    int writeEPCState = -1;
                    if (LocalcacherConfig.isUseNewDeviceCode) {
                        NewDeviceTools.wirteMessage(inputEpc);
                    } else {
                        writeEPCState = DeviceTools.getDeviceToolsInstance(getApplicationContext())
                                .writeEPC(inputEpc);
                    }
                    final int finalWriteEPCState = writeEPCState;
                    equipment_chenckin.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                            if (LocalcacherConfig.isUseNewDeviceCode) {
                                if (writeEPC != null) {
                                    Log.w(DeviceTools.LogTag, writeEPC);
                                    // 直接写入本地数据库
                                    insertEquipmentDataBase(areanum, devicenum, projectnum, longitude, latitude, mAddressStr);
                                } else {
                                    showErrorDialog("监测点编号登记失败", equipment_chenckin);
                                }
                            } else {
                                // 直接写入本地数据库
                                if (finalWriteEPCState == 0) {
                                    insertEquipmentDataBase(areanum, devicenum, projectnum, longitude,
                                            latitude, mAddressStr);
                                } else {
                                    ShowToast.getInstance().show("写入失败，请离标签近点再次尝试写入", 0);
                                }

                            }
                        }
                    }, 1000);

                    break;
                case R.id.forgethandpwd_cancel:
                    showTwoButton.dismissPop();
                    break;
            }
        }
    };


    // 保持数据到本地数据库
    public void insertEquipmentDataBase(String areanum, final String device, String project, String longitude, String latitude, String location) {
        EquipmentBean bean = new EquipmentBean();
        String datetime = MethodConfig.getSystemTime();
        bean.setEquipmentCheckTime(datetime);
        bean.setEquipmentUploadState("未上传");
        bean.setEquipmentProjectId(project);
        bean.setEquipmentId(areanum + project + device);
        bean.setEquipmentLatitude(latitude);
        bean.setEquipmentLongitude(longitude);
        bean.setEquipmentLocation(location);
        dataHelper.insertEquipmentData(bean);
        equipment_chenckin.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast("数据登记成功", R.drawable.toast_icon_suc);
                // 对监测点编号自动加1
                int len = device.length();
                if (isNums(device)) {
                    int value = Integer.parseInt(device) + 1;
                    String zeroCount = "";
                    for (int i = 0; i < len - String.valueOf(value).length(); i++) {
                        zeroCount += "0";
                    }
                    equipment_device_num.setText(zeroCount + value);
                    equipment_device_num.setSelection(equipment_device_num.getText().length());
                }
            }
        }, 1000);
    }

    public boolean isNums(String str) {
        return str.matches("[0-9]+");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            if (data != null) {
                double longitude = data.getDoubleExtra(LocalcacherConfig.KEY_Longitude, 0.0d);
                double latitude = data.getDoubleExtra(LocalcacherConfig.KEY_Latitude, 0.0d);
                if (longitude == 0.0d || latitude == 0.0d) {
                    if (mLocationClient != null) {
                        mLocationClient.start();
                    }
                    return;
                }
                mLongitude = longitude;
                mLatitude = latitude;
                LatLng ptCenter = new LatLng(latitude, longitude);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(ptCenter));
                equipment_longitude.setText(longitude + "");
                equipment_latitude.setText(latitude + "");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataHelper.close();

        if (mOffline != null) {
            mOffline.destroy();
        }

        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient = null;
        }

        if (LocalcacherConfig.isCloseTest) {
            if (LocalcacherConfig.isUseNewDeviceCode) {
                NewDeviceTools.destory();
            } else {
                DeviceTools.getDeviceToolsInstance(getApplicationContext()).closeReader();
            }
        }

    }

}
