package com.termites.ui;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.bean.Tag_Data;
import com.termites.R;
import com.termites.tools.Tools;
import com.termites.tools.rfid.DeviceTools;

import java.io.UnsupportedEncodingException;

/**
 * Created by 张明_ on 2017/11/6.
 * Email 741183142@qq.com
 */

public class CheckDialog extends Dialog implements View.OnClickListener {

    private EditText mTvId;
    private Button mBtnCheckStart;
    private Button mBtnCheckStop;
    private Button mBtnClose;
    private Context mContext;
    private SoundPool soundPool;
    private int soundId;
    private int soundId1;
    private IUHFService reader;


    public CheckDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check);
        initView();
        reader = DeviceTools.getDeviceToolsInstance(mContext).getReader();
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(mContext, R.raw.scankey, 0);
        soundId1 = soundPool.load(mContext, R.raw.beep, 0);
    }

    private void initView() {
        mTvId = (EditText) findViewById(R.id.tv_id);
        mTvId.setText("573EA0010001");
        mBtnCheckStart = (Button) findViewById(R.id.btn_check_start);
        mBtnCheckStart.setOnClickListener(this);
        mBtnCheckStop = (Button) findViewById(R.id.btn_check_stop);
        mBtnCheckStop.setOnClickListener(this);
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_start:
                final String toString = mTvId.getText().toString();
                if (TextUtils.isEmpty(toString)) {
                    Toast.makeText(mContext, "监测装置编号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                mBtnCheckStart.setVisibility(View.GONE);
                mBtnCheckStop.setVisibility(View.VISIBLE);

                reader.setListener(new IUHFService.Listener() {
                    @Override
                    public void update(Tag_Data var1) {
                        try {
                            synchronized (this) {
                                String epc = Tools.hexStringToString(var1.epc);
                                int rssi = Integer.parseInt(var1.rssi);
                                if (toString.equals(epc)) {
                                    if (rssi > -60) {
                                        soundPool.play(soundId, 1, 1, 0, 0, 2);
                                    } else {
                                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                                    }

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                reader.newInventoryStart();
                break;
            case R.id.btn_check_stop:
                mBtnCheckStart.setVisibility(View.VISIBLE);
                mBtnCheckStop.setVisibility(View.GONE);
                reader.newInventoryStop();
                break;
            case R.id.btn_close:
                reader.newInventoryStop();
                if (soundPool != null) {
                    soundPool.release();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

}
