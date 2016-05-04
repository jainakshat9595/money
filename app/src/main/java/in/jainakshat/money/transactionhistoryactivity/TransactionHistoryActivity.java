package in.jainakshat.money.transactionhistoryactivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.jainakshat.money.R;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.db.models.ContactModel;
import in.jainakshat.money.db.models.HistoryModel;
import in.jainakshat.money.db.tables.ContactTable;
import in.jainakshat.money.db.tables.HistoryTable;

/**
 * Created by Akshat on 5/2/2016.
 */
public class TransactionHistoryActivity extends AppCompatActivity {

    private ContactModel mContactModel;

    private ArrayList<HistoryModel> mData_history;

    private TextView mTextView_th_netvalue;
    private TextView mTextView_th_netvalue_tag;
    private RecyclerView mRecyclerView_th_recyler_view;
    private TransactionHistoryAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactionhistory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Typeface karla_font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Karla-Regular.ttf");
        mContactModel = getIntent().getParcelableExtra("contact");
        toolbar.setTitle(mContactModel.getName());
        setSupportActionBar(toolbar);

        ((TextView)findViewById(R.id.th_netvalue_msg)).setTypeface(karla_font);
        mTextView_th_netvalue = (TextView) findViewById(R.id.th_netvalue);
        mTextView_th_netvalue.setTypeface(karla_font);
        mTextView_th_netvalue_tag = (TextView) findViewById(R.id.th_netvalue_tag);
        mTextView_th_netvalue_tag.setTypeface(karla_font);

        mData_history = HistoryTable.getHistory(DBHelper.getInstance(getBaseContext()), mContactModel.getType_id());

        mTextView_th_netvalue.setText(mContactModel.getNet_value());
        if(Integer.parseInt(mContactModel.getNet_value()) > 0) mTextView_th_netvalue_tag.setText("You Get!");
        else if(Integer.parseInt(mContactModel.getNet_value()) < 0) mTextView_th_netvalue_tag.setText("You Give!");
        else if(Integer.parseInt(mContactModel.getNet_value()) == 0) mTextView_th_netvalue_tag.setText("All Clear!");

        mRecyclerView_th_recyler_view = (RecyclerView) findViewById(R.id.th_recyler_view) ;
        mRecyclerView_th_recyler_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerViewAdapter = new TransactionHistoryAdapter(getBaseContext());

        mRecyclerViewAdapter.setData(mData_history);
        mRecyclerView_th_recyler_view.setAdapter(mRecyclerViewAdapter);

    }

    public void deleteAllHistory() {
        HistoryTable.deleteHistory(DBHelper.getInstance(getBaseContext()), mContactModel.getType_id());
        mData_history = HistoryTable.getHistory(DBHelper.getInstance(getBaseContext()), mContactModel.getType_id());
        mRecyclerViewAdapter.setData(mData_history);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void resetAccount() {
        deleteAllHistory();
        ContactTable.updateNetValueInDatabase(DBHelper.getInstance(getBaseContext()), mContactModel.getType_id(), 0, true, mContactModel.getTimestamp_created());
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_th, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.update_contact_list:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog))
                        .setTitle("Delete All History")
                        .setMessage("Do you really want to delete all the transaction history?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteAllHistory();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            case R.id.reset_contact:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog))
                        .setTitle("Reset "+mContactModel.getName())
                        .setMessage("Do you really want to reset everything with "+mContactModel.getName()+"?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                resetAccount();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
