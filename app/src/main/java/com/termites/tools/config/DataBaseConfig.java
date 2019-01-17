package com.termites.tools.config;

/**
 * Created by LF on 16/10/21.
 */

public class DataBaseConfig {
    public static final String DB_NAME = "termite.db";
    public static final int DB_VERSION = 1;
    public static final String INSPECTIONTABLE = "inspectiontable";
    public static final String EQUIPMENTTABLE = "equipmenttable";


    public static final class InspectionTable {
        public static final String ID = "_id";
        public static final String INSPECTIONID = "inspectionid";
        public static final String INSPECTIONTERMITESSTATE = "termitesstate";
        public static final String INSPECTIONTIME = "inspectiontime";
        public static final String INSPECTIONUPLOADSTATE = "uploadstate";
    }

    public static final class EquipmentTable {
        public static final String ID = "_id";
        public static final String EQUIPMENTID = "equipmentid";
        public static final String EQUIPMENTPROJECTID = "projectid";
        public static final String EQUIPMENTCHECKINTIME = "checkintime";
        public static final String EQUIPMENTUPLOADSTATE = "uploadstate";
        public static final String EQUIPMENTLONGITUDE = "longitude";
        public static final String EQUIPMENTLATITUDE = "latitude";
        public static final String EQUIPMENTLOOCATION = "location";
    }
}
