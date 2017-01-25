package in.jainakshat.money.mainactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;

import in.jainakshat.money.R;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.models.HistoryModel;
import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.db.tables.HistoryTable;
import in.jainakshat.money.permissionactivity.PermissionActivity;
import in.jainakshat.money.preferencesmanager.MoneyPreferenceManager;
import in.jainakshat.money.restoreactivity.RestoreActivity;
import in.jainakshat.money.transactionhistoryactivity.TransactionHistoryActivity;
import in.jainakshat.money.utills.ContactHandler;
import in.jainakshat.money.utills.PermissionHandler;

import static in.jainakshat.money.MainApplication.REQUEST_CODE_ASK_PERMISSIONS;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private MoneyPreferenceManager mPreferenceManager;

    private RecyclerView mRecyclerView;
    private ContactsAdapter mRecyclerViewAdapter;
    private SearchView mContactSearchView;

    private DrawerLayout mDrawer;

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

    private static final String TAG = "Google Drive Activity";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    private DriveId mFileId;
    public DriveFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        if(!PermissionHandler.checkPermission(getBaseContext())) {
            startActivity(new Intent(getBaseContext(), PermissionActivity.class));
            finish();
        }
        mContacts_array = ContactTable.getContacts(DBHelper.getInstance(getBaseContext()));
        mRecyclerViewAdapter.setData(mContacts_array);
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        //mGoogleApiClient.connect();
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

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        System.out.println("Connected: "+bundle.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());

        if (!connectionResult.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    Log.e("file id", mFileId.getResourceId() + "");
                    String url = "https://drive.google.com/open?id="+ mFileId.getResourceId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    ContactHandler.updateContacts(getBaseContext());
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
