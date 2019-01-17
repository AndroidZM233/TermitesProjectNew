package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/20.
 */

public class InspectionBean implements Serializable {
    private int ShowId;
    private String InspecId;
    private String InspectionTermiteState;
    private String InspectionTime;
    private String InspectionUploadState;

    public String getInspecId() {
        return InspecId;
    }

    public void setInspecId(String inspecId) {
        InspecId = inspecId;
    }

    public String getInspectionTermiteState() {
        return InspectionTermiteState;
    }

    public void setInspectionTermiteState(String inspectionTermiteState) {
        InspectionTermiteState = inspectionTermiteState;
    }

    public String getInspectionTime() {
        return InspectionTime;
    }

    public void setInspectionTime(String inspectionTime) {
        InspectionTime = inspectionTime;
    }

    public String getInspectionUploadState() {
        return InspectionUploadState;
    }

    public void setInspectionUploadState(String inspectionUploadState) {
        InspectionUploadState = inspectionUploadState;
    }

    public int getShowId() {
        return ShowId;
    }

    public void setShowId(int showId) {
        ShowId = showId;
    }
}
