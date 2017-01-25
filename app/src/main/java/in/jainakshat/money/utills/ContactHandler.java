package in.jainakshat.money.utills;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;

import in.jainakshat.money.MainApplication;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.mainactivity.MainActivity;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static in.jainakshat.money.MainApplication.REQUEST_CODE_ASK_PERMISSIONS;


/**
 * Created by Akshat on 4/28/2016.
 */
public class ContactHandler {

    public static void updateContacts(Context context) {
        ContactTable.deleteAllRows(DBHelper.getInstance(context));

        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" DESC");
        c.moveToFirst();
        String last_read_name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        while(c.moveToNext()) {
            int number_status = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
            if(number_status == 1) {
                String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String id = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                if((last_read_name.equals(name)) || (isNumeric(name))) {
                    continue;
                }
                else {
                    last_read_name = name;
                    ContactModel contact = new ContactModel();
                    contact.setType_id(id);
                    contact.setName(name);
                    contact.setNumber(number);
                    contact.setNet_value("0");
                    contact.setTimestamp(String.valueOf(System.currentTimeMillis()));
                    contact.setTimestamp_created(String.valueOf(System.currentTimeMillis()));
                    ContactTable.insert(DBHelper.getInstance(context), contact);
                }
            }
        }
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

}
