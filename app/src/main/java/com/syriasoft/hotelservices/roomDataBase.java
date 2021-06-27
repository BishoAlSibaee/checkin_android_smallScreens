package com.syriasoft.hotelservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class roomDataBase extends SQLiteOpenHelper {

    private static  int DATABASE_VESION = 1 ;
    private static String DATABASE_NAME = "Room" ;
    SQLiteDatabase db ;

    public roomDataBase( Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VESION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS room ( 'id' INTEGER PRIMARY KEY ,'DBid' INTEGER ,'RoomNumber' INTEGER ,'Hotel' INTEGER ,'Building' INTEGER , 'RoomType' VARCHAR , 'Floor' INTEGER,'token' TEXT,'lock' VARCHAR , 'project' VARCHAR , 'gateway' VARCHAR , 'lockGateway' VARCHAR ,'tuyaProject' VARCHAR ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS 'room'");
        onCreate(db);
    }

    public boolean insertRoom (int DBid,int RoomNumber ,String RoomType, int Floor , String token , int Hotel , int Building )
    {
        boolean result = false ;
        ContentValues values = new ContentValues();
        values.put( "DBid" , DBid );
        values.put( "RoomNumber" , RoomNumber );
        values.put( "RoomType" , RoomType );
        values.put( "Floor" , Floor );
        values.put( "token" , token );
        values.put("Hotel" , Hotel );
        values.put("Building" , Building );
        values.put( "lock" , "0" );
        values.put( "project" , "0" );
        values.put( "gateway" , "0" );
        values.put("lockGateway" , "0");
        values.put("tuyaProject" ,"0");
        try {
            db.insert("room", null, values);
            result = true ;
        }catch (Exception e )
        {
            result = false ;
        }

        return result ;
    }

    public boolean isLogedIn()
    {
        boolean result = false ;
        Cursor c = db.query("room", new String[]{"id"}, "", null, null, null, null);
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

    public int getRoomNumber()
    {
        int num = 0 ;
        Cursor c = db.query("room", new String[]{"RoomNumber"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public int getRoomDBid()
    {
        int num = 0 ;
        Cursor c = db.query("room", new String[]{"DBid"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public int getHotel()
    {
        int num = 0 ;
        Cursor c = db.query("room", new String[]{"Hotel"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public int getBuilding()
    {
        int num = 0 ;
        Cursor c = db.query("room", new String[]{"Building"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public int getFloor()
    {
        int num = 0 ;
        Cursor c = db.query("room", new String[]{"Floor"}, "", null, null, null, null);
        c.moveToFirst();
        num = c.getInt(0);

        return num ;
    }

    public void logout()
    {
        db.execSQL("DROP TABLE IF EXISTS 'room'");
        onCreate(db);
    }

    public void insertTuyaProject(String lockName)
    {
        if (isLogedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("tuyaProject" , lockName );
            db.update("room" , cv , "id= 1" ,null) ;
        }
    }

    public String getTuyaProject()
    {
        String lock = "";

        Cursor c = db.query("room", new String[]{"tuyaProject"}, "", null, null, null, null);
        c.moveToFirst();
        lock = c.getString(0);

        return lock ;
    }

    public void insertLock(String lockName)
    {
        if (isLogedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("lock" , lockName );
            db.update("room" , cv , "id= 1" ,null) ;
        }
    }

    public void insertLockGateway(String lockName)
    {
        if (isLogedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("lockGateway" , lockName );
            db.update("room" , cv , "id= 1" ,null) ;
        }
    }

    public String getLockGateway()
    {
        String lock = "";

        Cursor c = db.query("room", new String[]{"lockGateway"}, "", null, null, null, null);
        c.moveToFirst();
        lock = c.getString(0);

        return lock ;
    }

    public void insertProject(String project)
    {
        if (isLogedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("project" , project );
            db.update("room" , cv , "id= 1" ,null) ;
        }
    }

    public void insertGateway(String gateway)
    {
        if (isLogedIn())
        {
            ContentValues cv = new ContentValues();
            cv.put("gateway" , gateway );
            db.update("room" , cv , "id= 1" ,null) ;
        }
    }

    public String getLockName()
    {
        String lock = "";

            Cursor c = db.query("room", new String[]{"lock"}, "", null, null, null, null);
            c.moveToFirst();
            lock = c.getString(0);

        return lock ;
    }

    public String getProjectName()
    {
        String project = "";

        Cursor c = db.query("room", new String[]{"project"}, "", null, null, null, null);
        c.moveToFirst();
        project = c.getString(0);

        return project ;
    }

    public String getGateway()
    {
        String gateway = "";

        Cursor c = db.query("room", new String[]{"gateway"}, "", null, null, null, null);
        c.moveToFirst();
        gateway = c.getString(0);

        return gateway ;
    }

}
