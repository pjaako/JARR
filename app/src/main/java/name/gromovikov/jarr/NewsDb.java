package name.gromovikov.jarr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.List;

/**
 * Created by pja on 12.11.2015.
 * based on http://developer.android.com/intl/ru/training/basics/data-storage/databases.html
 */
public class NewsDb {

    private Helper helper;
    private Context context;

    NewsDb(Context context){
        this.context = context;
        this.helper = new Helper(context);
    }
    /* Inner class that defines the table contents */
    public abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "entries";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_BRIEF = "brief";
        public static final String COLUMN_NAME_TEXT = "text";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT"
                    + COMMA_SEP + Entry.COLUMN_NAME_TITLE + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_LINK + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_BRIEF + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_TEXT + TEXT_TYPE
                    + COMMA_SEP + "UNIQUE (" + Entry.COLUMN_NAME_LINK + ")"
            +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    public void addFromFeed(List<NewsEntry> feed){
        for (NewsEntry postMeta : feed){
            // Gets the data repository in write mode
            SQLiteDatabase db = helper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(Entry.COLUMN_NAME_TITLE, postMeta.getTitle());
            values.put(Entry.COLUMN_NAME_LINK, postMeta.getLink());

            // INSERT the new row, return new id
            // OR IGNORE insertion and return existing ID if link exists
            long newRowId;
            newRowId = db.insertWithOnConflict(
                    Entry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);


        }

    }

    public boolean isEmpty (){
        SQLiteDatabase db = helper.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, Entry.TABLE_NAME)<1;
    }


    public Cursor findTextlessNews (){
        SQLiteDatabase db = helper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Entry.COLUMN_NAME_LINK,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Entry._ID + " DESC";
        //select entries with text not loaded or loaded with an error
        //TODO - we have to stop trying to re-load errorneus posts after some attempts
        //since we are going to end up with huge re-load operation for posts already deleted
        //String selection = "? is null or ? = '' or ? ='" + PostParser.POST_TEXT_ERROR+"'";
        String selection = Entry.COLUMN_NAME_TEXT+" is null or " + Entry.COLUMN_NAME_TEXT + " = ''";
        //String[] selectionArgs = {Entry.COLUMN_NAME_TEXT,Entry.COLUMN_NAME_TEXT,Entry.COLUMN_NAME_TEXT};
        Cursor cursor = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                //selectionArgs,                            // The values for the WHERE clause
                null,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;

    }

    public Cursor getMetas () {
        SQLiteDatabase db = helper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Entry._ID,
                Entry.COLUMN_NAME_LINK,
                Entry.COLUMN_NAME_TITLE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = Entry._ID + " DESC";
        //select entries with text not loaded or loaded with an error
        //TODO - we have to stop trying to re-load errorneus posts after some attempts
        //since we are going to end up with huge re-load operation for posts already deleted
        Cursor cursor = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                              // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return cursor;
    }

    public void addTextForLink (String text, String link){
        SQLiteDatabase db = helper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_TEXT, text);
        String whereClause = Entry.COLUMN_NAME_LINK + "='" + link + "'";
        // UPDATE the row, containing a link
        // OR IGNORE update if no such row in the table
        long updatedRowsCount;
        updatedRowsCount = db.updateWithOnConflict(Entry.TABLE_NAME, values, whereClause,
                null,SQLiteDatabase.CONFLICT_IGNORE); //returns 1 since link is unique and exists.



    }



    public class Helper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "news.db";

        public Helper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}