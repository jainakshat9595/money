package in.jainakshat.money.mainactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import in.jainakshat.money.R;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.models.HistoryModel;
import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.db.tables.HistoryTable;
import in.jainakshat.money.preferencesmanager.MoneyPreferenceManager;
import in.jainakshat.money.restoreactivity.RestoreActivity;
import in.jainakshat.money.transactionhistoryactivity.TransactionHistoryActivity;
import in.jainakshat.money.utills.ContactHandler;

public class MainActivity extends AppCompatActivity {

    private MoneyPreferenceManager mPreferenceManager;

    private RecyclerView mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;
    private SearchView mContactSearchView;

    private LinearLayout mRelativeView_bottom_bar_layout_add;
    private RelativeLayout mRelativeView_bottom_bar_layout_search;

    private ImageView mImageView_bottom_bar_layout_add_close;
    private ImageView mImageView_bottom_bar_layout_add_check;
    private EditText mEditText_bottom_bar_layout_add_amount;
    private Spinner mSpinner_bottom_bar_layout_add_type;
    private EditText mEditText_bottom_bar_layout_add_description;

    private RelativeLayout mLoadingOverlay;

    private ArrayList<ContactModel> mContacts_array;

    private ContactModel selectedContactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferenceManager = new MoneyPreferenceManager(getBaseContext());
        if(!mPreferenceManager.getContactsUpdated()) {
            ContactHandler.updateContacts(getBaseContext());
            mPreferenceManager.setContactsUpdated(true);
        }

