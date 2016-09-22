
package com.handsomezhou.appsearch.dialog;

import com.handsomezhou.appsearch.R;

import android.app.ProgressDialog;
import android.content.Context;

public class BaseProgressDialog extends ProgressDialog {
    public BaseProgressDialog(Context context) {
        super(context, R.style.progress_dialog);

        setCanceledOnTouchOutside(true);
    }

    public void show(String message) {
        this.setMessage(message);
        this.show();
    }

    public void hide() {
        this.dismiss();

    }

}
