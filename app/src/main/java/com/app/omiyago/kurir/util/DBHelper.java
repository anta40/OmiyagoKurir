package com.app.omiyago.kurir.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.omiyago.kurir.model.ScannedItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Andre Tampubolon (andre.tampubolon@idstar.co.id) on 3/10/2019.
 */
public class DBHelper extends SQLiteOpenHelper {

    enum TTD {
        PEMILIK,
        KURIR
    }

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "kuriromiyago_db";

    private String CREATE_TABLE_CMD;

    public DBHelper(Context ctxt){
        super(ctxt, DATABASE_NAME, null, DATABASE_VERSION);

        CREATE_TABLE_CMD = "CREATE TABLE IF NOT EXISTS tbl_scanned_item(item_id INTEGER PRIMARY KEY, nama VARCHAR, noRef VARCHAR, " +
                "jumlah REAL, total INTEGER, alamat VARCHAR, ttd VARCHAR, nama_kurir VARCHAR, ttd_kurir VARCHAR, status_pickup VARCHAR);";
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
        //int res = 0;
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

    public void assignSignature(int item_id, TTD ttd, String content){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "";

        if (ttd == TTD.KURIR){
            query = "UPDATE tbl_scanned_item SET ttd_kurir='"+content+"' WHERE item_id="+item_id;
        }
        else {
            query = "UPDATE tbl_scanned_item SET ttd='"+content+"' WHERE item_id="+item_id;
        }

        db.rawQuery(query, null);

        db.close();
    }

    public boolean isSigned(ScannedItem item, TTD ttd){
        boolean result = false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        Cursor cursor = null;

        if (ttd == TTD.KURIR){
            query = "SELECT ttd_kurir WHERE item_id="+item.getId();
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                result = cursor.getString(cursor.getColumnIndex("ttd_kurir")).isEmpty();
            }
        }
        else {
            query = "SELECT ttd WHERE item_id="+item.getId();
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                result = cursor.getString(cursor.getColumnIndex("ttd")).isEmpty();
            }
        }

        cursor.close();
        db.close();

        return result;
    }
}
