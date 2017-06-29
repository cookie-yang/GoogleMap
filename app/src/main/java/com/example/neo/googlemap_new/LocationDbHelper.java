package com.example.neo.googlemap_new;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neo on 14/06/2017.
 */
public class LocationDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Location.db";
    public static final String TABLE_LOCATION = "location";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_OPENTIME = "opentime";
    public static final String KEY_QUOTA = "quota";


    public static final String TABLE_ACCOUNT = "account";
    public static final String KEY_USERNAME ="username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CARPLATE = "carplate";
    public static final String KEY_PHONENUM = "phonenumber";


    public LocationDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("+ KEY_ID + " INTEGER PRIMARY KEY,"
                +KEY_NAME + " TEXT," + KEY_ADDRESS + " TEXT,"+KEY_OPENTIME+" TEXT,"
                +KEY_QUOTA+" INTEGER," + KEY_LATITUDE + " REAL," + KEY_LONGITUDE + " REAL"+ ")";

        String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("+ KEY_ID + " INTEGER PRIMARY KEY,"
                +KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT,"+KEY_CARPLATE+" TEXT,"
                + KEY_PHONENUM + " TEXT"+ ")";
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_ACCOUNT_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // Create tables again
        onCreate(db);
    }

    public void addLoaction(MYLocation myLocation){
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_NAME,myLocation.getName());
        values.put(KEY_ADDRESS,myLocation.getAddress());
        values.put(KEY_OPENTIME,myLocation.getOpentime());
        values.put(KEY_QUOTA,myLocation.getQuota());
        values.put(KEY_LONGITUDE,myLocation.getLongitude());
        values.put(KEY_LATITUDE,myLocation.getLatitude());

        db.insert(TABLE_LOCATION,null,values);
        db.close();

    }
    public void addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME,account.getUserName());
        values.put(KEY_PASSWORD,account.getPassword());
        values.put(KEY_CARPLATE,account.getCarPlate());
        values.put(KEY_PHONENUM,account.getPhoneNum());

        db.insert(TABLE_ACCOUNT,null,values);
        db.close();

    }
    public boolean verify(String username,String password){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumn = new String[]{KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_CARPLATE, KEY_PHONENUM};
        String whereClause = KEY_USERNAME + " = ? ";
        String[] whereArg = new String[]{username};
        Cursor cursor = db.query(TABLE_ACCOUNT, tableColumn, whereClause, whereArg, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try {
            Account myAccount = new Account(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4));
            cursor.close();
            db.close();
            if (myAccount.getPassword().equals(password))
                    return true;
            else
                return false;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("can not find this account");

        }
        return false;
    }

    public Account getAccount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumn = new String[]{KEY_ID, KEY_USERNAME, KEY_PASSWORD, KEY_CARPLATE, KEY_PHONENUM};
        String whereClause = KEY_USERNAME + " = ? ";
        String[] whereArg = new String[]{username};
        Cursor cursor = db.query(TABLE_ACCOUNT, tableColumn, whereClause, whereArg, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        try {
            Account myAccount = new Account(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4));
            cursor.close();
            db.close();
            return myAccount;
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("can not find this account");

        }
        return null;
    }

    public MYLocation getLocation(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] tableColumn = new String[]{KEY_ID,KEY_NAME,KEY_ADDRESS,KEY_OPENTIME,KEY_QUOTA,KEY_LATITUDE,KEY_LONGITUDE};
        String whereClause = KEY_NAME+" = ? ";
        String[] whereArg = new String[]{name};
        Cursor cursor = db.query(TABLE_LOCATION,tableColumn,whereClause,whereArg,null,null,null,null);
        if(cursor !=null)
            cursor.moveToFirst();
        try {
            MYLocation myLocation = new MYLocation(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
            cursor.close();
            db.close();
            return myLocation;
        }catch (Exception e){
            System.out.println(e);
            System.out.println("can not find this location");

        }
        return null;

    }
    public List<MYLocation>getAllLocations(){
        List<MYLocation>locationList = new ArrayList<MYLocation>();
        String query = "SELECT * FROM "+TABLE_LOCATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do{
                MYLocation myLocation = new MYLocation(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                        Integer.parseInt(cursor.getString(4)), cursor.getString(5), cursor.getString(6));
                locationList.add(myLocation);
            }while (cursor.moveToNext());
        }
        db.close();
        return locationList;
    }
    public int updateLocation(MYLocation myLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,myLocation.getName());
        values.put(KEY_ADDRESS,myLocation.getAddress());
        values.put(KEY_OPENTIME,myLocation.getOpentime());
        values.put(KEY_QUOTA,myLocation.getQuota());
        values.put(KEY_LONGITUDE,myLocation.getLongitude());
        values.put(KEY_LATITUDE,myLocation.getLatitude());

        int index = db.update(TABLE_LOCATION,values,KEY_NAME+" = ? ",new String[]{myLocation.getName()});
        db.close();
        return index;

    }
    public void deleteLocation(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION,KEY_NAME+" = ? ",new String[]{name});
        db.close();
    }
    public int getLocationCount(){
        String query = "SELECT * FROM "+TABLE_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        cursor.close();

        return cursor.getCount();

    }
}
