package com.syriasoft.cleanup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DNDDB extends SQLiteOpenHelper {

    SQLiteDatabase db;
    Context c;

    public DNDDB(@Nullable Context context) {
        super(context, "DND", null, 01);
        db = getWritableDatabase();
        this.c = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS dnds ('id' INTEGER PRIMARY KEY , 'roomNumber' INTEGER  ,'dep' VARCHAR ,'roomServiceText' TEXT , 'time' BIGINT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'dnds'");
        onCreate(db);
    }

    public boolean insertOrder(int room, String dep, String roomservicetext, long time) {
        boolean result = false;
        ContentValues values = new ContentValues();
        values.put("roomNumber", room);
        values.put("dep", dep);
        values.put("roomServiceText", roomservicetext);
        values.put("time", time);
        try {
            db.insert("dnds", null, values);
            result = true;
        } catch (Exception e) {
            Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("bbb", e.getMessage());
            result = false;
        }

        return result;
    }

    public boolean isNotEmpty() {
        boolean result = false;
        Cursor c = db.query("dnds", new String[]{"id"}, "", null, null, null, null);
        if (c.getCount() == 1) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public void removeAll() {
        db.execSQL("DROP TABLE IF EXISTS 'dnds'");
        onCreate(db);
    }

    public List<cleanOrder> getOrders() {
        List<cleanOrder> list = new ArrayList<cleanOrder>();
        int id;
        int room;
        String dep;
        String rst;
        long time;

        Cursor c = db.rawQuery("SELECT * FROM 'dnds' ; ", null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                id = c.getInt(0);
                room = c.getInt(1);
                dep = c.getString(2);
                rst = c.getString(3);
                time = c.getLong(4);
                cleanOrder u = new cleanOrder(String.valueOf(room), String.valueOf(id), dep, rst, time);
                list.add(u);
                if (i != (c.getCount() + 1)) {
                    c.moveToNext();
                }
            }
        }
        c.close();
        return list;
    }

    public boolean removeRow(Long id) {

        boolean res = false;
        ContentValues values = new ContentValues();
        values.put("id", id);
        List<cleanOrder> c = getOrders();

        for (int i = 0; i < c.size(); i++) {
            if (Integer.parseInt(c.get(i).orderNumber) == id)//
            {
                db.delete("dnds", "id=?", new String[]{values.get("id").toString()});
                res = true;
            }
        }
        return res;
    }

    public boolean searchOrder(int room, String dep) {
        boolean res = false;
        Cursor c = db.rawQuery("SELECT * FROM 'dnds' ; ", null);
        c.moveToFirst();
        if (c.getCount() > 0) {
            for (int i = 0; i < c.getCount(); i++) {
                if (c.getInt(1) == room && dep.equals(c.getString(2))) {
                    res = true;
                }
                if (i != (c.getCount() + 1)) {
                    c.moveToNext();
                }
            }
        }
        return res;
    }
}