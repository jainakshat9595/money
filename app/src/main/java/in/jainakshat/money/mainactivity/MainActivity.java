package in.jainakshat.money.mainactivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;

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
import in.jainakshat.money.transactionhistoryactivity.TransactionHistoryActivity;
import in.jainakshat.money.utills.ContactHandler;

public class MainActivity extends AppCompatActivity {

    private MoneyPreferenceManager mPreferenceManager;

    private RecyclerView mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;
    private SearchView mContactSearchView;

    private RelativeLayout mRelativeView_bottom_bar_layout_add;
    private RelativeLayout mRelativeView_bottom_bar_layout_search;

    private ImageView mImageView_bottom_bar_layout_add_close;
    private ImageView mImageView_bottom_bar_layout_add_check;
    private EditText mEditText_bottom_bar_layout_add_amount;
    private Spinner mSpinner_bottom_bar_layout_add_type;

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
        /*Collections.sort(mContacts_array, new Comparator<ContactModel>() {
            @Override
            public int compare(ContactModel o1, ContactModel o2) {
                return (int)(Long.parseLong(o1.getTimestamp()) - Long.parseLong(o2.getTimestamp()));
            }
        });*/
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mContactSearchView = (SearchView) findViewById(R.id.contact_search_view);
        int searchViewTextBoxID = mContactSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchViewTextBox = (TextView) mContactSearchView.findViewById(searchViewTextBoxID);
        searchViewTextBox.setTextColor(Color.WHITE);
        searchViewTextBox.setHintTextColor(Color.WHITE);

        mRelativeView_bottom_bar_layout_add = (RelativeLayout) findViewById(R.id.bottom_bar_layout_add);
        mRelativeView_bottom_bar_layout_search = (RelativeLayout) findViewById(R.id.bottom_bar_layout_search);

        mImageView_bottom_bar_layout_add_close = (ImageView) findViewById(R.id.bottom_bar_layout_add_close);
        mImageView_bottom_bar_layout_add_check = (ImageView) findViewById(R.id.bottom_bar_layout_add_check);
        mEditText_bottom_bar_layout_add_amount = (EditText) findViewById(R.id.bottom_bar_layout_add_amount);
        mSpinner_bottom_bar_layout_add_type = (Spinner) findViewById(R.id.bottom_bar_layout_add_type);

        ArrayList<String> mSpinner_bottom_bar_layout_add_type_items = new ArrayList<>();
        mSpinner_bottom_bar_layout_add_type_items.add("Take");
        mSpinner_bottom_bar_layout_add_type_items.add("Give");
        mSpinner_bottom_bar_layout_add_type_items.add("Settle");
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
                /*Collections.sort(filteredList, new Comparator<ContactModel>() {
                    @Override
                    public int compare(ContactModel o1, ContactModel o2) {
                        return (int)(Long.parseLong(o1.getTimestamp()) - Long.parseLong(o2.getTimestamp()));
                    }
                });*/
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

                updateNetValueAndTransactionHistory(selectedContactModel, mEditText_bottom_bar_layout_add_amount.getText().toString(), mSpinner_bottom_bar_layout_add_type.getSelectedItem().toString());
                mEditText_bottom_bar_layout_add_amount.setText("");

            }
        });

    }

    @Override
    protected void onResume() {
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerViewAdapter.notifyDataSetChanged();
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
        if (id == R.id.update_contact_list) {
            Snackbar.make(mRecyclerView, "Update Contact List has been clicked!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return true;
        }
        else if(id == R.id.reset_everything) {
            Snackbar.make(mRecyclerView, "Reset has been clicked!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateNetValueAndTransactionHistory(ContactModel contactModel, String amount, String operation) {
        switch(operation) {
            case "Take":
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
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_1);
                break;
            case "Give":
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
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_2);
                break;
            case "Settle":
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
                HistoryTable.insert(DBHelper.getInstance(getBaseContext()), historyModel_3);
                break;

        }
        updateAdapterList();
        selectedContactModel = null;
    }

    public void updateAdapterList() {
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        /*Collections.sort(mContacts_array, new Comparator<ContactModel>() {
            @Override
            public int compare(ContactModel o1, ContactModel o2) {
                return (int)(Long.parseLong(o1.getTimestamp()) - Long.parseLong(o2.getTimestamp()));
            }
        });*/
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

}
