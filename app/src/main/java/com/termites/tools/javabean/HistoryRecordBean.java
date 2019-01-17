package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/21.
 */

public class HistoryRecordBean implements Serializable {
    private String Type;
    private String EquipmentId;
    private String EquipmentProjectId;
    private String EquipmentCheckTime;
    private String EquipmentLongitude;
    private String EquipmentLatitude;
    private String InspecId;
    private String InspectionTermiteState;
    private String InspectionTime;
    private String UploadState;
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setEquipmentCheckTime(String equipmentCheckTime) {
        EquipmentCheckTime = equipmentCheckTime;
    }

    public void setEquipmentProjectId(String equipmentProjectId) {
        EquipmentProjectId = equipmentProjectId;
    }

    public void setEquipmentId(String equipmentId) {
        EquipmentId = equipmentId;
    }

    public void setInspecId(String inspecId) {
        InspecId = inspecId;
    }

    public void setInspectionTermiteState(String inspectionTermiteState) {
        InspectionTermiteState = inspectionTermiteState;
    }

    public void setInspectionTime(String inspectionTime) {
        InspectionTime = inspectionTime;
    }

    public String getEquipmentCheckTime() {
        return EquipmentCheckTime;
    }

    public String getEquipmentProjectId() {
        return EquipmentProjectId;
    }

    public String getEquipmentId() {
        return EquipmentId;
    }

    public String getInspecId() {
        return InspecId;
    }

    public String getInspectionTermiteState() {
        return InspectionTermiteState;
    }

    public String getInspectionTime() {
        return InspectionTime;
    }

    public String getUploadState() {
        return UploadState;
    }

    public void setUploadState(String uploadState) {
        UploadState = uploadState;
    }

    public String getEquipmentLatitude() {
        return EquipmentLatitude;
    }

    public String getEquipmentLongitude() {
        return EquipmentLongitude;
    }

    public void setEquipmentLatitude(String equipmentLatitude) {
        EquipmentLatitude = equipmentLatitude;
    }

    public void setEquipmentLongitude(String equipmentLongitude) {
        EquipmentLongitude = equipmentLongitude;
    }
}
