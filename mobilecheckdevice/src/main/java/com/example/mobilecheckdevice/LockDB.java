package com.example.mobilecheckdevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LockDB  extends SQLiteOpenHelper {

    SQLiteDatabase db ;
    Context c ;

    public LockDB(@Nullable Context context) {
        super(context, "Lock", null, 01);
        db =getWritableDatabase();
        this.c = context ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Lock ('id' INTEGER PRIMARY KEY ,'Lock' VARCHAR ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'Lock'");
        onCreate(db);
    }

    public boolean insertLock ( String lock )
    {
        boolean result = false ;
        ContentValues values = new ContentValues();
        values.put("Lock",lock);
        try {
            db.insert("Lock", null, values);
            result = true ;
        }catch (Exception e )
        {
            Toast.makeText(c , e.getMessage() , Toast.LENGTH_LONG).show();
            Log.e("bbb" , e.getMessage());
            result = false ;
        }

        return result ;
    }

    public void removeAll()
    {
        db.execSQL("DROP TABLE IF EXISTS 'Lock'");
        onCreate(db);
    }

    public boolean isLoggedIn()
    {
        boolean result = false ;
        Cursor c = db.query("Lock", new String[]{"id"}, "", null, null, null, null);
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

    public String getLockValue()
    {
        String value = null;

        Cursor c = db.rawQuery("SELECT * FROM 'Lock' ; " , null);
        c.moveToFirst();
        value = c.getString(1);

        return value ;
    }

    public boolean modifyValue(String newValue)
    {
        boolean res = false ;

        ContentValues cv = new ContentValues();
        cv.put("Lock" , newValue );
        db.update("Lock" , cv , "id="+String.valueOf(1) , null ) ;
        res = true ;

        return res ;
    }
}
