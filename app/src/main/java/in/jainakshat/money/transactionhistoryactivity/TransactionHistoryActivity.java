package in.jainakshat.money.transactionhistoryactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.jainakshat.money.R;
import in.jainakshat.money.db.models.ContactModel;

/**
 * Created by Akshat on 5/2/2016.
 */
public class TransactionHistoryActivity extends AppCompatActivity {

    private ContactModel mContactModel;

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

        mTextView_th_netvalue.setText(mContactModel.getNet_value());
        if(Integer.parseInt(mContactModel.getNet_value()) > 0) mTextView_th_netvalue_tag.setText("You Get!");
        else if(Integer.parseInt(mContactModel.getNet_value()) < 0) mTextView_th_netvalue_tag.setText("You Give!");
        else if(Integer.parseInt(mContactModel.getNet_value()) == 0) mTextView_th_netvalue_tag.setText("All Clear!");

        mRecyclerView_th_recyler_view = (RecyclerView) findViewById(R.id.th_recyler_view) ;
        mRecyclerView_th_recyler_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerViewAdapter = new TransactionHistoryAdapter();
        ArrayList<String> mData = new ArrayList<>();
        mData.add("One");mData.add("Two");mData.add("Three");mData.add("Four");
        mRecyclerViewAdapter.setData(mData);
        mRecyclerView_th_recyler_view.setAdapter(mRecyclerViewAdapter);

    }
}
