package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/26.
 */

public class EpcStatusBean implements Serializable{
    private String State;
    private String CurrentEpc;

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCurrentEpc() {
        return CurrentEpc;
    }

    public void setCurrentEpc(String currentEpc) {
        CurrentEpc = currentEpc;
    }
}
