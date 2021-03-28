package com.gazitf.etapp.utils;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.gazitf.etapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/*
 * @created 27/03/2021 - 7:28 PM
 * @project EtApp
 * @author fnkaya
 */
public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
        setupAlertDialog();
    }

    private void setupAlertDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);
        alertDialog = builder.create();
    }

    public void startDialog() {
        alertDialog.show();
    }

    public void dismissDialog() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }
}
