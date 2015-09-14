
package com.handsomezhou.appsearch.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.handsomezhou.appsearch.R;

public abstract class BaseDialog extends AlertDialog {
    private int mViewSpacingLeft = 0;
    private int mViewSpacingTop = 0;
    private int mViewSpacingRight = 0;
    private int mViewSpacingBottom = 0;

    protected abstract View getView();

    public BaseDialog(Context context) {

        super(context, R.style.dialog);
        View view = getView();
        new Builder(getContext()).create();

        this.setView(view, mViewSpacingLeft, mViewSpacingTop, mViewSpacingRight, mViewSpacingBottom);
        this.setCanceledOnTouchOutside(false);

    }

}
