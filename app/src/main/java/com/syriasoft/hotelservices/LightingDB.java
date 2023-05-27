package com.syriasoft.hotelservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LightingDB extends SQLiteOpenHelper {

    static String DBName = "LightingDB" ;
    static int Version = 1 ;
    SQLiteDatabase DB ;

    public LightingDB(Context context) {
        super(context, DBName, null , Version);
        DB = getWritableDatabase() ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS masterOff ( 'switch' INTEGER , 'button' INTEGER) ;");
        db.execSQL("CREATE TABLE IF NOT EXISTS screenBtn ('switch' INTEGER , 'button' INTEGER , 'name' VARCHAR(15)) ;");
        db.execSQL("CREATE TABLE IF NOT EXISTS moodBtn ('switch' INTEGER , 'button' INTEGER , 'name' VARCHAR(15)) ;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertButtonToMasterOff(int Switch , int Btn) {
        long res = 0 ;
        ContentValues values = new ContentValues();
        values.put("switch",Switch);
        values.put("button",Btn);
        try {
            res =  DB.insert("masterOff", null, values);

        }
        catch (Exception e ) {
            res = -1 ;
        }

        boolean x = false ;

        if (res == -1) {
            x= false ;
        }
        else if (res >0){
            x=  true ;
        }
        return x ;
    }

    public boolean insertMoodToScreen(int Switch , int Btn , String name) {
        long res = 0 ;
        ContentValues values = new ContentValues();
        values.put("switch",Switch);
        values.put("button",Btn);
        values.put("name",name);
        try {
            res =  DB.insert("moodBtn", null, values);
        }
        catch (Exception e ) {
            res = -1 ;
        }

        boolean x = false ;

        if (res == -1) {
            x= false ;
        }
        else if (res >0){
            x=  true ;
        }
        return x ;
    }

    public boolean insertButtonToScreen(int Switch , int Btn , String name) {
        long res = 0 ;
        ContentValues values = new ContentValues();
        values.put("switch",Switch);
        values.put("button",Btn);
        values.put("name",name);
        try {
            res =  DB.insert("screenBtn", null, values);

        }
        catch (Exception e ) {
            res = -1 ;
        }

        boolean x = false ;

        if (res == -1) {
            x= false ;
        }
        else if (res >0){
            x=  true ;
        }
        return x ;
    }

    public List<MasterOffButton> getMasterOffButtons() {
        List<MasterOffButton> list = new ArrayList<MasterOffButton>();

        Cursor c = DB.query("masterOff", new String[]{"switch","button"}, "", null, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++) {
            MasterOffButton item = new MasterOffButton(c.getInt(0),c.getInt(1));
            list.add(item);
            c.moveToNext();
        }
        c.close();
        return list ;
    }

    public List<ScreenButton> getScreenButtons () {
        List<ScreenButton> list = new ArrayList<ScreenButton>();

        Cursor c = DB.query("screenBtn", new String[]{"switch","button","name"}, "", null, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++) {
            ScreenButton item = new ScreenButton(c.getInt(0),c.getInt(1),c.getString(2));
            list.add(item);
            c.moveToNext();
        }
        c.close();
        return list ;
    }

    public List<ScreenButton> getMoodButtons () {
        List<ScreenButton> list = new ArrayList<>();

        Cursor c = DB.query("moodBtn", new String[]{"switch","button","name"}, "", null, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++) {
            ScreenButton item = new ScreenButton(c.getInt(0),c.getInt(1),c.getString(2));
            list.add(item);
            c.moveToNext();
        }
        c.close();
        return list ;
    }

    public boolean deleteButtonFromMasterOff (int Switch , int button) {
        boolean res = false ;
        ContentValues values = new ContentValues();
        values.put("switch",Switch);
        values.put("button",button);
        List<MasterOffButton> c = getMasterOffButtons();

        for (int i=0 ; i<c.size() ; i++)
        {
            if (c.get(i).Switch == Switch && c.get(i).button == button )//
            {
                if (DB.delete("masterOff" , "switch=? and button=?" ,new String[]{values.get("switch").toString(),values.get("button").toString()} ) >0) {
                    res = true ;
                }
            }
        }

        return res ;
    }

    public boolean deleteButtonFromScreen(int Switch , int button , String name) {
        boolean res = false ;
        ContentValues values = new ContentValues();
        values.put("switch",Switch);
        values.put("button",button);
        values.put("name" , name);
        List<ScreenButton> c = getScreenButtons();

        for (int i=0 ; i<c.size() ; i++)
        {
            if (c.get(i).Switch == Switch && c.get(i).button == button )//
            {
                if (DB.delete("screenBtn" , "switch=? and button=? and name=?" ,new String[]{values.get("switch").toString(),values.get("button").toString(),values.get("name").toString()}) >0) {
                    res = true ;
                }
            }
        }

        return res ;
    }

    public void dropMasterOff() {
        DB.execSQL("DROP TABLE IF EXiSTS masterOff");
    }

}
