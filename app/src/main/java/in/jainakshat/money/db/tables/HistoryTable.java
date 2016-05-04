package in.jainakshat.money.db.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.models.HistoryModel;

/**
 * Created by Akshat on 5/4/2016.
 */
public class HistoryTable {

    public static final String TABLE_HISTORY = "History_table";

    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_YEAR= "year";
    public static final String COLUMN_MONTH= "month";
    public static final String COLUMN_DATE= "date";
    public static final String COLUMN_ACTION= "action";
    public static final String COLUMN_NAME_NULLABLE = "dummy";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_HISTORY
            + "("
            + COLUMN_CONTACT_ID + " VARCHAR(5),"
            + COLUMN_AMOUNT + " VARCHAR(5) NOT NULL,"
            + COLUMN_YEAR + " VARCHAR(5) NOT NULL,"
            + COLUMN_MONTH + " VARCHAR(15) NOT NULL,"
            + COLUMN_DATE + " VARCHAR(3) NOT NULL,"
            + COLUMN_ACTION + " VARCHAR(7) NOT NULL"
            + ");";

    // Database delete all SQL statement
    private static final String DELETE_ALL_ROWS = "DELETE " +
            "FROM " + TABLE_HISTORY + ";" ;

    private static SQLiteDatabase database;

    public static SQLiteDatabase getDatabase(SQLiteOpenHelper dbHelper) {
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public HistoryTable() {
        super();
    }

    // Create table when database is created
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // Update table when database is update
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(HistoryTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        HistoryTable.onCreate(database);
    }

    public static void insert(SQLiteOpenHelper dbHelper, HistoryModel historyModel) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_ID, historyModel.getContact_id());
        values.put(COLUMN_AMOUNT, historyModel.getAmount());
        values.put(COLUMN_YEAR, historyModel.getYear());
        values.put(COLUMN_MONTH, historyModel.getMonth());
        values.put(COLUMN_DATE, historyModel.getDate());
        values.put(COLUMN_ACTION, historyModel.getAction());

        getDatabase(dbHelper).replace(TABLE_HISTORY, COLUMN_NAME_NULLABLE, values);
    }

    public static ArrayList<HistoryModel> getHistory(SQLiteOpenHelper dbHelper, String contact_id) {
        ArrayList<HistoryModel> histories = new ArrayList<>();

        final String SELECT_CONVERSION = "SELECT " +
                "* " +
                "FROM " + HistoryTable.TABLE_HISTORY + " " +
                "WHERE "+COLUMN_CONTACT_ID+"='"+contact_id+"';";

        Log.d("DataTest", SELECT_CONVERSION);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Cursor cursor = getDatabase(dbHelper).rawQuery(SELECT_CONVERSION, null);
        if (cursor.moveToLast()) {
            do {
                HistoryModel history = new HistoryModel();
                history.setContact_id(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID)));
                history.setAmount(cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT)));
                history.setYear(cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)));
                history.setMonth(cursor.getString(cursor.getColumnIndex(COLUMN_MONTH)));
                history.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                history.setAction(cursor.getString(cursor.getColumnIndex(COLUMN_ACTION)));

                histories.add(history);

            } while (cursor.moveToPrevious());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public static void deleteAllRows(SQLiteOpenHelper dbHelper) {
        Log.d("DataTest", DELETE_ALL_ROWS);
        getDatabase(dbHelper).execSQL(DELETE_ALL_ROWS);
    }

    public static void deleteHistory(SQLiteOpenHelper dbHelper, String contact_id) {
        final String DELETE_HISTORY = "DELETE " +
                "FROM " + TABLE_HISTORY + " " +
                "WHERE "+COLUMN_CONTACT_ID+"='"+contact_id+"';";

        Log.d("DataTest", DELETE_HISTORY);
        getDatabase(dbHelper).execSQL(DELETE_HISTORY);
    }
}
