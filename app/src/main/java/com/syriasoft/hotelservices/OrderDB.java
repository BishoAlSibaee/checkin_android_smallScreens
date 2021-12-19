package com.syriasoft.hotelservices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class OrderDB extends SQLiteOpenHelper {

    static String DBNAME  = "orders";
    static int VERTION = 1 ;
    SQLiteDatabase db ;

    public OrderDB(@Nullable Context context)
    {
        super(context, DBNAME , null , VERTION);
        db =getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS orders ( 'id' INTEGER  ,'fac' INTEGER,'type' VARCHAR ,'name' VARCHAR ,'desc' VARCHAR , 'quantity' INTEGER , 'price' DOUBLE,'discount' DOUBLE, 'total' DOUBLE ,'photo' TEXT ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldOrders ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT ,'clientF' VARCHAR ,'clientL' VARCHAR , 'total' DOUBLE ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldOrdersItems ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT , 'order' INTEGER  ,'type' VARCHAR ,'name' VARCHAR ,'desc' VARCHAR , 'quantity' INTEGER , 'price' DOUBLE,'discount' DOUBLE, 'total' DOUBLE ,'photo' TEXT ) ");
    }

    @Override
    public void onCreate(SQLiteDatabase dbi) {
        //db =getWritableDatabase();
        //db.execSQL("CREATE TABLE IF NOT EXISTS orders ( 'id' INTEGER  ,'type' VARCHAR ,'name' VARCHAR ,'desc' VARCHAR , 'quantity' INTEGER , 'price' DOUBLE,'discount' DOUBLE, 'total' DOUBLE ,'photo' TEXT ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase dbc, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS 'orders'");
        onCreate(db);
    }

    public long insertOldOrder (String clientF , String clientL , double total )
    {
        long result = 0 ;
        ContentValues values = new ContentValues();
        values.put("clientF",clientF);
        values.put("clientL",clientL);
        values.put("total",total);
        try
        {
           result =  db.insert("OldOrders", null, values);

        }
        catch (Exception e )
        {
            result = -1 ;
        }

        return result ;
    }

    public boolean insertOldOrderItem (int order ,String type ,String name,String desc, int quantity , double price , double discount , double total , String photo)
    {
        boolean result = false ;
        ContentValues values = new ContentValues();
        values.put("order",order);
        values.put("type",type);
        values.put("name",name);
        values.put("desc",desc);
        values.put("quantity",quantity);
        values.put("price",price);
        values.put("discount",discount);
        values.put("total",total);
        values.put("photo",photo);
        try {
            db.insert("orders", null, values);
            result = true ;
        }catch (Exception e )
        {
            result = false ;
        }

        return result ;
    }

    public void cleanOldOrders()
    {
        db.execSQL("DROP TABLE IF EXISTS 'OldOrders'");
        db.execSQL("DROP TABLE IF EXISTS 'OldOrdersItems'");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldOrders ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT ,'clientF' VARCHAR ,'clientL' VARCHAR , 'total' DOUBLE ) ");
        db.execSQL("CREATE TABLE IF NOT EXISTS OldOrdersItems ( 'id' INTEGER PRIMARY KEY AUTOINCREMENT , 'order' INTEGER  ,'type' VARCHAR ,'name' VARCHAR ,'desc' VARCHAR , 'quantity' INTEGER , 'price' DOUBLE,'discount' DOUBLE, 'total' DOUBLE ,'photo' TEXT ) ");
    }

    public List<RestaurantOrderItem> getOldOrderItems(int order)
    {
        List<RestaurantOrderItem> list = new ArrayList<RestaurantOrderItem>();

        String Order = String.valueOf(order);

        Cursor c = db.query("OldOrdersItems", new String[]{"id","order","type","name","desc","quantity","price","discount","total" , "photo"}, "order=?", new String[] { Order }, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++)
        {
            RestaurantOrderItem item = new RestaurantOrderItem(c.getInt(0),c.getInt(1),c.getString(2),c.getString(3),c.getString(4),c.getInt(5),c.getDouble(6),c.getDouble(7),c.getInt(8),c.getString(9));
            list.add(item);
            c.moveToNext();
        }

        return list ;
    }

    public List<RestaurantOldOrder> getOldOrders()
    {
        List<RestaurantOldOrder> list = new ArrayList<RestaurantOldOrder>();

        Cursor c = db.query("OldOrders", new String[]{"id","clientF","clientL","total"}, "", null, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++)
        {
            RestaurantOldOrder item = new RestaurantOldOrder(c.getInt(0),c.getString(1),c.getString(2),c.getDouble(3));
            list.add(item);
            c.moveToNext();
        }

        return list ;
    }

    public boolean insertOrder (int id ,int fac,String type ,String name,String desc, int quantity , double price , double discount , double total , String photo)
    {
        boolean result = false ;
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("fac",fac);
        values.put("type",type);
        values.put("name",name);
        values.put("desc",desc);
        values.put("quantity",quantity);
        values.put("price",price);
        values.put("discount",discount);
        values.put("total",total);
        values.put("photo",photo);
        try {
            db.insert("orders", null, values);
            result = true ;
        }catch (Exception e )
        {
            result = false ;
        }

        return result ;
    }

    public boolean isEmpty()
    {
        boolean result = false ;
        Cursor c = db.query("orders", new String[]{"id"}, "", null, null, null, null);
        if (c.getCount() >= 1)
        {
            result = false ;
        }
        else
        {
            result = true ;
        }

        return result;
    }

    public List<RestaurantOrderItem> getItems()
    {
        List<RestaurantOrderItem> list = new ArrayList<RestaurantOrderItem>();

        Cursor c = db.query("orders", new String[]{"id","fac","type","name","desc","quantity","price","discount","total" , "photo"}, "", null, null, null, null);

        c.moveToFirst();

        for (int i=0 ; i<c.getCount(); i++)
        {
            RestaurantOrderItem item = new RestaurantOrderItem(c.getInt(0),c.getInt(1),c.getString(2),c.getString(3),c.getString(4),c.getInt(5),c.getDouble(6),c.getDouble(7),c.getInt(8),c.getString(9));
            list.add(item);
            c.moveToNext();
        }

        return list ;
    }

    public void removeOrder()
    {
        db.execSQL("DROP TABLE IF EXISTS 'orders'");
        db.execSQL("CREATE TABLE IF NOT EXISTS orders ( 'id' INTEGER ,'fac' INTEGER ,'type' VARCHAR ,'name' VARCHAR ,'desc' VARCHAR , 'quantity' INTEGER , 'price' DOUBLE,'discount' DOUBLE, 'total' DOUBLE ,'photo' TEXT ) ");
    }

    public boolean removeItem(int id , int quantity)
    {
        boolean res = false ;
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("quantity",quantity);
        List<RestaurantOrderItem> c = getItems();

        for (int i=0 ; i<c.size() ; i++)
        {
            if (c.get(i).id == id && c.get(i).quantity == quantity )//
            {
                db.delete("orders" , "id=?" ,new String[]{values.get("id").toString()} );
                res = true ;
            }
            //c.moveToNext();
        }

        return res ;
    }

    public boolean modifyItem(int id , int quantity)
    {
        boolean res = false ;
        List<RestaurantOrderItem> c = getItems();

        for (int i=0 ; i<c.size() ; i++)
        {
            Log.d("modify" , c.get(i).id+" " +id);
            if (c.get(i).id == id )//
            {
                double price = c.get(i).price ;
                ContentValues cv = new ContentValues();
                cv.put("quantity" , quantity );
                double total = quantity*price ;
                cv.put("total" , total);
                if (db.update("orders" , cv , "id="+String.valueOf(id) , null ) > 0 ) {
                    res = true ;
                }

            }
            //c.moveToNext();
        }
        return res ;
    }
}
