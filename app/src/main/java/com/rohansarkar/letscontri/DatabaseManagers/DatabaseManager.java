package com.rohansarkar.letscontri.DatabaseManagers;

/**
 * Created by rohan on 17/5/15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rohansarkar.letscontri.CustomData.DataExpense;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    String LOG_TAG= "DatabaseManager Logs";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_NAME = "friends_name";
    public static final String KEY_TITLE = "title";
    public static final String KEY_BINARY = "binary_equivalent";

    private static final String DATABASE_NAME = "HelpMeTrip";
    private static final String DATABASE_TABLE = "TripDetails";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper{

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_AMOUNT + " DOUBLE NOT NULL, " +
                            KEY_NAME + " TEXT NOT NULL, " +
                            KEY_TITLE + " TEXT NOT NULL, " +
                            KEY_BINARY + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DatabaseManager(Context c){
        ourContext = c;
    }

    public DatabaseManager open() throws SQLException{
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }
    public void close(){
        ourHelper.close();
    }

    public long createEntry(DataExpense data) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_AMOUNT, data.amount);
        cv.put(KEY_NAME, data.name);
        cv.put(KEY_TITLE, data.title);
        cv.put(KEY_BINARY, data.binary);
        Log.d("DatabaseManager", "Created Entry.");
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<DataExpense> getExpenses(){
        String[] columns = new String[]{ KEY_ROWID, KEY_AMOUNT, KEY_NAME, KEY_TITLE, KEY_BINARY};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        ArrayList<DataExpense> expenses= new ArrayList<>();
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iTitle = c.getColumnIndex(KEY_TITLE);
        int iName = c.getColumnIndex(KEY_NAME);
        int iAmount = c.getColumnIndex(KEY_AMOUNT);
        int iBinary = c.getColumnIndex(KEY_BINARY);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            DataExpense data = new DataExpense(c.getInt(iRow), c.getString(iTitle), c.getString(iName),
                    c.getDouble(iAmount), c.getString(iBinary));
            expenses.add(data);
        }

        return expenses;
    }

    public void deleteEntry(long row_key) throws SQLException{
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + row_key, null);
    }

    public long updateEntry(DataExpense data) {
        try {
            ContentValues cvUpdate = new ContentValues();
            cvUpdate.put(KEY_TITLE, data.title);
            cvUpdate.put(KEY_NAME, data.name);
            cvUpdate.put(KEY_AMOUNT, data.amount);
            cvUpdate.put(KEY_BINARY, data.binary);
            Log.d(LOG_TAG, "Updates");
            return ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + "=" + data.rowId, null);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error encountered");
        }
        return 0;
    }

    public List<String> getNames(){
        String[] columns = new String[]{ KEY_ROWID, KEY_AMOUNT, KEY_NAME, KEY_TITLE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        List<String> result= new ArrayList<String>();
        int iName = c.getColumnIndex(KEY_NAME);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            result.add(c.getString(iName));

        return result;
    }

    public void deleteAll(){
        ourDatabase.execSQL("delete from "+ DATABASE_TABLE);
        ourDatabase.execSQL("vacuum");
    }
}