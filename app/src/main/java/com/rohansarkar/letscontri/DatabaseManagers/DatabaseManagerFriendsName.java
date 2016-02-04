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
import java.util.ArrayList;
import java.util.List;

public class DatabaseManagerFriendsName {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "friends_name";

    private static final String DATABASE_NAME = "HelpMeTripFriends";
    private static final String DATABASE_TABLE = "FriendsName";
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
                            KEY_NAME + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    public DatabaseManagerFriendsName(Context c){
        ourContext = c;
    }

    public DatabaseManagerFriendsName open() throws SQLException{
        ourHelper = new DbHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();

        return this;
    }

    public void close(){
        ourHelper.close();
    }

    public long createEntry(String name) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }

    public ArrayList<String> getData() {
        ArrayList<String> friendList= new ArrayList<String>();
        String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        int iName = c.getColumnIndex(KEY_NAME);

        //can have an issue here
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            friendList.add(c.getString(iName));
        }

        return friendList;
    }

    public String getName(long l) throws SQLException{
        String[] columns = new String[]{ KEY_ROWID, KEY_NAME, };
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "=" + l, null, null, null, null);
        if (c != null){
            c.moveToFirst();
            String name = c.getString(2);
            return name;
        }
        return null;
    }
    public void updateEntry(long row, String name) throws SQLException{
        ContentValues cvUpdate = new ContentValues();
        cvUpdate.put(KEY_NAME, name);
        ourDatabase.update(DATABASE_TABLE, cvUpdate, KEY_ROWID + "=" + row, null);
    }

    public void deleteEntry(long lRow1) throws SQLException{
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + lRow1, null);
    }

    public void deleteAll() throws SQLException{

        ourDatabase.execSQL("delete from "+ DATABASE_TABLE);
        ourDatabase.execSQL("vacuum");
    }

    public long size(){
        ArrayList<String> friendList= new ArrayList<String>();
        String[] columns = new String[]{ KEY_ROWID, KEY_NAME};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
        int iName = c.getColumnIndex(KEY_NAME);

        //can have an issue here
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            friendList.add(c.getString(iName));
        }

        return friendList.size();
    }

    public boolean isEmpty(){
        Cursor cursor = ourDatabase.rawQuery("SELECT count(*) FROM " + DATABASE_TABLE, null);
        if (cursor.getCount() > 0)
            return false;
        return true;
    }
}