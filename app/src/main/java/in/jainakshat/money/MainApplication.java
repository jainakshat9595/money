package in.jainakshat.money;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Akshat on 4/29/2016.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
