package com.handsomezhou.appsearch.dialog;

import android.app.ProgressDialog;
import android.content.Context;

public class BaseProgressDialog extends ProgressDialog {
    public BaseProgressDialog(Context context) {
        super(context);
        setCanceledOnTouchOutside(true);
    }
    
    public void show(String message){
        this.setMessage(message);
        this.show();
    }
    
    public void hide(){
        this.dismiss();
      
    }
   
}
