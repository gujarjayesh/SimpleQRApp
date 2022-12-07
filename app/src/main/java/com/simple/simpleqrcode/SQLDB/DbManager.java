package com.simple.simpleqrcode.SQLDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DbManager extends SQLiteOpenHelper {

    public static final String dbname = "dbQuote";

    public DbManager(@Nullable Context context) {
        super(context, dbname, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String qry = "create table tbl_quote(id integer primary key autoincrement,code text,name text,gst text,qty text,mrp text)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String qry = "DROP TABLE IF EXISTS tbl_quote";
        db.execSQL(qry);
        onCreate(db);
    }

    public Boolean verifyData(String TABLE_NAME, String name){
        SQLiteDatabase db = this.getWritableDatabase(); //<<<<<<<<<< ADDED
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()){
            res.close();
            return true;
        }
        res.close();
        return false;
    }

    public String createQuote(String code,String name,String gst,String qty,String mrp)
    {

        if (verifyData("tbl_quote", name))
        {
            return String.valueOf(0);
        }


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("code",code);
        contentValues.put("name",name);
        contentValues.put("gst",gst);
        contentValues.put("qty",qty);
        contentValues.put("mrp",mrp);
       float res =  sqLiteDatabase.insert("tbl_quote",null,contentValues);

       if (res==-1)
           return "failed";
       else
           return "Successfully Added";

    }

    public Cursor readalldata()
    {
        SQLiteDatabase database =this.getWritableDatabase();
        String qry = "select * from tbl_quote";
        Cursor cursor = database.rawQuery(qry,null);
        return cursor;
    }
    public void deleteAll()
    {
        SQLiteDatabase db =this.getWritableDatabase();

        //SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db.execSQL("delete from tbl_quote");
        db.close();
    }
    public void deleteItem(String get_ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "tbl_quote" + " WHERE name = '" + get_ID + "'");
        db.close();

    }
}
