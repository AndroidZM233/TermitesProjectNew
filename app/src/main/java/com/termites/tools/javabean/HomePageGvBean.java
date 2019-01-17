package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/20.
 */

public class HomePageGvBean implements Serializable{
    private int IconSource;
    private String Text;
    private String JumpType;

    public void setIconSource(int iconSource) {
        IconSource = iconSource;
    }

    public int getIconSource() {
        return IconSource;
    }

    public void setJumpType(String jumpType) {
        JumpType = jumpType;
    }

    public String getJumpType() {
        return JumpType;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getText() {
        return Text;
    }
}
