package in.jainakshat.money.permissionactivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.jainakshat.money.R;
import in.jainakshat.money.mainactivity.MainActivity;
import in.jainakshat.money.utills.PermissionHandler;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static in.jainakshat.money.MainApplication.REQUEST_CODE_ASK_PERMISSIONS;

/**
 * Created by AkshatJain on 12/28/2016.
 */

public class PermissionActivity extends AppCompatActivity {

    private Button mNextButton;
    private TextView mPermissionStatus;
    private TextView mPermissionSubText;
    private TextView mPermissionText;
    private TextView mPermissionList;
    private ImageView mPermissionThumb;

    private Boolean grantedFlag = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        if(PermissionHandler.checkPermission(getBaseContext())) {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
        Typeface karla_font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Karla-Regular.ttf");

        mNextButton = (Button) findViewById(R.id.next_button);
        mPermissionStatus = (TextView) findViewById(R.id.permission_status);
        mPermissionSubText = (TextView) findViewById(R.id.permission_subtext);
        mPermissionText = (TextView) findViewById(R.id.permission_text);
        mPermissionList = (TextView) findViewById(R.id.permission_list);
        mPermissionThumb = (ImageView) findViewById(R.id.permission_thumb);

        mNextButton.setTypeface(karla_font);
        mPermissionStatus.setTypeface(karla_font);
        mPermissionSubText.setTypeface(karla_font);
        mPermissionList.setTypeface(karla_font);
        mPermissionText.setTypeface(karla_font);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(grantedFlag) {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                } else {
                    requestPermissions(new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    grantedFlag = true;
                    mPermissionStatus.setText("All Set");
                    mPermissionSubText.setText("Click 'Proceed' to launch Money");
                    mPermissionText.setVisibility(View.GONE);
                    mPermissionList.setVisibility(View.GONE);
                    mPermissionThumb.setVisibility(View.VISIBLE);
                    mNextButton.setBackgroundColor(getResources().getColor(R.color.md_teal_600));
                    mNextButton.setText("Proceed");

                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
