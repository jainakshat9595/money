package in.jainakshat.money.mainactivity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import in.jainakshat.money.R;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.preferencesmanager.MoneyPreferenceManager;
import in.jainakshat.money.utills.ContactHandler;

public class MainActivity extends AppCompatActivity {

    private MoneyPreferenceManager mPreferenceManager;

    private RecyclerView mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;

    private ArrayList<ContactModel> mContacts_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferenceManager = new MoneyPreferenceManager(getBaseContext());
        if(!mPreferenceManager.getContactsUpdated()) {
            ContactHandler.updateContacts(getBaseContext());
            //mPreferenceManager.setContactsUpdated(true);
        }

        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));

        System.out.println("mContacts_array.size(): "+ mContacts_array.size());

        mRecyclerView = (RecyclerView) findViewById(R.id.main_contacts_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerViewAdapter = new ContactsAdapter(getBaseContext());
        mRecyclerViewAdapter.setData(mContacts_array);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
}
