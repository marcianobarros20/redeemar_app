package com.tier5.redeemar.RedeemarConsumerApp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Windows on 25-02-2015.
 */
public class DBOffers {
    //public static final int BOX_OFFICE = 0;
    //public static final int UPCOMING = 1;

    private static final String LOGTAG = "DBOffers";

    public static final int ALL_OFFERS = 0;
    public static final int MY_OFFERS = 1;
    public static final int BRAND_OFFERS = 2;

    private OffersHelper mHelper;
    private SQLiteDatabase mDatabase;


    public DBOffers(Context context) {
        mHelper = new OffersHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertOffers(int table, ArrayList<Offer> listOffers, boolean clearPrevious) {
        if (clearPrevious) {
            deleteOffers(table);
        }


        //create a sql prepared statement
        String sql = "";

        switch(table) {

            case ALL_OFFERS:
                sql = "INSERT INTO " + OffersHelper.TABLE_OFFERS  + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            case MY_OFFERS:
                sql = "INSERT INTO " + OffersHelper.TABLE_MY_OFFERS  + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";


        };


        //compile the statement and start a transaction
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listOffers.size(); i++) {
            Offer currentOffer = listOffers.get(i);
            statement.clearBindings();
            //for a given column index, simply bind the data to be put inside that index
            statement.bindString(2, currentOffer.getOfferId());
            //statement.bindString(3, currentOffer.getReleaseDateTheater() == null ? -1 : currentOffer.getReleaseDateTheater().getTime());
            statement.bindString(3, currentOffer.getOfferDescription());
            statement.bindString(4, currentOffer.getWhatYouGet());
            statement.bindString(5, currentOffer.getMoreInformation());
            statement.bindString(6, currentOffer.getSettingsVal());
            statement.bindString(7, currentOffer.getPriceRangeId());
            statement.bindString(8, currentOffer.getImageName());
            statement.bindString(9, currentOffer.getImageUrl());
            statement.bindString(10, currentOffer.getAddress());
            statement.bindDouble(11, currentOffer.getPrice());
            statement.bindDouble(12, currentOffer.getPayValue());
            statement.bindDouble(13, currentOffer.getRetailvalue());
            statement.bindDouble(14, currentOffer.getDiscount());
            statement.bindLong(15, currentOffer.getValueCalculate());
            statement.bindLong(16, currentOffer.getExpiredInDays());

            statement.execute();
        }
        //set the transaction as successful and end the transaction
        Log.d(LOGTAG, "inserting entries " + listOffers.size() + new Date(System.currentTimeMillis()));
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<Offer> readOffers(int table) {
        ArrayList<Offer> listOffers = new ArrayList<>();


        //get a list of columns to be retrieved, we need all of them
        String[] columns = {OffersHelper.COLUMN_ID,
                OffersHelper.COLUMN_CAMPAIGN_ID,
                OffersHelper.COLUMN_USER_ID,
                OffersHelper.COLUMN_CAT_ID,
                OffersHelper.COLUMN_SUBCAT_ID,
                OffersHelper.COLUMN_OFFER_DESCRIPTION,
                OffersHelper.COLUMN_WHAT_YOU_GET,
                OffersHelper.COLUMN_MORE_INFO,
                OffersHelper.COLUMN_SETTINGS_VAL,
                OffersHelper.COLUMN_PRICE_RANGE_ID,
                OffersHelper.COLUMN_IMAGE_NAME,
                OffersHelper.COLUMN_IMAGE_URL,
                OffersHelper.COLUMN_ADDRESS,
                OffersHelper.COLUMN_PRICE,
                OffersHelper.COLUMN_PAY_VALUE,
                OffersHelper.COLUMN_RETAIL_VALUE,
                OffersHelper.COLUMN_DISCOUNT,
                OffersHelper.COLUMN_VALUE_CALCULATE,
                OffersHelper.COLUMN_EXP_IN_DAYS
        };

        Cursor cursor;

        switch(table)  {
            case MY_OFFERS:
                cursor = mDatabase.query(OffersHelper.TABLE_MY_OFFERS, columns, null, null, null, null, null);
            default:
                cursor = mDatabase.query(OffersHelper.TABLE_OFFERS, columns, null, null, null, null, null);

        }
        //cursor = mDatabase.query((table == ALL_OFFERS ? OffersHelper.TABLE_OFFERS : OffersHelper.TABLE_UPCOMING), columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(LOGTAG, "Loading entries " + cursor.getCount() + new Date(System.currentTimeMillis()));
            do {

                //create a new Offer object and retrieve the data from the cursor to be stored in this Offer object
                Offer Offer = new Offer();
                //each step is a 2 part process, find the index of the column first, find the data of that column using
                //that index and finally set our blank Offer object to contain our data


                /*Offer.setTitle(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_TITLE)));
                long releaseDateMilliseconds = cursor.getLong(cursor.getColumnIndex(OffersHelper.COLUMN_RELEASE_DATE));
                Offer.setReleaseDateTheater(releaseDateMilliseconds != -1 ? new Date(releaseDateMilliseconds) : null);
                Offer.setAudienceScore(cursor.getInt(cursor.getColumnIndex(OffersHelper.COLUMN_AUDIENCE_SCORE)));
                Offer.setSynopsis(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_SYNOPSIS)));
                Offer.setUrlThumbnail(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_URL_THUMBNAIL)));
                Offer.setUrlSelf(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_URL_SELF)));
                Offer.setUrlCast(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_URL_CAST)));
                Offer.setUrlReviews(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_URL_REVIEWS)));
                Offer.setUrlSimilar(cursor.getString(cursor.getColumnIndex(OffersHelper.COLUMN_URL_SIMILAR)));
                //add the Offer to the list of Offer objects which we plan to return*/




                listOffers.add(Offer);
            }
            while (cursor.moveToNext());
        }
        return listOffers;
    }

