package com.example.punit.twitterclient.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.example.punit.twitterclient.R;

public class PermissionUtility {


    public static boolean checkIfStoragePermissionIsGranted(Context context){
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void requestStoragePermission(final Context context){
        if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,Manifest.permission.READ_EXTERNAL_STORAGE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle)
                    .setCancelable(true)
                    .setTitle("Storage Permission is necessary")
                    .setMessage("App needs storage permission in order to attach images/videos to tweet")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    Constants.EXT_STORAGE_PERMISSION);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.EXT_STORAGE_PERMISSION);
        }
    }

}
