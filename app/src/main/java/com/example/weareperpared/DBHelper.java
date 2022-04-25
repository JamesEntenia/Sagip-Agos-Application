package com.example.weareperpared;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Userdetails (name TEXT primary key, password TEXT,usertype TEXT,contact TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Userdetails");
    }


    public boolean insert (String name,String password,String usertype,String contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name",name);
        contentValues.put("password",password);
        contentValues.put("usertype",usertype);
        contentValues.put("contact",contact);

        long result = db.insert("Userdetails",null, contentValues);

        if(result ==-1){
            return  false;
        }else{
            return true;
        }
    }


    public boolean update (String name,String password,String usertype,String contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("password",password);
        contentValues.put("usertype",usertype);
        contentValues.put("contact",contact);

        Cursor cursor = db.rawQuery("Select * from Userdetails where name = ?", new String[] {name});

        if (cursor.getCount()>0){
            long result = db.update("Userdetails",contentValues,"name=?",new String[]{name});

            if(result ==-1){
                return  false;
            }else{
                return true;
            }
        }else{
             return false;
        }
    }


    public boolean delete (String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Userdetails where name = ?", new String[] {name});

        if (cursor.getCount()>0){
            long result = db.delete("Userdetails","name=?",new String[]{name});

            if(result ==-1){
                return  false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }


    public Cursor getdata (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Userdetails",null);

        return cursor;
    }

}
