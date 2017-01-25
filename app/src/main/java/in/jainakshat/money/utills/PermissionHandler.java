package in.jainakshat.money.utills;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import in.jainakshat.money.mainactivity.MainActivity;

/**
 * Created by AkshatJain on 12/28/2016.
 */

public class PermissionHandler {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermission(Context context) {
        System.out.println("Custom Log: checkPermission called");
        int hasContactsPermission = context.checkSelfPermission(Manifest.permission.READ_CONTACTS);
        int hasStoragePermission = context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasContactsPermission == PackageManager.PERMISSION_GRANTED && hasStoragePermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

}
