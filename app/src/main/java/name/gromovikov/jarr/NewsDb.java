package name.gromovikov.jarr;

import android.content.ContentValues;
import android.content.Context;
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
        public static final String COLUMN_NAME_ENTRY_ID = "id";
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
                    + COMMA_SEP + Entry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_TITLE + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_LINK + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_BRIEF + TEXT_TYPE
                    + COMMA_SEP + Entry.COLUMN_NAME_TEXT + TEXT_TYPE
                    + COMMA_SEP + "UNIQUE " + Entry.COLUMN_NAME_LINK
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

            // Insert the new row, on non-uniqe Link or other error return -1
            long newRowId;
            newRowId = db.insert(
                    Entry.COLUMN_NAME_TITLE,
                    Entry.COLUMN_NAME_LINK,
                    values);


        }

    }

    public class Helper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

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