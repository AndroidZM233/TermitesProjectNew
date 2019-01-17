package com.termites.ui;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.termites.R;
import com.termites.dao.Register;
import com.termites.tools.ShowInputMethodManager;
import com.termites.tools.SimpleEditTextTextWatcher;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.javabean.NetConnectionBean;
import com.termites.ui.base.BaseWithTitleBackActivity;

/**
 * Created by LF on 16/10/20.
 */

public class CheckInMyDeviceActivity extends BaseWithTitleBackActivity implements View.OnClickListener {
    // 登记账号
    private EditText checkin_device_account;
    private ImageView checkin_device_account_clear;
    // 登记密码
    private EditText checkin_device_pwd;
    private ImageView checkin_device_pwd_clear;
    private ImageView checkin_device_pwd_expressly;
    // 设备编号
    private EditText checkin_device_equipment_number;
    private ImageView checkin_device_equipment_number_clear;
    // 机器码
    private TextView checkin_device_robot_number;
    // 提交按钮
    private Button checkin_device_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_checkin_device);

        initView();
    }

    private void initView() {
        setTitleTxt("登记本机");

        // 登记账号
        checkin_device_account = $_Act(R.id.checkin_device_account);

        // 账号清空按钮
        checkin_device_account_clear = $_Act(R.id.checkin_device_account_clear);
        checkin_device_account_clear.setOnClickListener(this);

        SimpleEditTextTextWatcher mAccountWatcher = new SimpleEditTextTextWatcher(checkin_device_account, checkin_device_account_clear, false, true);
        checkin_device_account.addTextChangedListener(mAccountWatcher);

        // 登记密码
        checkin_device_pwd = $_Act(R.id.checkin_device_pwd);

        // 密码清空按钮
        checkin_device_pwd_clear = $_Act(R.id.checkin_device_pwd_clear);
        checkin_device_pwd_clear.setOnClickListener(this);

        SimpleEditTextTextWatcher mPwdWatcher = new SimpleEditTextTextWatcher(checkin_device_pwd, checkin_device_pwd_clear, false, true);
        checkin_device_pwd.addTextChangedListener(mPwdWatcher);

        // 密码是否明文按钮
        checkin_device_pwd_expressly = $_Act(R.id.checkin_device_pwd_expressly);
        checkin_device_pwd_expressly.setOnClickListener(this);

        // 设备编号
        checkin_device_equipment_number = $_Act(R.id.checkin_device_equipment_number);

        // 设备编号清空按钮
        checkin_device_equipment_number_clear = $_Act(R.id.checkin_device_equipment_number_clear);
        checkin_device_equipment_number_clear.setOnClickListener(this);

        SimpleEditTextTextWatcher mEquipmentWatcher = new SimpleEditTextTextWatcher(checkin_device_equipment_number, checkin_device_equipment_number_clear, false, true);
        checkin_device_equipment_number.addTextChangedListener(mEquipmentWatcher);

        // 机器码
        checkin_device_robot_number = $_Act(R.id.checkin_device_robot_number);

        // 提交按钮
        checkin_device_submit = $_Act(R.id.checkin_device_submit);
        checkin_device_submit.setOnClickListener(this);

        // 初始化数据
        initData();
    }

    private void initData() {
        // 设置机器码的值
        checkin_device_robot_number.setText(MethodConfig.getRobotCode(getActivity()));
    }

    public void submitData(final String name, final String pwd, final String device) {
        showProgress("正在提交数据,请稍后...");
        new Register(getActivity(), name, pwd, device, new Register.SuccessCallback() {

            @Override
            public void onSuccess(NetConnectionBean bean) {
                hideProgress();
                if (bean != null) {
                    if (bean.isError()) {
                        toast(bean.getMessage());
                    } else {
                        MethodConfig.saveFile(device.toString());
                        toast("登记成功", R.drawable.toast_icon_suc);
                        setResult(100, null);
                        finish();
                    }
                }
            }
        }, new Register.FailCallback() {

            @Override
            public void onFail() {
                hideProgress();
                toast("登记失败,请重试");
            }
        });
    }

    @Override
    public void onClickTitleBack(LinearLayout mLinearLayout) {
        ShowInputMethodManager.hideSoftInput(mLinearLayout);
        setResult(100, null);
        super.onClickTitleBack(mLinearLayout);
    }

    // 密码是否明文
    private boolean mPwdIsExpressly = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkin_device_account_clear:
                checkin_device_account.setText("");
                break;
            case R.id.checkin_device_pwd_clear:
                checkin_device_pwd.setText("");
                break;
            case R.id.checkin_device_equipment_number_clear:
                checkin_device_equipment_number.setText("");
                break;
            case R.id.checkin_device_pwd_expressly:
                if (mPwdIsExpressly) {
                    checkin_device_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdIsExpressly = false;
                    checkin_device_pwd_expressly.setImageResource(R.drawable.cipher_text);
                } else {
                    checkin_device_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdIsExpressly = true;
                    checkin_device_pwd_expressly.setImageResource(R.drawable.plain_text);
                }
                if (!TextUtils.isEmpty(checkin_device_pwd.getText().toString())) {
                    checkin_device_pwd.setSelection(checkin_device_pwd.getText().toString().length());
                }
                break;
            case R.id.checkin_device_submit:
                setRepeat(checkin_device_submit);
                if (!MethodConfig.isNetWorkAvailables(getActivity())) {
                    return;
                }

                String account = checkin_device_account.getText().toString();
                if (TextUtils.isEmpty(account)) {
                    toast("请输入登记帐号");
                    return;
                }

                String pwd = checkin_device_pwd.getText().toString();
                if (TextUtils.isEmpty(pwd)) {
                    toast("请输入登记密码");
                    return;
                }

                String equipmentNumber = checkin_device_equipment_number.getText().toString();
                if (TextUtils.isEmpty(equipmentNumber)) {
                    toast("请输入设备编号");
                    return;
                }
                submitData(account, pwd, equipmentNumber);
                break;
        }
    }

    // 主界面点击两次返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {// 是实体返回键并且已经点击
            setResult(100, null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
