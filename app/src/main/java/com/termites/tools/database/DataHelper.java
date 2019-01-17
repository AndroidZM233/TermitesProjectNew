package com.termites.tools.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.termites.tools.javabean.EquipmentBean;
import com.termites.tools.javabean.InspectionBean;

import java.util.ArrayList;
import java.util.List;

import static com.termites.tools.config.DataBaseConfig.*;

/**
 * Created by LF on 16/10/22.
 */

public class DataHelper {

    private SQLiteDatabase db;
    private SqliteHelper dbHelper;


    public DataHelper(Context context) {
        dbHelper = new SqliteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
        dbHelper.close();
    }

    // 查询Inspection表中的所有数据
    public List<InspectionBean> getInspectionAllData() {
        List<InspectionBean> mDatas = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from " + INSPECTIONTABLE +
                " ORDER BY " + InspectionTable.INSPECTIONTIME + " DESC LIMIT 50", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && !TextUtils.isEmpty(cursor.getString(1))) {
            InspectionBean inspectionBean = new InspectionBean();
            inspectionBean.setInspecId(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONID)));
            inspectionBean.setInspectionTermiteState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTERMITESSTATE)));
            inspectionBean.setInspectionTime(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTIME)));
            inspectionBean.setInspectionUploadState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONUPLOADSTATE)));
            mDatas.add(inspectionBean);
            cursor.moveToNext();
        }
        cursor.close();
        return mDatas;
    }

    // 查询Insepection表中数据按时间排序并按InsepectionId分组
    public List<InspectionBean> getInspectionAllDataMaxTime() {
        List<InspectionBean> mDatas = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from" +
                " (select * from " + INSPECTIONTABLE +
                " order by " + InspectionTable.INSPECTIONTIME +
                " ASC) t" +
                " group by " + InspectionTable.INSPECTIONID + " LIMIT 50", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && !TextUtils.isEmpty(cursor.getString(1))) {
            InspectionBean inspectionBean = new InspectionBean();
            inspectionBean.setInspecId(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONID)));
            inspectionBean.setInspectionTermiteState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTERMITESSTATE)));
            inspectionBean.setInspectionTime(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTIME)));
            inspectionBean.setInspectionUploadState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONUPLOADSTATE)));
            mDatas.add(inspectionBean);
            cursor.moveToNext();
        }
        cursor.close();
        return mDatas;
    }

    // 查询Equipment表中的所有数据
    public List<EquipmentBean> getEquipmentAllData(boolean isShowUploadData) {
        List<EquipmentBean> mDatas = new ArrayList<>();
        Cursor cursor;
        if (isShowUploadData) {
            cursor = db.rawQuery("select * from " +
                    EQUIPMENTTABLE + " ORDER BY " + EquipmentTable.EQUIPMENTCHECKINTIME + " DESC LIMIT 50", null);
        } else {
            cursor = db.rawQuery("select * from " + EQUIPMENTTABLE +
                    " where " + EquipmentTable.EQUIPMENTUPLOADSTATE + "=?" +
                    " order by " + EquipmentTable.EQUIPMENTCHECKINTIME + " DESC LIMIT 50", new String[]{"未上传"});
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && !TextUtils.isEmpty(cursor.getString(1))) {
            EquipmentBean equipmentBean = new EquipmentBean();
            equipmentBean.setEquipmentId(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTID)));
            equipmentBean.setEquipmentProjectId(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTPROJECTID)));
            equipmentBean.setEquipmentCheckTime(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTCHECKINTIME)));
            equipmentBean.setEquipmentLongitude(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTLONGITUDE)));
            equipmentBean.setEquipmentLatitude(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTLATITUDE)));
            equipmentBean.setEquipmentUploadState(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTUPLOADSTATE)));
            equipmentBean.setEquipmentLocation(cursor.getString(cursor.getColumnIndex(EquipmentTable.EQUIPMENTLOOCATION)));
            mDatas.add(equipmentBean);
            cursor.moveToNext();
        }
        cursor.close();
        return mDatas;
    }

    // 删除Inspection表中的所有InspecId相等的纪录
    public void deleteInspectionOneData(String InspecId) {
        db.execSQL("delete from " + INSPECTIONTABLE +
                        " where " + InspectionTable.INSPECTIONID + "=?",
                new String[]{InspecId});
    }

    // 删除Equipment表中的某条纪录
    public void deleteEquipmentOneData(EquipmentBean bean) {
        db.execSQL("delete from " + EQUIPMENTTABLE +
                        " where " + EquipmentTable.EQUIPMENTID + "=?" +
                        " and " + EquipmentTable.EQUIPMENTPROJECTID + "=?" +
                        " and " + EquipmentTable.EQUIPMENTCHECKINTIME + "=?" +
                        " and " + EquipmentTable.EQUIPMENTLONGITUDE + "=?" +
                        " and " + EquipmentTable.EQUIPMENTLATITUDE + "=?",
                new String[]{bean.getEquipmentId(),
                        bean.getEquipmentProjectId(),
                        bean.getEquipmentCheckTime(),
                        bean.getEquipmentLongitude(),
                        bean.getEquipmentLatitude()});
    }

    // 清空Inspection表中数据
    public void clearInspectionData() {
        db.delete(INSPECTIONTABLE, null, null);
    }

    // 清空Equipment表中数据
    public void clearEquipmentData() {
        db.delete(EQUIPMENTTABLE, null, null);
    }

    // 往Inspection表中插入数据
    public void insertInspectionData(InspectionBean bean) {
        ContentValues values = new ContentValues();
        values.put(InspectionTable.INSPECTIONID, bean.getInspecId());
        values.put(InspectionTable.INSPECTIONTIME, bean.getInspectionTime());
        values.put(InspectionTable.INSPECTIONTERMITESSTATE, bean.getInspectionTermiteState());
        values.put(InspectionTable.INSPECTIONUPLOADSTATE, bean.getInspectionUploadState());
        db.insert(INSPECTIONTABLE, InspectionTable.ID, values);
    }

    // 往Equipment表中插入数据
    public void insertEquipmentData(EquipmentBean bean) {
        ContentValues values = new ContentValues();
        values.put(EquipmentTable.EQUIPMENTID, bean.getEquipmentId());
        values.put(EquipmentTable.EQUIPMENTPROJECTID, bean.getEquipmentProjectId());
        values.put(EquipmentTable.EQUIPMENTCHECKINTIME, bean.getEquipmentCheckTime());
        values.put(EquipmentTable.EQUIPMENTLONGITUDE, bean.getEquipmentLongitude());
        values.put(EquipmentTable.EQUIPMENTLATITUDE, bean.getEquipmentLatitude());
        values.put(EquipmentTable.EQUIPMENTUPLOADSTATE, bean.getEquipmentUploadState());
        values.put(EquipmentTable.EQUIPMENTLOOCATION, bean.getEquipmentLocation());
        db.insert(EQUIPMENTTABLE, EquipmentTable.ID, values);
    }

    // 修改Equipment表中的EQUIPMENTUPLOADSTATE值
    public void updateEpuipmentData(EquipmentBean bean) {
        db.execSQL("update " + EQUIPMENTTABLE + " set " +
                        EquipmentTable.EQUIPMENTUPLOADSTATE + "=? where " +
                        EquipmentTable.EQUIPMENTID + "=? and " +
                        EquipmentTable.EQUIPMENTCHECKINTIME + "=?"
                , new String[]{bean.getEquipmentUploadState(),
                        bean.getEquipmentId(),
                        bean.getEquipmentCheckTime()});
    }

    // 修改Equipment表中的EQUIPMENTLONGITUDE和EQUIPMENTLATITUDE的值
    public void updateEpuipmentData_LongitudeAndLatitude(EquipmentBean bean) {
        db.execSQL("update " + EQUIPMENTTABLE + " set " +
                        EquipmentTable.EQUIPMENTLONGITUDE + "=?" +
                        "," +
                        EquipmentTable.EQUIPMENTLATITUDE + "=?" +
                        " where " +
                        EquipmentTable.EQUIPMENTID + "=? and " +
                        EquipmentTable.EQUIPMENTCHECKINTIME + "=?"
                , new String[]{bean.getEquipmentLongitude(),
                        bean.getEquipmentLatitude(),
                        bean.getEquipmentId(),
                        bean.getEquipmentCheckTime()});
    }

    // 查询当前INSPECTIONTABLE表中项目编号(监测点编号)时间最大的那条数据
    public InspectionBean selectInspectionState(String InspectionId) {
        InspectionBean inspectionBean = new InspectionBean();
        Cursor cursor = db.rawQuery("select * from" +
                " (select * from " + INSPECTIONTABLE +
                " order by " + InspectionTable.INSPECTIONTIME +
                " ASC) t" +
                " where " + InspectionTable.INSPECTIONID + "=?" +
                " group by " + InspectionTable.INSPECTIONID, new String[]{InspectionId});
        cursor.moveToFirst();
        while (!cursor.isAfterLast() && !TextUtils.isEmpty(cursor.getString(1))) {
            inspectionBean.setInspecId(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONID)));
            inspectionBean.setInspectionTermiteState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTERMITESSTATE)));
            inspectionBean.setInspectionTime(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONTIME)));
            inspectionBean.setInspectionUploadState(cursor.getString(cursor.getColumnIndex(InspectionTable.INSPECTIONUPLOADSTATE)));
            cursor.moveToNext();
        }
        cursor.close();
        return inspectionBean;
    }
}
