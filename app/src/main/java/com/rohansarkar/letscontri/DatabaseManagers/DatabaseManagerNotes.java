package com.rohansarkar.letscontri.DatabaseManagers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rohansarkar.letscontri.CustomData.DataNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohan on 11/1/16.
 */
public class DatabaseManagerNotes {
    static String LOG_TAG= "DatabaseManagerNotes Logs";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NOTE = "note";

    private static final String DATABASE_NAME = "HelpMeTripNotes";
    private static final String DATABASE_TABLE = "PersonalNotes";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_NOTE + " DOUBLE NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DatabaseManagerNotes(Context c){
        ourContext = c;
    }

    public DatabaseManagerNotes open() throws SQLException {
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }
    public void close(){
        ourHelper.close();
    }

    public long createEntry(String note) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NOTE, note);
        Log.d("DatabaseManager", "Created Entry.");
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<DataNote> getNotes() throws SQLException{
        String[] columns = new String[]{ KEY_ROWID, KEY_NOTE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);

        ArrayList<DataNote> notes= new ArrayList<>();
        int iRow = c.getColumnIndex(KEY_ROWID);
        int iNote = c.getColumnIndex(KEY_NOTE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            DataNote data = new DataNote(c.getInt(iRow), c.getString(iNote));
            notes.add(data);
        }

        return notes;
    }

    public void deleteEntry(long row_key) throws SQLException{
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + row_key, null);
    }

    public long updateEntry(DataNote data) {
        try {
            ContentValues cvUpdate = new ContentValues();
            cvUpdate.put(KEY_NOTE, data.note);
            Log.d(LOG_TAG, "Updates");
            return ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + "=" + data.rowId, null);
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error encountered");
        }
        return 0;
    }

    public List<String> getNames(){
        String[] columns = new String[]{ KEY_ROWID, KEY_NOTE};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        List<String> result= new ArrayList<String>();
        int iName = c.getColumnIndex(KEY_NOTE);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
            result.add(c.getString(iName));

        return result;
    }

    public void deleteAll(){
        ourDatabase.execSQL("delete from "+ DATABASE_TABLE);
        ourDatabase.execSQL("vacuum");
    }
}
