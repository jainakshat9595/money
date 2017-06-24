package in.jainakshat.money.activity.loginactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import in.jainakshat.money.R;
import in.jainakshat.money.utills.KeyBoardUtils;
import in.jainakshat.money.utills.RestClient;

/**
 * Created by Akshat on 20-05-2017.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView mLogin_one;
    private EditText mLogin_et;
    private ImageButton mLogin_next;

    private ProgressBar mSpinner;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();

        Typeface karla_font = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Karla-Regular.ttf");

        mLogin_one = (TextView) findViewById(R.id.login_one);
        mLogin_et = (EditText) findViewById(R.id.login_et);
        mLogin_next = (ImageButton) findViewById(R.id.login_next);

        mSpinner = (ProgressBar)findViewById(R.id.progressBar1);

        mLogin_et.setTypeface(karla_font);
        mLogin_one.setTypeface(karla_font);

        mLogin_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();
                params.put("phone", ""+mLogin_et.getText());
                RestClient.post("user/login", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        mSpinner.setVisibility(View.VISIBLE);
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        mSpinner.setVisibility(View.GONE);
                        KeyBoardUtils.hide(LoginActivity.this);
                        try {
                            JSONObject jsonObject = new JSONObject(new String(responseBody, 0));
                            if(jsonObject.get("message").equals("USER_EXIST")) {
                                Intent intent = new Intent(mContext, LoginPasswordActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("PHONE_NUMBER", jsonObject.get("phone").toString());
                                intent.putExtra("NAME", jsonObject.get("name").toString());
                                mContext.startActivity(intent);
                            } else {
                                Intent intent = new Intent(mContext, LoginRegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("PHONE_NUMBER", jsonObject.get("phone").toString());
                                mContext.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        mSpinner.setVisibility(View.GONE);
                        KeyBoardUtils.hide(LoginActivity.this);
                        System.out.println("MoneyApp: onFailure, statusCode: "+statusCode+", responseBody: "+new String(responseBody, 0));
                    }
                });
            }
        });
    }
}
