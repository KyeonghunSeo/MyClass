package com.hellowo.myclass.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.hellowo.myclass.R;

public class DialogUtil {

    public static void showAlertDialog(Activity activity, String title, String message,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener,
                                       int iconResourceId){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(activity);
        alertdialog.setMessage(message);

        if(positiveListener != null){
            alertdialog.setPositiveButton(R.string.ok, positiveListener);
        }

        if(negativeListener != null){
            alertdialog.setNegativeButton(R.string.cancel, negativeListener);
        }

        AlertDialog alert = alertdialog.create();
        alert.setTitle(title);

        if(iconResourceId != 0){
            alert.setIcon(iconResourceId);
        }

        alert.show();
    }

}
