package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/21.
 */

public class NetConnectionBean implements Serializable {
    private boolean isError;
    private String message;

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
