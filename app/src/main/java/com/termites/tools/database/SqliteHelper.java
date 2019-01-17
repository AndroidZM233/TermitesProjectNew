package com.termites.tools.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.termites.tools.config.DataBaseConfig.*;
import static com.termites.tools.config.DataBaseConfig.EquipmentTable.*;
import static com.termites.tools.config.DataBaseConfig.InspectionTable.*;
import static com.termites.tools.config.DataBaseConfig.InspectionTable.ID;

/**
 * Created by LF on 16/10/21.
 */

public class SqliteHelper extends SQLiteOpenHelper {

    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                INSPECTIONTABLE + "(" +
                ID + " integer primary key autoincrement," +
                INSPECTIONID + " varchar(30)," +
                INSPECTIONTERMITESSTATE + " varchar(10)," +
                INSPECTIONTIME + " datetime," +
                INSPECTIONUPLOADSTATE + " varchar(10)" +
                ")"
        );

        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                EQUIPMENTTABLE + "(" +
                ID + " integer primary key autoincrement," +
                EQUIPMENTID + " varchar(30)," +
                EQUIPMENTPROJECTID + " varchar(30)," +
                EQUIPMENTCHECKINTIME + " datetime," +
                EQUIPMENTUPLOADSTATE + " varchar(10)," +
                EQUIPMENTLONGITUDE + " varchar(10)," +
                EQUIPMENTLATITUDE + " varchar(10)," +
                EQUIPMENTLOOCATION + " varchar(30)" +
                ")"
        );
    }

    //更新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + INSPECTIONTABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EQUIPMENTTABLE);
        onCreate(db);
    }

    //更新列
    public void updateColumn(SQLiteDatabase db, String tableName, String oldColumn, String newColumn, String typeColumn) {
        try {
            db.execSQL("ALTER TABLE " +
                    tableName + " CHANGE " +
                    oldColumn + " " + newColumn +
                    " " + typeColumn
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
