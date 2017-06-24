package in.jainakshat.money.activity.restoreactivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;

import in.jainakshat.money.R;
import in.jainakshat.money.db.DBHelper;
import in.jainakshat.money.activity.mainactivity.MainActivity;

/**
 * Created by Akshat on 5/18/2016.
 */
public class RestoreActivity extends AppCompatActivity {

    private TextView restoreStatus;
    private TextView restoreTime;
    private TextView restoreText;
    private Button restoreButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restoredb);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Typeface karla_font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Karla-Regular.ttf");
        toolbar.setTitle("Restore");
        setSupportActionBar(toolbar);

        restoreStatus = (TextView) findViewById(R.id.restore_status);
        restoreTime = (TextView) findViewById(R.id.restore_time);
        restoreText = (TextView) findViewById(R.id.restore_text);
        restoreButton = (Button) findViewById(R.id.restore_button);

        restoreStatus.setTypeface(karla_font);
        restoreTime.setTypeface(karla_font);
        restoreText.setTypeface(karla_font);
        restoreButton.setTypeface(karla_font);

        if(!checkForBackups().equals("false")) {
            restoreStatus.setText("Available!");
            Date lastBackupDate = new Date(Long.parseLong(checkForBackups()));
            restoreTime.setText(lastBackupDate.toString());
        }
        else {
            restoreStatus.setText("Oops!");
            restoreTime.setText("No backups found");
            restoreText.setVisibility(View.GONE);
            restoreButton.setVisibility(View.GONE);
        }

        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreDB();
            }
        });

    }

    public String checkForBackups() {
        File backupFile = new File(Environment.getExternalStorageDirectory(), DBHelper.DATABASE_NAME+".db");
        if(backupFile.exists() && backupFile.isFile() && backupFile.length()>0) {
            return String.valueOf(backupFile.lastModified());
        }
        else return "false";
    }

    public void restoreDB() {
        try {
            File currentDB = getDatabasePath(DBHelper.DATABASE_NAME);
            File backupFile = new File(Environment.getExternalStorageDirectory(), DBHelper.DATABASE_NAME+".db");

            if (Environment.getExternalStorageDirectory().canRead()) {
                getBaseContext().deleteDatabase(DBHelper.DATABASE_NAME);
                FileChannel src = new FileInputStream(backupFile).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                /*Snackbar.make(restoreStatus, "Successfully restored! Restart App Once to update Changes.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //finish();
                Intent mStartActivity = new Intent(getBaseContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rdb, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.delete_all_backup:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog))
                        .setTitle("Delete All Backup")
                        .setMessage("Do you really want to delete all the Backups?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(deleteAllBackups()) {
                                    Snackbar.make(restoreStatus, "Backups deleted successfully!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                else {
                                    Snackbar.make(restoreStatus, "Some Error!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean deleteAllBackups() {
        File backupFile = new File(Environment.getExternalStorageDirectory(), DBHelper.DATABASE_NAME+".db");
        boolean deleted = backupFile.delete();
        return deleted;
    }
}
