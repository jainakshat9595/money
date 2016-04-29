package in.jainakshat.money.utills;

import android.content.Context;
import android.database.Cursor;
import android.provider.Contacts;
import android.provider.ContactsContract;

import java.util.ArrayList;

import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.tables.ContactTable;

/**
 * Created by Akshat on 4/28/2016.
 */
public class ContactHandler {

    public static void updateContacts(Context context) {

        ContactTable.deleteAllRows(DBHelper.getInstance(context));

        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        c.moveToFirst();
        String last_read_name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        while(c.moveToNext()) {
            int number_status = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER));
            if(number_status == 1) {
                String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String id = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                if(last_read_name.equals(name)) {
                    continue;
                }
                else {
                    last_read_name = name;
                    ContactModel contact = new ContactModel();
                    contact.setType_id(id);
                    contact.setName(name);
                    contact.setNumber(number);
                    if(name.length()%2 == 0) contact.setNet_value("-25");
                    else if(name.length()%3 == 0) contact.setNet_value("25");
                    else contact.setNet_value("0");
                    ContactTable.insert(DBHelper.getInstance(context), contact);
                }
            }
        }
    }
}
