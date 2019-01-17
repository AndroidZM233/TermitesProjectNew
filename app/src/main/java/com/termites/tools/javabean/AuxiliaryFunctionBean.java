package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/26.
 */

public class AuxiliaryFunctionBean implements Serializable {
    private int iconSource;
    private String appName;
    private int appType;

    public int getIconSource() {
        return iconSource;
    }

    public void setIconSource(int iconSource) {
        this.iconSource = iconSource;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }
}
