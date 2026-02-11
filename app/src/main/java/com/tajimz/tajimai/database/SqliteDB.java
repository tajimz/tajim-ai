package com.tajimz.tajimai.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



    public String getDescriptionByPath(String path) {
        // Expecting format: "personId.categoryName.descriptionName"
        String[] parts = path.split("\\.");
        if (parts.length != 3) return null;

        String personId = parts[0];
        String categoryName = parts[1];
        String descriptionName = parts[2];

        SQLiteDatabase db = this.getReadableDatabase();
        String result = "no_info";

        String query = "SELECT d.value, d.fallback_name " +
                "FROM description d " +
                "JOIN categories c ON d.category_fk = c.id " +
                "WHERE d.person_id = ? AND c.name = ? AND (d.name = ? OR d.fallback_name = ?) " +
                "LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{personId, categoryName, descriptionName, descriptionName});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                String fallback = cursor.getString(cursor.getColumnIndexOrThrow("fallback_name"));
                result = (value != null && !value.isEmpty()) ? value : fallback;
            }
            cursor.close();
        }

        return result;
    }


    public JSONArray getAllDescriptionByPathAsJson(String path) {
        // Expecting format: "personId.categoryName"
        String[] parts = path.split("\\.");
        if (parts.length != 3) return new JSONArray(); // return empty JSON array

        String personId = parts[0];
        String categoryName = parts[1];
        String catName = parts[2]; //not needed, just taking

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT d.name, d.value, d.fallback_name " +
                "FROM description d " +
                "JOIN categories c ON d.category_fk = c.id " +
                "WHERE d.person_id = ? AND c.name = ?";

        Cursor cursor = db.rawQuery(query, new String[]{personId, categoryName});
        JSONArray jsonArray = new JSONArray();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
                String fallback = cursor.getString(cursor.getColumnIndexOrThrow("fallback_name"));
                String finalValue = (value != null && !value.isEmpty()) ? value : fallback;

                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", name);
                    obj.put("value", finalValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jsonArray.put(obj);
            }
            cursor.close();
        }

        return jsonArray;
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
    public long getCategoryId(String name) {
        Cursor cursor = getReadableDatabase().query(
                "categories",
                new String[]{"id"},
                "name = ?",
                new String[]{name},
                null, null, null
        );
        cursor.moveToFirst();
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        cursor.close();
        return id;
    }



    // 3. Insert description
    public long addDescription(String name, String fallbackName, long categoryId, long personId, String value) {
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

    public void seedDefaultPersonality(){
        addPersonality("Tajim Ai");

        addCategory("basic_info");
        addCategory("education_profession");
        addCategory("interests_lifestyle");
        addCategory("contact_socials");
        addCategory("health_fitness");

        addDescription("name", "name", 1, 1, "TR Tajim");
        addDescription("gender", "gender", 1, 1, "Teenager");
        addDescription("date_of_birth", "dob", 1, 1, "2008-08-18");
        addDescription("age", "age", 1, 1, "17");
        addDescription("nationality", "country", 1, 1, "Bangladeshi");
        addDescription("relationship_status", "relationship", 1, 1, "single");
        addDescription("siblings", "family", 1, 1, "elder brother (Tamim) and younger sister (Tuba)");
        addDescription("blood_group", "blood", 1, 1, "B+");
        addDescription("language_spoken", "languages", 1, 1, "English, Bangla");

        addDescription("education", "study", 2, 1, "High School (Class 10, A Section, Roll 17)");
        addDescription("school", "institution", 2, 1, "Rangpur Zilla School");
        addDescription("profession", "job", 2, 1, "Student / Programmer");
        addDescription("skills", "tech_skills", 2, 1, "Java, Arduino, Web Development, PHP, Python, C++, SQL, Android Development");
        addDescription("certifications", "achievements", 2, 1, "2nd Runner Up At Regional Programming Contest BCC, Rangpur, 2025; Nationalist at National High School Programming Contest");
        addDescription("projects_completed", "projects", 2, 1, "Android Chat App, Portfolio Website");

        addDescription("hobbies", "activities", 3, 1, "Coding, Tech Research, Gaming");
        addDescription("interests", "focus_area", 3, 1, "Android Apps, Linux, AI Development");
        addDescription("favorite_music", "music", 3, 1, "English Pop, Bangla Songs");
        addDescription("favorite_movies", "movies", 3, 1, "I don't watch movies");
        addDescription("favorite_books", "books", 3, 1, "Jotsna o Jononir Golpo by Humayun Ahmed");
        addDescription("favorite_foods", "foods", 3, 1, "No Fascination about foods");
        addDescription("sports_played", "sports", 3, 1, "I don't like Football, I can play Badminton");
        addDescription("travel_interest", "travel", 3, 1, "moderate");

        addDescription("email", "mail", 4, 1, "com.tajim@email.com");
        addDescription("phone", "mobile", 4, 1, "+8801909131512");
        addDescription("facebook", "fb", 4, 1, "facebook.com/trtajim");
        addDescription("instagram", "insta", 4, 1, "instagram.com/trtajim");
        addDescription("twitter", "x", 4, 1, "x.com/trtajim");
        addDescription("linkedin", "linkedin", 4, 1, "linkedin.com/in/tajimz");
        addDescription("github", "github", 4, 1, "github.com/tajimz");
        addDescription("websites", "portfolio", 4, 1, "https://tajimz.github.io");

        addDescription("height_cm", "height", 5, 1, "166");
        addDescription("weight_kg", "weight", 5, 1, "51");
        addDescription("blood_group", "blood", 5, 1, "B+");
        addDescription("sleep_schedule", "sleep", 5, 1, "12 PM");
        addDescription("allergies", "allergy", 5, 1, "");
        addDescription("fitness_activities", "exercise", 5, 1, "Walking");



    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS personalities");
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS description");
        onCreate(db);
    }
}
