package com.tier5.redeemar.RedeemarConsumerApp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tier5 on 2/9/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "DatabaseHelper";

    // Database Name
    public static final String DATABASE_NAME = "redeemar.db";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Users table name

    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_BRANDS = "brands";
    private static final String TABLE_OFFERS = "offers";
    private static final String TABLE_MY_OFFERS = "myoffers";

    private static final String KEY_ID = "id";
    private static final String KEY_PARENT_ID = "parent_id";
    private static final String KEY_CAT_NAME = "cat_name";
    private static final String KEY_CAT_STATUS = "status";
    private static final String KEY_CAT_VISIBILITY = "visibility";


    private static final String KEY_CAMPAIGN_ID = "campaign_id";
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_SUBCAT_ID = "subcat_id";
    private static final String KEY_OFFER_DESCRIPTION = "offer_description";
    private static final String KEY_WHAT_YOU_GET = "what_you_get";
    private static final String KEY_MORE_INFO = "more_information";
    private static final String KEY_SETTINGS_VAL = "settings_val";
    private static final String KEY_PRICE_RANGE_ID = "price_range_id";
    private static final String KEY_IMAGE_NAME = "offer_image";
    private static final String KEY_IMAGE_LARGE_PATH = "offer_large_image_path";
    private static final String KEY_IMAGE_MEDIUM_PATH = "offer_medium_image_path";
    private static final String KEY_IMAGE_SMALL_PATH = "offer_image_path";
    private static final String KEY_PRICE = "price";
    private static final String KEY_RETAIL = "pay";
    private static final String KEY_VALUE_CALCULATE = "value_calculate";
    private static final String KEY_VALUE_TEXT = "value_text";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_VALIDATE_AFTER = "validate_after";
    private static final String KEY_VALIDATE_WITHIN = "validate_within";
    private static final String KEY_ON_DEMAND = "on_demand";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PUBLISHED = "published";
    private static final String KEY_CREATED_BY = "created_by";

    // Users Table Columns names

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();



    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PARENT_ID+ " INTEGER,"
                + KEY_CAT_NAME + " TEXT,"
                + KEY_CAT_STATUS + " INTEGER,"
                + KEY_CAT_VISIBILITY+ " INTEGER)";

        db.execSQL(CREATE_CATEGORIES_TABLE);


        String CREATE_OFFERS_TABLE = "CREATE TABLE " + TABLE_OFFERS + "("
                + KEY_CAMPAIGN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CAT_ID+ " INTEGER,"
                + KEY_SUBCAT_ID + " INTEGER,"
                + KEY_OFFER_DESCRIPTION + " TEXT,"
                + KEY_WHAT_YOU_GET+ " TEXT,"
                + KEY_MORE_INFO+ " TEXT,"
                + KEY_SETTINGS_VAL+ " TEXT,"
                + KEY_PRICE_RANGE_ID+ " TEXT,"
                + KEY_IMAGE_NAME+ " TEXT,"
                + KEY_IMAGE_LARGE_PATH+ " TEXT,"
                + KEY_IMAGE_MEDIUM_PATH+ " TEXT,"
                + KEY_IMAGE_SMALL_PATH+ " TEXT,"
                + KEY_PRICE+ " REAL,"
                + KEY_RETAIL+ " REAL,"
                + KEY_VALUE_CALCULATE+ " INTEGER,"
                + KEY_VALUE_TEXT+ " TEXT,"
                + KEY_START_DATE+ " TEXT,"
                + KEY_END_DATE+ " TEXT,"
                + KEY_VALIDATE_AFTER+ " INTEGER,"
                + KEY_VALIDATE_WITHIN+ " INTEGER,"
                + KEY_ON_DEMAND+ " INTEGER,"
                + KEY_STATUS+ " INTEGER,"
                + KEY_PUBLISHED+ " INTEGER,"
                + KEY_CREATED_BY+ " INTEGER)";

        db.execSQL(CREATE_OFFERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_OFFERS);


    }

    public boolean addCategory(Category category) {

        boolean exists = categoryExists(category.getId());

        if(!exists) {



            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ID, category.getId());
            contentValues.put(KEY_PARENT_ID, category.getParentId());
            contentValues.put(KEY_CAT_NAME, category.getCatName());
            contentValues.put(KEY_CAT_STATUS, category.getStatus());
            contentValues.put(KEY_CAT_VISIBILITY, category.getVisibility());

            long res = db.insert(TABLE_CATEGORIES, null, contentValues);
            db.close();

            if (res == -1)
                return false;
            else {
                Log.d(LOGTAG, "Category "+category.getCatName()+" added");
                return true;
            }

        }
        else {
            Log.d(LOGTAG, "Category "+category.getCatName()+" exists");
            return false;
        }
    }

    public boolean categoryExists(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  COUNT("+KEY_ID+") AS counter FROM " + TABLE_CATEGORIES + " WHERE "+KEY_ID+" = "+id;
        Log.d(LOGTAG, "SQL: "+selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);


		/*Cursor cursor = db.query(TABLE_USERS, new String[] {"COUNT("+KEY_USER_ID+") AS COUNT_GOALS"},
				"UPPER("+USER_KEY_NAME+")" + "=?",
				new String[] {"UPPER("+user_name+")"}, null, null, null, null);*/

        int cnt = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cnt = cursor.getInt(0);
                //Log.d("DEBUG", "User Counter: "+cnt);
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        if(cnt == 0)
            return false;
        else
            return true;

    }

    public Category getCategory(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        //List<Category> listCategories = new ArrayList<Category>();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE "+KEY_ID+" = "+id+"";
        Log.d(LOGTAG, "SQL: "+selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        Category cat = new Category();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cat.setId(cursor.getInt(0));
                cat.setParentId(cursor.getInt(1));
                cat.setCatName(cursor.getString(2));
                cat.setStatus(cursor.getInt(3));
                cat.setVisibility(cursor.getInt(4));

            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return cat;
    }

    public List<Category> getCategories(int pid) {

        SQLiteDatabase db = this.getWritableDatabase();
        List<Category> listCategories = new ArrayList<Category>();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE "+KEY_PARENT_ID+" = "+pid+" order by "+KEY_CAT_NAME+"";
        Log.d(LOGTAG, "SQL: "+selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        int cnt = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(0));
                cat.setParentId(cursor.getInt(1));
                cat.setCatName(cursor.getString(2));
                cat.setStatus(cursor.getInt(3));
                cat.setVisibility(cursor.getInt(4));
                listCategories.add(cat);

            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        return listCategories;
    }



    public int countCategories(int id) {
        // Select All Query
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  COUNT("+KEY_ID+") AS counter FROM " + TABLE_CATEGORIES + " WHERE "+KEY_PARENT_ID+"="+id;
        Log.d("DEBUG", "SQL: "+selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);


		/*Cursor cursor = db.query(TABLE_USERS, new String[] {"COUNT("+KEY_USER_ID+") AS COUNT_GOALS"},
				"UPPER("+USER_KEY_NAME+")" + "=?",
				new String[] {"UPPER("+user_name+")"}, null, null, null, null);*/

        int cnt = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cnt = cursor.getInt(0);
                //Log.d("DEBUG", "User Counter: "+cnt);
            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();

        // return user list
        return cnt;

    }
}
