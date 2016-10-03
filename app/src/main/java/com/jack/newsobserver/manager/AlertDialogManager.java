package com.jack.newsobserver.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.jack.newsobserver.R;
import com.jack.newsobserver.helper.TestNetwork;

public class AlertDialogManager {

    public void alertDialogShow(final Activity activity) {
        AlertDialog.Builder dialogMsg = new AlertDialog.Builder(activity);
        dialogMsg.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_message);
        dialogMsg.setPositiveButton(R.string.dialog_error_ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });
        dialogMsg.show();
    }

    public void noInternetDialogShow(final Activity activity) {
        final AlertDialog.Builder dialogMsg = new AlertDialog.Builder(activity);
        dialogMsg.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_message);
        dialogMsg.setPositiveButton(R.string.dialog_error_retry_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TestNetwork.isNetworkAvailable(activity)) {
                    dialogInterface.dismiss();
                } else {
                    dialogMsg.show();
                }

            }
        });
        dialogMsg.setNegativeButton(R.string.dialog_error_closeapp_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                activity.finish();
            }
        });
        dialogMsg.show();
    }
}
