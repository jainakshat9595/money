package in.jainakshat.money.preferencesmanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Akshat on 4/29/2016.
 */
public class MoneyPreferenceManager {

    private static final String CONTACTS_UPDATED = "ContactUpdated";
    private static final String CONVERSION_PREFERENCES = "ConversionPreferences";

    private SharedPreferences mSharedpreferences;

    public MoneyPreferenceManager(Context context) {
        mSharedpreferences = context.getSharedPreferences(CONVERSION_PREFERENCES, context.MODE_PRIVATE);
    }

    // Setters
    public void setContactsUpdated(boolean status) {
        mSharedpreferences.edit().putBoolean(CONTACTS_UPDATED, status).commit();
    }

    // Getters
    public Boolean getContactsUpdated() {
        return mSharedpreferences.getBoolean(CONTACTS_UPDATED, false);
    }

    // Removers
    public void removeContactsUpdated() {
        mSharedpreferences.edit().remove(CONTACTS_UPDATED).commit();
    }

}
