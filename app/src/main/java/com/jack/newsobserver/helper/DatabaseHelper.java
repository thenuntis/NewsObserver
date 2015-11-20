package com.jack.newsobserver.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jack.newsobserver.manager.AlertDialogManager;
import com.jack.newsobserver.manager.GetDataFromHtmlManager;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "newsobserverdb.db";
    public static final int SCHEMA=1;
    public static final String NEWS_TOPICS_TABLE = "topics";
    public static final String TOPICS_CATEGORY_ID_COLUMN = "category_id";
    public static final String TOPICS_LINK_COLUMN = "link";
    public static final String NEWS_CATEGORY_TABLE = "category";
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";

    public static final String CATEGORY_TABLE_CREATE = "create table " + NEWS_CATEGORY_TABLE + "("
            + ID_COLUMN   + " integer primary key autoincrement, "
            + NAME_COLUMN + " text not null" + ")";
    public static final String TOPICS_TABLE_CREATE = "create table " + NEWS_TOPICS_TABLE + "("
            + ID_COLUMN   + " integer primary key autoincrement, "
            + TOPICS_CATEGORY_ID_COLUMN + " integer not null "
            + "REFERENCES " + NEWS_CATEGORY_TABLE + " ("+ID_COLUMN+"), "
            + NAME_COLUMN + " text not null, "
            + TOPICS_LINK_COLUMN + " text not null" + ")";
    private Context ctx;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CATEGORY_TABLE_CREATE);
        db.execSQL(TOPICS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if(!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public Cursor createCursor (String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(query,null);
    }

    public boolean initDataBase(){
        String query = "SELECT name FROM topics";
        Cursor cursor = this.createCursor(query);
        int val = cursor.getCount();
        cursor.close();
        return(val == 0);
    }

    public void fillDataBaseFromUrl(String url) {
        Log.e("-->()<--", "fillbasefromurl method");
        SQLiteDatabase db = this.getWritableDatabase();
        if (new IsNetworkAvailable(ctx).testNetwork()){
            Log.e("-->()<--","net-OK, gotofilldatafromhtml");
            GetDataFromHtmlManager mGetDataFromHtmlManager = new GetDataFromHtmlManager(db);
            mGetDataFromHtmlManager.execute(url);
            Log.e("-->()<--", "filling complete");
        }else{
            new AlertDialogManager().alertDialogShow(ctx);
        }
    }

    public String getStringFromCursor (Cursor cursor,int row,String col) {
        String value;
        if (cursor !=null){
            cursor.moveToPosition(row);
            value = cursor.getString(cursor.getColumnIndex(col));
        }else {
            value = "empty cursor";
        }

        return value;
    }

}
