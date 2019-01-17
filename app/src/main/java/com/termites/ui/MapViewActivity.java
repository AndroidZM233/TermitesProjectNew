package com.termites.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.termites.R;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.InspectionBean;
import com.termites.ui.base.BaseWithTitleBackActivity;

import java.util.ArrayList;

/**
 * Created by LF on 16/10/23.
 */

public class MapViewActivity extends BaseWithTitleBackActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean mIsDataUpload = false;
    private boolean mIsHistoryData = false;
    private double[] mLongitudes;
    private double[] mLatitudes;
    private BitmapDescriptor[] mMapMarkIcon;
    private String[] mEquipmentId;
    private double mCurrentLatitude;
    private double mCurrentLongitude;
    private boolean isFirst = true;
    private ArrayList<MarkerBean> markerBeanArrayList = new ArrayList<>();
    // 定位
    private LocationClient mLocationClient;
    private BDLocationListener bdLocationListener = new MyLocationListener();
    private BitmapDescriptor mRedBitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.mapview_red_icon);
    private BitmapDescriptor mGreenBitmap = BitmapDescriptorFactory
            .fromResource(R.drawable.mapview_green_icon);
    private DataHelper dataHelper;
    private ImageView iv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.layout_mapview);

        Intent intent = getIntent();
        mIsHistoryData = intent.getBooleanExtra(LocalcacherConfig.KEY_IsHistoryData, false);
        mIsDataUpload = intent.getBooleanExtra(LocalcacherConfig.KEY_IsDataUpload, false);
        String longitude = intent.getStringExtra(LocalcacherConfig.KEY_Longitude);
        String latitude = intent.getStringExtra(LocalcacherConfig.KEY_Latitude);
        String equipmentId = intent.getStringExtra(LocalcacherConfig.KEY_EquipmentId);
        setTitleTxt("地图");
        iv_location = (ImageView) findViewById(R.id.iv_location);
        if (mIsHistoryData) {
            iv_location.setVisibility(View.GONE);
        } else {
            iv_location.setVisibility(View.VISIBLE);
        }

        dataHelper = new DataHelper(this);
        if (!mIsDataUpload) {
            setTitleRightTextVisility(View.VISIBLE);
            setTitleRightTxt("确定");
        }

        if (mIsDataUpload || mIsHistoryData) {
            if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
                if (longitude.contains(",") && latitude.contains(",") && equipmentId.contains(",")) {
                    String[] longitudes_str = longitude.split(",");
                    String[] latitudes_str = latitude.split(",");
                    mEquipmentId = equipmentId.split(",");
                    mLongitudes = new double[longitudes_str.length];
                    mLatitudes = new double[latitudes_str.length];
                    mMapMarkIcon = new BitmapDescriptor[mEquipmentId.length];
                    for (int i = 0; i < longitudes_str.length; i++) {
                        mLongitudes[i] = Double.parseDouble(longitudes_str[i]);
                    }
                    for (int i = 0; i < latitudes_str.length; i++) {
                        mLatitudes[i] = Double.parseDouble(latitudes_str[i]);
                    }
                    for (int i = 0; i < mEquipmentId.length; i++) {
                        InspectionBean bean = dataHelper.selectInspectionState(mEquipmentId[i]);
                        if (TextUtils.isEmpty(bean.getInspectionTermiteState())) {
                            mMapMarkIcon[i] = mGreenBitmap;
                        } else {
                            mMapMarkIcon[i] = bean.getInspectionTermiteState().equals("有") ? mRedBitmap : mGreenBitmap;
                        }
                    }
                } else {
                    mLongitudes = new double[1];
                    mLatitudes = new double[1];
                    mEquipmentId = new String[1];
                    mMapMarkIcon = new BitmapDescriptor[1];
                    mLongitudes[0] = Double.parseDouble(longitude);
                    mLatitudes[0] = Double.parseDouble(latitude);
                    mEquipmentId[0] = equipmentId;
                    InspectionBean bean = dataHelper.selectInspectionState(mEquipmentId[0]);
                    if (TextUtils.isEmpty(bean.getInspectionTermiteState())) {
                        mMapMarkIcon[0] = mGreenBitmap;
                    } else {
                        mMapMarkIcon[0] = bean.getInspectionTermiteState().equals("有") ? mRedBitmap : mGreenBitmap;
                    }
                }
            }
        }

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapview);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(20.0f);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(mapLisener);
        mBaiduMap.setOnMarkerDragListener(onMarkerDragListener);
        if (MethodConfig.isNetworkAvailable(this)) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        } else {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(bdLocationListener);
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setAddrType("all");
        option.setScanSpan(2000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        if (mIsDataUpload || mIsHistoryData) {
            for (int i = 0; i < mLatitudes.length; i++) {
                LatLng point = new LatLng(mLatitudes[i], mLongitudes[i]);

                // 构建markerOption，用于在地图上添加marker
                MarkerOptions options = new MarkerOptions()//
                        .position(point)// 设置marker的位置
                        .icon(mMapMarkIcon[i])// 设置marker的图标
                        .zIndex(9)// 設置marker的所在層級
                        .draggable(true);// 设置手势拖拽
                options.animateType(MarkerOptions.MarkerAnimateType.drop);
                // 在地图上添加marker，并显示
                Marker marker = (Marker) mBaiduMap.addOverlay(options);
                MarkerBean markerBean = new MarkerBean();
                markerBean.setMarker(marker);
                markerBean.setEquipmentId(mEquipmentId[i]);
                markerBeanArrayList.add(markerBean);
                if (i == 0) {
                    showLatLng(isFirst, point);
                }
            }
            mBaiduMap.setOnMarkerClickListener(new MyOnMarkerClickListener(markerBeanArrayList));
        }
    }

    class MyOnMarkerClickListener implements BaiduMap.OnMarkerClickListener {
        private ArrayList<MarkerBean> markerBeanArrayList;

        public MyOnMarkerClickListener(ArrayList<MarkerBean> markerBeanArrayList) {
            this.markerBeanArrayList = markerBeanArrayList;
        }

        @Override
        public boolean onMarkerClick(final Marker marker) {
            for (int i = 0; i < markerBeanArrayList.size(); i++) {
                MarkerBean markerBean = markerBeanArrayList.get(i);
                if (marker == markerBean.getMarker()) {
                    TextView textView = new TextView(getApplicationContext());
                    textView.setBackgroundResource(R.drawable.popup);
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(Color.BLACK);
                    textView.setPadding(20, 0, 20, 0);
                    textView.setHeight(70);
                    textView.setText("监测点编号: " + markerBean.getEquipmentId());
                    InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                            mBaiduMap.hideInfoWindow();
                        }
                    };
                    LatLng ll = marker.getPosition();
                    InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(textView), ll, -70, listener);
                    mBaiduMap.showInfoWindow(mInfoWindow);
                    break;
                }
            }
            return false;
        }

    }

    private class MarkerBean {
        private Marker marker;
        private String EquipmentId;

        public MarkerBean() {
        }

        public String getEquipmentId() {
            return EquipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            EquipmentId = equipmentId;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }

    //地图移动了获取中间值
    BaiduMap.OnMapStatusChangeListener mapLisener = new BaiduMap.OnMapStatusChangeListener() {

        @Override
        public void onMapStatusChangeStart(MapStatus mapstatus) {
//            tv_site.setText(getResources().getString(R.string.move_location));
//            tv_point_out.setVisibility(View.GONE);
        }

        @Override
        public void onMapStatusChange(MapStatus mapstatus) {
        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapstatus) {
            if (mIsDataUpload) {
                return;
            }
            //纬度
            mCurrentLatitude = mapstatus.target.latitude;
            //经度
            mCurrentLongitude = mapstatus.target.longitude;
            addMarkerOverlay(isFirst, mCurrentLatitude, mCurrentLongitude);

        }
    };

    BaiduMap.OnMarkerDragListener onMarkerDragListener = new BaiduMap.OnMarkerDragListener() {

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
        }

        @Override
        public void onMarkerDragStart(Marker marker) {
        }
    };


    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (mIsDataUpload || mIsHistoryData) {
                return;
            }
            if (isFirst) {
                // 改变定位模式
                addMarkerOverlay(isFirst, location.getLatitude(), location.getLongitude());
                isFirst = false;
            }
        }
    }

    /**
     * 添加标注覆盖物
     */
    private void addMarkerOverlay(boolean isFirst, double latitude, double longitude) {
        mBaiduMap.clear();
        // 定义marker坐标点
        LatLng point = new LatLng(latitude, longitude);
        // 当从历史页面过来修改过定位点坐标之后,重新把新的mark点添加进去
        if (mIsHistoryData) {
            // 构建markerOption，用于在地图上添加marker
            OverlayOptions options = new MarkerOptions()//
                    .position(point)// 设置marker的位置
                    .icon(mMapMarkIcon[0])// 设置marker的图标
                    .zIndex(9)// 設置marker的所在層級
                    .draggable(true);// 设置手势拖拽
            // 在地图上添加marker，并显示
            Marker marker = (Marker) mBaiduMap.addOverlay(options);
            MarkerBean markerBean = new MarkerBean();
            markerBean.setMarker(marker);
            markerBean.setEquipmentId(mEquipmentId[0]);
            markerBeanArrayList.clear();
            markerBeanArrayList.add(markerBean);
        }
        showLatLng(isFirst, point);
    }

    public void showLatLng(boolean isFirst, LatLng point) {
        float zoom = 20f;
        if (!isFirst) {
            zoom = mBaiduMap.getMapStatus().zoom;
        }
        //定义地图状态
        MapStatus mMapStatus;
        if (mIsHistoryData == false &&
                mIsDataUpload == false &&
                LocalcacherConfig.getCurrentLongitude() != 0 &&
                LocalcacherConfig.getCurrentLatitude() != 0 &&
                isFirst) {
            LatLng ll = new LatLng(LocalcacherConfig.getCurrentLatitude(), LocalcacherConfig.getCurrentLongitude());
            mMapStatus = new MapStatus.Builder()
                    .target(ll)
                    .zoom(zoom)
                    .build();
        } else {
            mMapStatus = new MapStatus.Builder()
                    .target(point)
                    .zoom(zoom)
                    .build();
        }

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void onFinish() {
        if (mIsHistoryData) {
            setResult(300, null);
        } else {
            setResult(200, null);
        }
        finish();
        overridePendingTransition(R.anim.out_from, R.anim.out_from_bottom);
    }

    @Override
    public void onClickTitleBack(LinearLayout mLinearLayout) {
        onFinish();
    }

    @Override
    public void onClickTitleRightTxt(TextView mTextView) {
        super.onClickTitleRightTxt(mTextView);
        Intent intent = new Intent();
        intent.putExtra(LocalcacherConfig.KEY_Longitude, mCurrentLongitude);
        intent.putExtra(LocalcacherConfig.KEY_Latitude, mCurrentLatitude);
        if (mIsHistoryData) {
            setResult(300, intent);
        } else {
            setResult(200, intent);
        }
        finish();
        if (mIsHistoryData == false && mIsDataUpload == false) {
            if(mCurrentLatitude >0 && mCurrentLongitude >0){
                LocalcacherConfig.cacheCurrentLatitude(mCurrentLatitude);
                LocalcacherConfig.cacheCurrentLongitude(mCurrentLongitude);
            }
        }

    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
        // 回收bitmip资源
        mRedBitmap.recycle();
        mGreenBitmap.recycle();
        mMapMarkIcon = null;
        dataHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {// 是实体返回键并且已经点击
            onFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
