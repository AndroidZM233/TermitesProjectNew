package com.termites.tools.javabean;

import java.io.Serializable;

/**
 * Created by LF on 16/10/21.
 */

public class EquipmentBean implements Serializable {
    private String EquipmentId;
    private String EquipmentProjectId;
    private String EquipmentCheckTime;
    private String EquipmentUploadState;
    private String EquipmentLongitude;
    private String EquipmentLatitude;
    private String EquipmentLocation;

    public String getEquipmentId() {
        return EquipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        EquipmentId = equipmentId;
    }

    public String getEquipmentProjectId() {
        return EquipmentProjectId;
    }

    public void setEquipmentProjectId(String equipmentProjectId) {
        EquipmentProjectId = equipmentProjectId;
    }

    public void setEquipmentCheckTime(String equipmentCheckTime) {
        EquipmentCheckTime = equipmentCheckTime;
    }

    public String getEquipmentCheckTime() {
        return EquipmentCheckTime;
    }

    public void setEquipmentUploadState(String equipmentUploadState) {
        EquipmentUploadState = equipmentUploadState;
    }

    public String getEquipmentUploadState() {
        return EquipmentUploadState;
    }

    public String getEquipmentLongitude() {
        return EquipmentLongitude;
    }

    public void setEquipmentLongitude(String equipmentLongitude) {
        EquipmentLongitude = equipmentLongitude;
    }

    public String getEquipmentLatitude() {
        return EquipmentLatitude;
    }

    public void setEquipmentLatitude(String equipmentLatitude) {
        EquipmentLatitude = equipmentLatitude;
    }

    public String getEquipmentLocation() {
        return EquipmentLocation;
    }

    public void setEquipmentLocation(String equipmentLocation) {
        EquipmentLocation = equipmentLocation;
    }
}