        selectedContactModel = null;

        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));

        mRecyclerView = (RecyclerView) findViewById(R.id.main_contacts_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerViewAdapter = new ContactsAdapter(getBaseContext());
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mContactSearchView = (SearchView) findViewById(R.id.contact_search_view);
        int searchViewTextBoxID = mContactSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchViewTextBox = (TextView) mContactSearchView.findViewById(searchViewTextBoxID);
        searchViewTextBox.setTextColor(Color.WHITE);
        searchViewTextBox.setHintTextColor(Color.WHITE);

        mRelativeView_bottom_bar_layout_add = (LinearLayout) findViewById(R.id.bottom_bar_layout_add);
        mRelativeView_bottom_bar_layout_search = (RelativeLayout) findViewById(R.id.bottom_bar_layout_search);

        mImageView_bottom_bar_layout_add_close = (ImageView) findViewById(R.id.bottom_bar_layout_add_close);
        mImageView_bottom_bar_layout_add_check = (ImageView) findViewById(R.id.bottom_bar_layout_add_check);
        mEditText_bottom_bar_layout_add_amount = (EditText) findViewById(R.id.bottom_bar_layout_add_amount);
        mSpinner_bottom_bar_layout_add_type = (Spinner) findViewById(R.id.bottom_bar_layout_add_type);
        mEditText_bottom_bar_layout_add_description = (EditText) findViewById(R.id.bottom_bar_layout_add_description);

        mLoadingOverlay = (RelativeLayout) findViewById(R.id.loading_overlay);
        mLoadingOverlay.setVisibility(View.GONE);

        ArrayList<String> mSpinner_bottom_bar_layout_add_type_items = new ArrayList<>();
        mSpinner_bottom_bar_layout_add_type_items.add("I have to take");
        mSpinner_bottom_bar_layout_add_type_items.add("I have to give");
        mSpinner_bottom_bar_layout_add_type_items.add("Amount settled");
        mSpinner_bottom_bar_layout_add_type.setAdapter(new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, mSpinner_bottom_bar_layout_add_type_items));

        mContactSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<ContactModel> filteredList = new ArrayList<>();

                for (int i=0; i<mContacts_array.size(); i++) {
                    ContactModel contact = mContacts_array.get(i);
                    String text = contact.getName().toLowerCase();
                    if(text.contains(newText)) {
                        filteredList.add(contact);
                    }
                }
                mRecyclerViewAdapter.setData(filteredList);
                mRecyclerViewAdapter.notifyDataSetChanged();  // data set changed
                return true;
            }
        });

        mRecyclerViewAdapter.setOnItemClickHandler(new ContactsAdapter.ItemClickHandler() {
            @Override
            public void OnItemClicked(ContactModel contactModel) {
                Intent intent = new Intent(getBaseContext(), TransactionHistoryActivity.class);
                intent.putExtra("contact", contactModel);
                startActivity(intent);
            }

            @Override
            public void OnItemLongClicked(ContactModel contactModel) {
                mRelativeView_bottom_bar_layout_search.setVisibility(View.GONE);
                mRelativeView_bottom_bar_layout_add.setVisibility(View.GONE);

                mRelativeView_bottom_bar_layout_add.setVisibility(View.VISIBLE);

                selectedContactModel = contactModel;

            }
        });

        mImageView_bottom_bar_layout_add_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewAdapter.unselectItem();
                mRelativeView_bottom_bar_layout_search.setVisibility(View.GONE);
                mRelativeView_bottom_bar_layout_add.setVisibility(View.GONE);

                mRelativeView_bottom_bar_layout_search.setVisibility(View.VISIBLE);
            }
        });

        mImageView_bottom_bar_layout_add_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewAdapter.unselectItem();
                mRelativeView_bottom_bar_layout_search.setVisibility(View.GONE);
                mRelativeView_bottom_bar_layout_add.setVisibility(View.GONE);

                mRelativeView_bottom_bar_layout_search.setVisibility(View.VISIBLE);

                updateNetValueAndTransactionHistory(selectedContactModel, mEditText_bottom_bar_layout_add_amount.getText().toString(), mSpinner_bottom_bar_layout_add_type.getSelectedItem().toString(), mEditText_bottom_bar_layout_add_description.getText().toString());
                mEditText_bottom_bar_layout_add_amount.setText("");

            }
        });

    }

    @Override
    protected void onResume() {
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        mRecyclerViewAdapter.setData(mContacts_array);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.reset_everything) {
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog))
                    .setTitle("Reset Everything!")
                    .setMessage("Do you really want to reset everything with Money App?")
                    .setIcon(R.drawable.alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mLoadingOverlay.setVisibility(View.VISIBLE);
                            resetEverything();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        else if(id == R.id.backup_db) {
            backupDB();
            return true;
        }
        else if(id == R.id.restore_db) {
            startActivity(new Intent(getBaseContext(), RestoreActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateNetValueAndTransactionHistory(ContactModel contactModel, String amount, String operation, String description) {
        switch(operation) {
            case "I have to take":
                int n_value_1 = Integer.parseInt(contactModel.getNet_value());
                n_value_1 = n_value_1 + Integer.parseInt(amount);
                ContactTable.updateNetValueInDatabase(DBHelper.getInstance(getBaseContext()), contactModel.getType_id(), n_value_1, false, null);
                HistoryModel historyModel_1 = new HistoryModel();
                historyModel_1.setContact_id(contactModel.getType_id());
                historyModel_1.setAmount(amount);
                historyModel_1.setYear(String.valueOf(new Date().getYear()));
                historyModel_1.setMonth(String.valueOf(new Date().getMonth()));
                historyModel_1.setDate(String.valueOf(new Date().getDate()));
                historyModel_1.setAction("Take");
                historyModel_1.setDescription(description);
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_1);
                break;
            case "I have to give":
                int n_value_2 = Integer.parseInt(contactModel.getNet_value());
                n_value_2 = n_value_2 - Integer.parseInt(amount);
                ContactTable.updateNetValueInDatabase(DBHelper.getInstance(getBaseContext()), contactModel.getType_id(), n_value_2, false, null);
                HistoryModel historyModel_2 = new HistoryModel();
                historyModel_2.setContact_id(contactModel.getType_id());
                historyModel_2.setAmount(amount);
                historyModel_2.setYear(String.valueOf(new Date().getYear()));
                historyModel_2.setMonth(String.valueOf(new Date().getMonth()));
                historyModel_2.setDate(String.valueOf(new Date().getDate()));
                historyModel_2.setAction("Give");
                historyModel_2.setDescription(description);
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_2);
                break;
            case "Amount settled":
                int n_value_3 = Integer.parseInt(contactModel.getNet_value());
                if(n_value_3 < 0) {
                    n_value_3 = Integer.parseInt(amount) + n_value_3;
                } else if(n_value_3 > 0) {
                    n_value_3 = n_value_3 - Integer.parseInt(amount);
                } else if(n_value_3 == 0) {
                    Snackbar.make(mRecyclerView, "Error: Cannot settle!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    break;
                }
                ContactTable.updateNetValueInDatabase(DBHelper.getInstance(getBaseContext()), contactModel.getType_id(), n_value_3, false, null);
                HistoryModel historyModel_3 = new HistoryModel();
                historyModel_3.setContact_id(contactModel.getType_id());
                historyModel_3.setAmount(amount);
                historyModel_3.setYear(String.valueOf(new Date().getYear()));
                historyModel_3.setMonth(String.valueOf(new Date().getMonth()));
                historyModel_3.setDate(String.valueOf(new Date().getDate()));
                historyModel_3.setAction("Settle");
                historyModel_3.setDescription(description);
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_3);
                break;

        }
        updateAdapterList();
        selectedContactModel = null;
    }

    public void updateAdapterList() {
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void backupDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String backupDBPath = DBHelper.DATABASE_NAME+".db";
                File currentDB = getDatabasePath(DBHelper.DATABASE_NAME);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Snackbar.make(mRecyclerView, "Backup made into internal storage!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        } catch (Exception e) {
        }
    }

    public void resetEverything() {
        HistoryTable.deleteAllRows(DBHelper.getInstance(getBaseContext()));
        ContactTable.deleteAllRows(DBHelper.getInstance(getBaseContext()));
        mPreferenceManager.setContactsUpdated(false);
        ContactHandler.updateContacts(getBaseContext());
        mPreferenceManager.setContactsUpdated(true);
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        mRecyclerViewAdapter.setData(mContacts_array);
        mLoadingOverlay.setVisibility(View.GONE);
    }

}
