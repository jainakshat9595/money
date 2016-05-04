package in.jainakshat.money.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.db.tables.HistoryTable;


/**
 * Created by Akshat on 4/29/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Money_DB";

    private static final int DATABASE_VERSION = 1;

    private static DBHelper sInstance;
    private static Context sContext;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null)
            sContext = context;
        sInstance = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        return sInstance;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ContactTable.onCreate(db);
        HistoryTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ContactTable.onUpgrade(db, oldVersion, newVersion);
        HistoryTable.onUpgrade(db, oldVersion, newVersion);
    }
}
