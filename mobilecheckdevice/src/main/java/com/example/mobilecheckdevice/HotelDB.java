package com.example.mobilecheckdevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HotelDB extends SQLiteOpenHelper
{
    SQLiteDatabase db ;

    public HotelDB(@Nullable Context context)
    {
        super(context, "Hotel", null, 1);
        db =getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //db =getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS hotel ( 'id' INTEGER PRIMARY KEY ,'DBid' INTEGER , 'HotelName' VARCHAR , 'city' VARCHAR , 'TuyaProject' VARCHAR , 'TTLockPriject' VARCHAR ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void Logout()
    {
        db.execSQL("DROP TABLE IF EXISTS 'hotel'");
        onCreate(db);
    }

    public boolean insertHotel ( int DBid , String HotelName, String city )
    {
        boolean result = false ;
        ContentValues values = new ContentValues();
        values.put( "DBid" , DBid );
        values.put( "HotelName" , HotelName );
        values.put( "city" , city );
        values.put("TuyaProject" , "0");
        values.put("TTLockPriject","0");

        try
        {
            db.insert("hotel", null, values);
            result = true ;
        }
        catch (Exception e )
        {
            result = false ;
        }

        return result ;
    }

    public int getHotelId()
    {
        int num = 0 ;
        Cursor c = db.query("hotel", new String[]{"DBid"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public String getHotelName()
    {
        String name = "" ;
        Cursor c = db.query("hotel", new String[]{"HotelName"}, "", null, null, null, null);
        c.moveToFirst();
        name = c.getString(0);

        return name ;
    }

    public boolean isLoggedIn()
    {
        boolean result = false ;
        Cursor c = db.query("hotel", new String[]{"id"}, "", null, null, null, null);
        if (c.getCount() == 1)
        {
            result = true ;
        }
        else
        {
            result = false ;
        }

        return result;
    }

    public void insertTuyaProject(String tuya)
    {
        if (isLoggedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("TuyaProject" , tuya );
            db.update("hotel" , cv , "id= 1" ,null) ;
        }
    }

    public String getTuyaProject()
    {
        String tuya = "";

        Cursor c = db.query("hotel", new String[]{"TuyaProject"}, "", null, null, null, null);
        c.moveToFirst();
        tuya = c.getString(0);

        return tuya ;
    }

    public void insertTTLockProject(String tt)
    {
        if (isLoggedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("TTLockPriject" , tt );
            db.update("hotel" , cv , "id= 1" ,null) ;
        }
    }

    public String getTTLockProject()
    {
        String tt = "";

        Cursor c = db.query("hotel", new String[]{"TTLockPriject"}, "", null, null, null, null);
        c.moveToFirst();
        tt = c.getString(0);

        return tt ;
    }
}
