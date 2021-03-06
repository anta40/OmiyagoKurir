package com.app.omiyago.kurir.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.omiyago.kurir.model.ScannedItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 3/10/2019.
 */
public class DBHelper extends SQLiteOpenHelper {



    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "kuriromiyago_db";

    private String CREATE_TABLE_CMD;

    public DBHelper(Context ctxt){
        super(ctxt, DATABASE_NAME, null, DATABASE_VERSION);

        CREATE_TABLE_CMD = "CREATE TABLE IF NOT EXISTS tbl_scanned_item(item_id INTEGER PRIMARY KEY, nama VARCHAR, noRef VARCHAR, " +
                "jumlah REAL, total INTEGER, alamat VARCHAR, ttd VARCHAR (5000), nama_kurir VARCHAR, ttd_kurir VARCHAR (5000), status_pickup VARCHAR);";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_scanned_item");
        onCreate(db);
    }

    public long addItem(ScannedItem item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("item_id", item.getId());
        values.put("nama", item.getNama());
        values.put("noRef", item.getNoRef());
        values.put("jumlah", item.getJumlah());
        values.put("total", item.getTotal());
        values.put("alamat", item.getAlamat());
        values.put("ttd","");
        values.put("nama_kurir", "");
        values.put("ttd_kurir","");
        values.put("status_pickup","");

        long id = db.insert("tbl_scanned_item", null, values);
        db.close();

        return id;
    }

    public boolean isExist(ScannedItem item){
        boolean res = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM tbl_scanned_item WHERE item_id="+item.getId();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() > 0) res = true;
        cursor.close();
        db.close();

        return res;
    }

    public List<ScannedItem> getAllItems(){
        List<ScannedItem> result = new ArrayList<>();

        String query = "SELECT * FROM tbl_scanned_item";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery(query, null);

        if (mCursor.moveToFirst()) {
            do {
                int id = mCursor.getInt(mCursor.getColumnIndex("item_id"));
                String nama = mCursor.getString(mCursor.getColumnIndex("nama"));
                String noRef = mCursor.getString(mCursor.getColumnIndex("noRef"));
                int jumlah = mCursor.getInt(mCursor.getColumnIndex("jumlah"));
                double total = mCursor.getDouble(mCursor.getColumnIndex("total"));
                String alamat = mCursor.getString(mCursor.getColumnIndex("alamat"));
                String ttd = mCursor.getString(mCursor.getColumnIndex("ttd"));
                String nama_kurir = mCursor.getString(mCursor.getColumnIndex("nama_kurir"));
                String ttd_kurir = mCursor.getString(mCursor.getColumnIndex("ttd_kurir"));
                String status_pickup = mCursor.getString(mCursor.getColumnIndex("status_pickup"));
                ScannedItem item = new ScannedItem(id, nama, noRef, jumlah, total, alamat, ttd, nama_kurir, ttd_kurir, status_pickup);
                result.add(item);
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return result;
    }

    public void assignSignature(int item_id, String ttd_type, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";
        ContentValues cv = new ContentValues();

        if (ttd_type.equals("kurir")){
            cv.put("ttd_kurir", content);
            db.update("tbl_scanned_item", cv, "item_id="+item_id, null);
        }
        else {
            cv.put("ttd", content);
            db.update("tbl_scanned_item", cv, "item_id="+item_id, null);
        }

        db.close();
    }

    public void deleteItem(int item_id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM tbl_scanned_item WHERE item_id="+item_id;
        db.rawQuery(query, null);
        db.close();
    }

    public ScannedItem getItem(int item_id){
        ScannedItem result = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM tbl_scanned_item WHERE item_id="+item_id;
        Cursor mCursor = db.rawQuery(query, null);

        if (mCursor.moveToFirst()){
            int id = mCursor.getInt(mCursor.getColumnIndex("item_id"));
            String nama = mCursor.getString(mCursor.getColumnIndex("nama"));
            String noRef = mCursor.getString(mCursor.getColumnIndex("noRef"));
            int jumlah = mCursor.getInt(mCursor.getColumnIndex("jumlah"));
            double total = mCursor.getDouble(mCursor.getColumnIndex("total"));
            String alamat = mCursor.getString(mCursor.getColumnIndex("alamat"));
            String ttd = mCursor.getString(mCursor.getColumnIndex("ttd"));
            String nama_kurir = mCursor.getString(mCursor.getColumnIndex("nama_kurir"));
            String ttd_kurir = mCursor.getString(mCursor.getColumnIndex("ttd_kurir"));
            String status_pickup = mCursor.getString(mCursor.getColumnIndex("status_pickup"));
            result = new ScannedItem(id, nama, noRef, jumlah, total, alamat, ttd, nama_kurir, ttd_kurir, status_pickup);
        }

        mCursor.close();
        db.close();
        return result;
    }

    public boolean isSigned(int item_id, String ttd_type){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        Cursor cursor = null;

        if (ttd_type.equals("kurir")){
            query = "SELECT * FROM tbl_scanned_item WHERE item_id="+item_id;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                if (cursor.getString(cursor.getColumnIndex("ttd_kurir")).isEmpty()) result = false;
                else result = true;
            }
        }
        else {
            query = "SELECT * FROM tbl_scanned_item WHERE item_id="+item_id;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
               if (cursor.getString(cursor.getColumnIndex("ttd")).isEmpty()) result = false;
               else result = true;
            }
        }

        cursor.close();
        db.close();

        return result;
    }
}
