package com.jack.newsobserver.manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.jack.newsobserver.R;

public class AlertDialogManager {

    public void alertDialogShow(Context context) {
        final AlertDialog.Builder dialogMsg = new AlertDialog.Builder(context);
        dialogMsg.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_message);
        dialogMsg.setPositiveButton(R.string.dialog_error_ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogMsg.show();
    }
}
