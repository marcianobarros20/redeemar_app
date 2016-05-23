package com.vuforia.samples.VuforiaSamples.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 9;

	// Database Name
	private static final String DATABASE_NAME = "redeemer.db";

	// Users table name

	private static final String TABLE_REDEEMER_LOGOS = "redeemer_logo";
	private static final String TABLE_REDEEMER_VIDEOS = "redeemer_logo";

	// Users Table Columns names
	
	
	
	public static final String KEY_LOGO_ID = "id";
	public static final String KEY_TARGET_ID = "target_id";
	public static final String KEY_COMPANY_NAME = "logo_name";
	public static final String KEY_LOGO_TEXT = "logo_text";

	
	
	public DatabaseHandler(Context context) {		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);		
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String CREATE_LOGO_TABLE = "CREATE TABLE " + TABLE_REDEEMER_LOGOS + "("
				+ KEY_LOGO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_TARGET_ID+ " TEXT,"
				+ KEY_COMPANY_NAME + " TEXT,"
				+ KEY_LOGO_TEXT + " TEXT)";

		db.execSQL(CREATE_LOGO_TABLE);


		String CREATE_VIDEO_TABLE = "CREATE TABLE " + TABLE_REDEEMER_VIDEOS + "("
				+ KEY_LOGO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_TARGET_ID+ " TEXT,"
				+ KEY_COMPANY_NAME + " TEXT,"
				+ KEY_LOGO_TEXT + " TEXT)";

		db.execSQL(CREATE_LOGO_TABLE);
		

		
		
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REDEEMER_LOGOS);
		
		// Create tables again
		onCreate(db);
	}

	///////////////////// **** REDEEMER_LOGO **** /////////////////////
	


	public ArrayList<Cursor> getData(String Query){
		//get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query 
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);
		
		
		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);
			

			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });
			
			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0) {

				
				alc.set(0,c);
				c.moveToFirst();
				
				return alc ;
			}
			return alc;
		} catch(SQLException sqlEx){
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		} catch(Exception ex){

			Log.d("printing exception", ex.getMessage());

			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+ex.getMessage() });
			alc.set(1,Cursor2);
			return alc;
			
			
		}

		
	}

	
	////////////////////////////////// Common Usages //////////////////////////////////
	public void deleteSpecificTable(String tableName)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String deleteQuery = "delete from " + tableName;
		//	db.delete(tableName, null, null);
		db.execSQL(deleteQuery);

	}
	
	
	public boolean insertSpecificTable(String tableName, ContentValues contentvalue)
	{

		boolean returnInsertResponce = false;

		SQLiteDatabase db = this.getWritableDatabase();

		try
		{

			// Cursor cursor=db.query(UserProfile.TABLE_NAME, null, null, null,
			// null, null, null);

			db.insert(tableName, null, contentvalue);
			returnInsertResponce = true;
		}
		catch (SQLException e)
		{
			return false;
		}
		db.close();
		return returnInsertResponce;

	}

	
}
