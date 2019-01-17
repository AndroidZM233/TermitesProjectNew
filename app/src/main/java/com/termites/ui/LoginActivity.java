package com.termites.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.termites.R;
import com.termites.dao.DataSync;
import com.termites.dao.Login;
import com.termites.tools.ActivityManagerDone;
import com.termites.tools.SecuritUtil;
import com.termites.tools.ShowInputMethodManager;
import com.termites.tools.SimpleEditTextTextWatcher;
import com.termites.tools.config.LocalcacherConfig;
import com.termites.tools.config.MethodConfig;
import com.termites.tools.database.DataHelper;
import com.termites.tools.javabean.CustomBean;
import com.termites.tools.javabean.NetConnectionBean;
import com.termites.ui.base.BaseWithTitleBackActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LF on 16/10/20.
 */

public class LoginActivity extends BaseWithTitleBackActivity implements View.OnClickListener {
    // 设备编号
    private TextView login_equipment_number;
    // 账号
    private EditText login_account;
    private ImageView login_account_clear;
    //密码
    private EditText login_pwd;
    private ImageView login_pwd_clear;
    private ImageView login_pwd_expressly;
    // 登录按钮
    private Button login_submit;
    // 登记本机按钮
    private Button login_register_device;
    // 数据同步按钮
    private RelativeLayout login_register_sycndata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        initView();
    }

    private void initView() {

        setTitleTxt("用户登录");
        setTitleBackTextVisility(View.GONE);

        // 设备编号
        login_equipment_number = $_Act(R.id.login_equipment_number);

        // 账号
        login_account = $_Act(R.id.login_account);
        // 账号清空按钮
        login_account_clear = $_Act(R.id.login_account_clear);
        login_account_clear.setOnClickListener(this);

        SimpleEditTextTextWatcher mAccountWatcher = new SimpleEditTextTextWatcher(login_account, login_account_clear, false, true);
        login_account.addTextChangedListener(mAccountWatcher);

        // 密码
        login_pwd = $_Act(R.id.login_pwd);
        // 密码清空按钮
        login_pwd_clear = $_Act(R.id.login_pwd_clear);
        login_pwd_clear.setOnClickListener(this);
        // 密码是否明文按钮
        login_pwd_expressly = $_Act(R.id.login_pwd_expressly);
        login_pwd_expressly.setOnClickListener(this);

        SimpleEditTextTextWatcher mPwdWatcher = new SimpleEditTextTextWatcher(login_pwd, login_pwd_clear, false, true);
        login_pwd.addTextChangedListener(mPwdWatcher);

        // 登录按钮
        login_submit = $_Act(R.id.login_submit);
        login_submit.setOnClickListener(this);

        // 登记本机按钮
        login_register_device = $_Act(R.id.login_register_device);
        login_register_device.setOnClickListener(this);

        // 数据同步按钮
        login_register_sycndata = $_Act(R.id.login_register_sycndata);
        login_register_sycndata.setOnClickListener(this);
        // 初始化数据
        initData();
    }


    private void initData() {
        if (LocalcacherConfig.isCloseTest) {
            // 获取缓存中的设备编号,如果没有显示提示信息
            String deviceNumber = MethodConfig.loadFile();
            login_equipment_number.setText(TextUtils.isEmpty(deviceNumber) ? "提示: 请先登记本机" : deviceNumber);
        } else {
            login_equipment_number.setText("13578");
        }

        // 获取缓存中的账号
        login_account.setText(TextUtils.isEmpty(LocalcacherConfig.getUserName()) ? "" : LocalcacherConfig.getUserName());
        login_account.setSelection(login_account.getText().toString().length());
        // 获取缓存中的密码
        login_pwd.setText(TextUtils.isEmpty(LocalcacherConfig.getUserPwd()) ? "" : LocalcacherConfig.getUserPwd());
        login_pwd.setSelection(login_pwd.getText().toString().length());
    }

    // 密码是否明文
    private boolean mPwdIsExpressly = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_account_clear:
                login_account.setText("");
                break;
            case R.id.login_pwd_clear:
                login_pwd.setText("");
                break;
            case R.id.login_pwd_expressly:
                if (mPwdIsExpressly) {
                    login_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPwdIsExpressly = false;
                    login_pwd_expressly.setImageResource(R.drawable.cipher_text);
                } else {
                    login_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPwdIsExpressly = true;
                    login_pwd_expressly.setImageResource(R.drawable.plain_text);
                }
                if (!TextUtils.isEmpty(login_pwd.getText().toString())) {
                    login_pwd.setSelection(login_pwd.getText().toString().length());
                }
                break;
            case R.id.login_submit:
                setRepeat(login_submit);
                if (LocalcacherConfig.isCloseTest) {
                    ShowInputMethodManager.hideSoftInput(login_submit);
                    String equipment_number = login_equipment_number.getText().toString();
                    String account = login_account.getText().toString();
                    String pwd = login_pwd.getText().toString();
                    if (equipment_number.contains("提示")) {
                        toast("请先登记本机");
                        return;
                    }
                    if (TextUtils.isEmpty(account)) {
                        toast("请输入账号");
                        return;
                    }
                    if (TextUtils.isEmpty(pwd)) {
                        toast("请输入密码");
                        return;
                    }
                    if (TextUtils.isEmpty(LocalcacherConfig.getCustomAreaCode()) || LocalcacherConfig.getCustomId() == 0) {
                        toast("请先同步数据");
                        return;
                    }

                    if (MethodConfig.isNetworkAvailable(getActivity())) {
                        submitLoginData(equipment_number, account, pwd);
                    } else {
                        CustomBean customBean = MethodConfig.getJsonSycnData(LocalcacherConfig.getSyncData());
                        if (customBean != null) {
                            List<CustomBean.Admins> adminsList = customBean.getAdmins();
                            if (adminsList.size() > 0) {
                                for (int i = 0; i < adminsList.size(); i++) {
                                    CustomBean.Admins admins = adminsList.get(i);
                                    if (account.equals(admins.getName()) &&
                                            SecuritUtil.md5(pwd).equals(admins.getPassword())) {
                                        toast("登录成功", R.drawable.toast_icon_suc);
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        finish();
                                        return;
                                    }
                                }
                                toast("登录失败,请检查账号或密码是否正确");
                            }
                        }
                    }
                } else {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    finish();
                }
                break;
            case R.id.login_register_device:
                setRepeat(login_register_device);
                ShowInputMethodManager.hideSoftInput(login_register_device);
                startActivityForResult(new Intent(getActivity(), CheckInMyDeviceActivity.class), 100);
                break;
            case R.id.login_register_sycndata:
                setRepeat(login_register_sycndata);
                ShowInputMethodManager.hideSoftInput(login_register_sycndata);
                if (login_equipment_number.getText().toString().contains("提示")) {
                    toast("请先登记本机");
                    return;
                }
                // 数据同步
                SycnData();
                break;
        }
    }

    // 登录接口
    public void submitLoginData(String device, final String name, final String pwd) {
        showProgress("正在登录,请稍后...");
        new Login(getActivity(), device, name, pwd, new Login.SuccessCallback() {
            @Override
            public void onSuccess(NetConnectionBean bean) {
                hideProgress();
                if (bean.isError()) {
                    toast(bean.getMessage());
                } else {
                    toast("登录成功", R.drawable.toast_icon_suc);
                    // 缓存登录成功后的账号和密码
                    LocalcacherConfig.cacheUserName(name);
                    LocalcacherConfig.cacheUserPwd(pwd);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    finish();
                }
            }
        }, new Login.FailCallback() {
            @Override
            public void onFail() {
                hideProgress();
                toast("登录失败,请稍后再试");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            initData();
        }
    }

    public void SycnData() {
        showProgress("正在同步数据,请稍后...");
        String device = login_equipment_number.getText().toString();
        new DataSync(getActivity(), device, new DataSync.SuccessCallback() {

            @Override
            public void onSuccess() {
                hideProgress();
                toast("数据同步成功", R.drawable.toast_icon_suc);
            }
        }, new DataSync.FailCallback() {

            @Override
            public void onFail() {
                toast("数据同步失败");
                hideProgress();
            }
        });
    }

    private long exitTime;

    // 主界面点击两次返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {// 是实体返回键并且已经点击
            if ((System.currentTimeMillis() - exitTime) > 2000) {// 间隔时间
                // 自定义Toast
                toast("再按一次退出程序", 0);
                exitTime = System.currentTimeMillis();
            } else {
                ActivityManagerDone.finishAllActivities();
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}