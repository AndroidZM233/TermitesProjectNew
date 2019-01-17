package com.termites.ui;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.termites.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FlashLightActivity extends AppCompatActivity  {

    private ToggleButton mBtn;
    Camera camera=null;// = Camera.open();
    Camera.Parameters parameter;// = camera.getParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_light);
        initView();
    }

    private void initView() {
        mBtn = (ToggleButton) findViewById(R.id.btn);
        mBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    openLight();
                }else {
                    closeLight();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SystemProperties.get("persist.sys.iscamera").equals("close")) {
            SystemProperties.set("persist.sys.scanstopimme", "true");
            Intent opencam = new Intent();
            opencam.setAction("com.se4500.opencamera");
            this.sendBroadcast(opencam, null);
        }
        if (camera==null){
            try {
                camera = Camera.open();
                parameter = camera.getParameters();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("ceshi----");
            openLight();
        }else {
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            camera = Camera.open();
            parameter = camera.getParameters();
            System.out.println("ceshi----");
            openLight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean judgeSe4500() {
        File DeviceName = new File("proc/se4500");
        try {
            BufferedWriter CtrlFile = new BufferedWriter(new FileWriter(
                    DeviceName, false));
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return false;
        } // open
    }


    public void openLight() {
        // 打开闪光灯关键代码
        camera.startPreview();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameter);
        camera.startPreview();
//        Toast.makeText(this, "open", Toast.LENGTH_SHORT).show();
    }

    public void closeLight() {// 关闭闪关灯关键代码
        parameter = camera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameter);// 关闭闪关灯关键代码
        parameter = camera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameter);
//        Toast.makeText(this, "close", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
//        if (judgeSe4500()) {
//            Intent intent = new Intent();
//            intent.setAction("com.se4500.closecamera");
//            this.sendBroadcast(intent);
//        }
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (SystemProperties.get("persist.sys.iscamera").equals("open")) {
            SystemProperties.set("persist.sys.scanstopimme", "false");
            Intent opencam = new Intent();
            opencam.setAction("com.se4500.closecamera");
            this.sendBroadcast(opencam, null);
        }
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (SystemProperties.get("persist.sys.iscamera").equals("open")) {
            SystemProperties.set("persist.sys.scanstopimme", "false");
            Intent opencam = new Intent();
            opencam.setAction("com.se4500.closecamera");
            this.sendBroadcast(opencam, null);
        }
    }
}
