package com.example.guruapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager_Portfolio(
        context: Context,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int)
    :SQLiteOpenHelper(context, name, factory, version){
    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL("CREATE TABLE if not exists ${databaseName} (image blob, title text, content text, color text, startDate text, lastDate text);")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }
}