    public void deleteOffers(int table) {
        //mDatabase.delete((table == ALL_OFFERS ? OffersHelper.TABLE_OFFERS : OffersHelper.TABLE_UPCOMING), null, null);

        switch(table)  {
            case MY_OFFERS:
                mDatabase.delete(OffersHelper.TABLE_MY_OFFERS, null, null);
            default:
                mDatabase.delete(OffersHelper.TABLE_OFFERS, null, null);

        }

    }

    private static class OffersHelper extends SQLiteOpenHelper {
        public static final String TABLE_OFFERS = "offers";
        public static final String TABLE_MY_OFFERS = "my_offers";

        // Field names
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CAMPAIGN_ID = "campaign_id";
        public static final String COLUMN_USER_ID = "campaign_id";
        public static final String COLUMN_CAT_ID = "cat_id";
        public static final String COLUMN_SUBCAT_ID = "subcat_id";
        public static final String COLUMN_OFFER_DESCRIPTION = "offer_description";
        public static final String COLUMN_WHAT_YOU_GET = "what_you_get";
        public static final String COLUMN_MORE_INFO = "more_infomation";
        public static final String COLUMN_SETTINGS_VAL = "settings_val";
        public static final String COLUMN_PRICE_RANGE_ID = "price_range_id";
        public static final String COLUMN_IMAGE_NAME = "inmage_name";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_PAY_VALUE = "pay_value";
        public static final String COLUMN_RETAIL_VALUE = "retail_value";
        public static final String COLUMN_DISCOUNT = "discount";
        public static final String COLUMN_VALUE_CALCULATE = "value_calculate";
        public static final String COLUMN_EXP_IN_DAYS = "expired_in_days";


        private static final String CREATE_TABLE_OFFERS = "CREATE TABLE " + TABLE_OFFERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CAMPAIGN_ID + " INT," +
                COLUMN_USER_ID + " INT," +
                COLUMN_CAT_ID + " INTEGER," +
                COLUMN_SUBCAT_ID + " INTEGER," +
                COLUMN_OFFER_DESCRIPTION + " TEXT," +
                COLUMN_WHAT_YOU_GET + " TEXT," +
                COLUMN_MORE_INFO + " TEXT," +
                COLUMN_SETTINGS_VAL + " TEXT," +
                COLUMN_PRICE_RANGE_ID + " TEXT," +
                COLUMN_IMAGE_NAME + " TEXT," +
                COLUMN_IMAGE_URL + " TEXT," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_PRICE + " TEXT," +
                COLUMN_PAY_VALUE + " TEXT," +
                COLUMN_RETAIL_VALUE + " TEXT" +
                COLUMN_DISCOUNT + " TEXT," +
                COLUMN_VALUE_CALCULATE + " TEXT," +
                COLUMN_EXP_IN_DAYS + " TEXT" +
                ");";


        private static final String DB_NAME = "redeemar_db";
        private static final int DB_VERSION = 1;
        private Context mContext;

        public OffersHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_OFFERS);

                Log.d(LOGTAG, "create table box office executed");
            } catch (SQLiteException exception) {
                Log.d(LOGTAG, "Exception in creating tables " + exception);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Log.d(LOGTAG, "upgrade table box office executed");
                db.execSQL(" DROP TABLE " + CREATE_TABLE_OFFERS + " IF EXISTS;");

                onCreate(db);
            } catch (SQLiteException exception) {
                Log.d(LOGTAG, "Exception in upgrading tables " + exception + "");
            }
        }
    }
}

