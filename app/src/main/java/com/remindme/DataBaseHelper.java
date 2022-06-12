package com.remindme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TAYYAB ALI on 4/23/2017.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    public static String databaseName="RemindMe.db";
    public String locationTable="Location_table";
    public String textTable="Text_table";
    /////////////////     Location Field  ///////////////
    public  String Id="ID";
    public  String Lat="LAT";
    public  String Lon="LON";
    public String Dist ="DIST";
    public String L_AllContact="L_ALLCONTACT";
    public  String L_Title="L_TITLE";
    ////////////////         Text Field //////////////
    public  String Tid="ID";
    public  String Ttitle="TTITLE";
    public  String Tdesc="TDESC";

    public DataBaseHelper(Context context) {
        super(context,databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+locationTable+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, LAT TEXT,LON TEXT,DIST TEXT,L_ALLCONTACT TEXT,L_TITLE TEXT)");
        db.execSQL("create table "+textTable+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, TTITLE TEXT,TDESC TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+textTable);
        onCreate(db);
    }

    public void insertIntoLocation(String lat , String lon,String dist,String l_allContact,String l_title){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(Lat,lat);
        contentValues.put(Lon,lon);
        contentValues.put(Dist,dist);
        contentValues.put(L_AllContact,l_allContact);
        contentValues.put(L_Title,l_title);
        db.insert(locationTable,null,contentValues);
    }

    public void insertIntoText(String ttitle , String tdesc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Ttitle, ttitle);
        contentValues.put(Tdesc, tdesc);
        db.insert(textTable,null,contentValues);
    }





    public Cursor showText(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from  " +textTable,null);
        return cursor;
    }

    public Cursor showLocation(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from  " +locationTable,null);
        return cursor;
    }


    public  void  updateRecord(String id ,String titile,String desc){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(Tid,id);
        contentValues.put(Ttitle,titile);
        contentValues.put(Tdesc,desc);
        db.update(textTable,contentValues,"ID = ?",new String[] {id});
    }

    public Integer deleteDataFromLocation(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        return  db.delete(locationTable,"ID = ?",new String[]{id});
    }

    public Integer deleteDataFromText(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        return  db.delete(textTable,"ID = ?",new String[]{id});
    }


} //main
