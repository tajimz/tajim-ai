package com.tajimz.tajimai.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDB extends SQLiteOpenHelper {
    public SqliteDB(Context context) {
        super(context, "sqlite_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create personalities table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS personalities (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL" +
                        ");"
        );

        // Create categories table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS categories (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL" +
                        ");"
        );

        // Create description table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS description (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "fallback_name TEXT," +
                        "category_fk INTEGER," +
                        "value TEXT," +
                        "person_id INTEGER," +
                        "FOREIGN KEY(category_fk) REFERENCES categories(id)," +
                        "FOREIGN KEY(person_id) REFERENCES personalities(id)" +
                        ");"
        );
    }

    public String getDescriptionByPath(SQLiteDatabase db, String path) {
        String[] parts = path.split("\\.");
        if (parts.length < 3 || parts.length > 4) return null;

        String personId = parts[0];
        String categoryName = parts[1];
        String descriptionName = parts[2];
        String fallbackName = (parts.length == 4) ? parts[3] : null;

        String query = "SELECT d.value, d.fallback_name " +
                "FROM description d " +
                "JOIN categories c ON d.category_fk = c.id " +
                "WHERE d.person_id = ? AND c.name = ? AND d.name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{personId, categoryName, descriptionName});
        String result;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                String fallback = cursor.getString(cursor.getColumnIndexOrThrow("fallback_name"));

                if ((value == null || value.trim().isEmpty()) && fallbackName != null) {
                    result = fallbackName + ": " + (fallback != null ? fallback : "I want to keep it secret");
                } else if (value == null || value.trim().isEmpty()) {
                    result = "I want to keep it secret";
                } else {
                    result = descriptionName + ": " + value;
                }
            } else {
                result = "I don't have that Information";
            }
            cursor.close();
        } else {
            result = "I don't have that Information";
        }

        return result;
    }

    // 1. Insert personality
    public long addPersonality(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return db.insert("personalities", null, cv);
    }

    // 2. Insert category
    public long addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return db.insert("categories", null, cv);
    }

    // 3. Insert description
    public long addDescription(String name, String fallbackName, int categoryId, int personId, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("fallback_name", fallbackName);
        cv.put("category_fk", categoryId);
        cv.put("person_id", personId);
        cv.put("value", value);
        return db.insert("description", null, cv);
    }
    // Delete personality and all related descriptions
    public void deletePersonality(int personId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Delete all descriptions related to this personality
            db.delete("description", "person_id = ?", new String[]{String.valueOf(personId)});

            // Delete the personality itself
            db.delete("personalities", "id = ?", new String[]{String.valueOf(personId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
