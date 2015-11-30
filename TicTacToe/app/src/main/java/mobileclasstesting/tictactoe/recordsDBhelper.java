package mobileclasstesting.tictactoe;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class recordsDBhelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_FILENAME = "records.db";
    public static final String TABLE_NAME = "Records";

    public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "(" +
            "UserName text not null," +
            "SPWins integer not null," +
            "SPDraws integer not null," +
            "SPLosses integer not null," +
            "MPWins integer not null," +
            "MPDraws integer not null," +
            "MPLosses integer not null" +
            ")";

    public static final String DROP_STATEMENT = "DROP TABLE " + TABLE_NAME;

    public recordsDBhelper(Context context){

        super(context, DATABASE_FILENAME, null, DATABASE_VERSION);

    }

    //automatically start with DefaultUser 0 0 0 0 0 0
    public void onCreate(SQLiteDatabase database){

        database.execSQL(CREATE_STATEMENT);

        ContentValues values = new ContentValues();
        values.put("UserName", "DefaultUser");
        values.put("SPWins", 0);
        values.put("SPDraws", 0);
        values.put("SPLosses", 0);
        values.put("MPWins", 0);
        values.put("MPDraws", 0);
        values.put("MPLosses", 0);

        database.insert(TABLE_NAME, null, values);

    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){

        database.execSQL(DROP_STATEMENT);
        database.execSQL(CREATE_STATEMENT);

    }



    public String[] getRecord(){

        String result[] = new String[7];

        SQLiteDatabase database = this.getWritableDatabase();

        String[] columns = new String[]{"UserName", "SPWins", "SPDraws", "SPLosses", "MPWins", "MPDraws", "MPLosses"};
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, new String[]{});

        cursor.moveToFirst();

        result[0] = cursor.getString(0);

        for(int index = 1; index < result.length; index++){

            result[index] = "" + cursor.getInt(index);

        }

        return result;

    }


    public int updateRecord(String[] records){

        int success = 0;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("SPWins", records[0]);
        values.put("SPDraws", records[1]);
        values.put("SPLosses", records[2]);
        values.put("MPWins", records[3]);
        values.put("MPDraws", records[4]);
        values.put("MPLosses", records[5]);

        int rowsAffected = database.update(TABLE_NAME, values, "", new String[]{});

        if(rowsAffected == 0)
            success = 1;

        return success;

    }

    public int updateUsername(String username){

        int success = 0;

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("UserName", username);

        int rowsAffected = database.update(TABLE_NAME, values, "", new String[]{});

        if(rowsAffected == 0)
            success = 1;

        return success;

    }


}
