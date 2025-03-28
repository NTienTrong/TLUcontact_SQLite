package com.vinnorman.sqlitetest

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) { // Sửa constructor

    companion object {
        private const val DATABASE_NAME = "ContactDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "contacts"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME + " TEXT," + COLUMN_PHONE + " TEXT" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addContact(name: String, phone: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_PHONE, phone)
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun updateContact(oldName: String, newName: String, newPhone: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, newName) // Update name
        values.put(COLUMN_PHONE, newPhone)
        val result = db.update(TABLE_NAME, values, "$COLUMN_NAME=?", arrayOf(oldName))
        db.close()
        return result
    }

    fun deleteContact(name: String): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NAME=?", arrayOf(name))
        db.close()
        return result
    }

    fun getAllContacts(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
}