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


/**
 * Created by Akshat on 4/29/2016.
 */
public class ContactTable {

    public static final String TABLE_CONVERSION = "Contacts_table";

    public static final String COLUMN_TYPE_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER= "number";
    public static final String COLUMN_NET_VALUE= "netvalue";
    public static final String COLUMN_TIMESTAMP= "timestamp";
    public static final String COLUMN_TIMESTAMP_CREATED= "timestamp_created";
    public static final String COLUMN_NAME_NULLABLE = "dummy";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONVERSION
            + "("
            + COLUMN_TYPE_ID + " VARCHAR(5) PRIMARY KEY,"
            + COLUMN_NAME + " VARCHAR(30) NOT NULL,"
            + COLUMN_NUMBER + " VARCHAR(15) NOT NULL,"
            + COLUMN_TIMESTAMP + " VARCHAR(20) NOT NULL,"
            + COLUMN_NET_VALUE + " VARCHAR(4) NOT NULL,"
            + COLUMN_TIMESTAMP_CREATED + " VARCHAR(20) NOT NULL"
            + ");";

    // Database select all SQL statement
    private static final String SELECT_ALL_CONVERSION = "SELECT " +
            "* " +
            "FROM " + ContactTable.TABLE_CONVERSION + " " +
            "ORDER BY "+COLUMN_TIMESTAMP+" DESC;";

    // Database delete all SQL statement
    private static final String DELETE_ALL_ROWS = "DELETE " +
            "FROM " + ContactTable.TABLE_CONVERSION + ";" ;

    private static SQLiteDatabase database;

    public static SQLiteDatabase getDatabase(SQLiteOpenHelper dbHelper) {
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public ContactTable() {
        super();
    }

    // Create table when database is created
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // Update table when database is update
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(ContactTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSION);
        ContactTable.onCreate(database);
    }

    public static void insert(SQLiteOpenHelper dbHelper, ContactModel contact) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_ID, contact.getType_id());
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_NUMBER, contact.getNumber());
        values.put(COLUMN_NET_VALUE, contact.getNet_value());
        values.put(COLUMN_TIMESTAMP, contact.getTimestamp());
        values.put(COLUMN_TIMESTAMP_CREATED, contact.getTimestamp_created());

        getDatabase(dbHelper).replace(TABLE_CONVERSION, COLUMN_NAME_NULLABLE, values);
    }

    public static ArrayList<ContactModel> getContacts(SQLiteOpenHelper dbHelper) {
        ArrayList<ContactModel> contacts = new ArrayList<>();
        Log.d("DataTest", SELECT_ALL_CONVERSION);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        Cursor cursor = getDatabase(dbHelper).rawQuery(SELECT_ALL_CONVERSION, null);
        if (cursor.moveToFirst()) {
            do {
                ContactModel contact = new ContactModel();
                contact.setType_id(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_TYPE_ID)));
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NAME)));
                contact.setNumber(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NUMBER)));
                contact.setNet_value(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_NET_VALUE)));
                contact.setTimestamp(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_TIMESTAMP)));
                contact.setTimestamp_created(cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_TIMESTAMP_CREATED)));

                contacts.add(contact);

            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contacts;
    }

    public static void deleteAllRows(SQLiteOpenHelper dbHelper) {
        Log.d("DataTest", DELETE_ALL_ROWS);

        getDatabase(dbHelper).execSQL(DELETE_ALL_ROWS);

    }

    public static void updateNetValueInDatabase(SQLiteOpenHelper dbHelper, String typeid, int net_value, boolean isReset, String timestamp_created) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NET_VALUE, String.valueOf(net_value));
        if(isReset) {
            values.put(COLUMN_TIMESTAMP, timestamp_created);
        }
        else {
            values.put(COLUMN_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        }


        String WHERE_CLAUSE = COLUMN_TYPE_ID+"='"+typeid+"'";

        getDatabase(dbHelper).update(TABLE_CONVERSION, values, WHERE_CLAUSE, null);
    }


}
