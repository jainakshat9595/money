package in.jainakshat.money.activity.loginactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import in.jainakshat.money.R;
import in.jainakshat.money.utills.Logger;

/**
 * Created by Akshat on 25-05-2017.
 */

public class LoginRegisterActivity extends AppCompatActivity {

    String mPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        mPhoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        Logger.logg("phone", mPhoneNumber);

    }

}
