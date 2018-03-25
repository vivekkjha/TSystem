package com.vivek.tsystem.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by vivekjha on 17/11/15.
 */
public class Utility {


    public static void hideSoftKeyboard(View view) {
        hideSoftKeyboard(null, view);
    }
    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
    public static void hideSoftKeyboard(Window wd, View view) {
        int flag = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        if (wd != null) {
            wd.setSoftInputMode(wd.getAttributes().softInputMode &~ flag);
        }
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!imm.isActive()) return;
        final IBinder windowToken = view.getWindowToken();
        if (!imm.hideSoftInputFromWindow(windowToken, 0)) {
            if (!imm.isActive()) {
                return;
            }
            //System.out.println("Toggle");
            Activity activity = scanForActivity(view.getContext());
            if (activity != null) {
                Window window = activity.getWindow();
                window.setSoftInputMode(window.getAttributes().softInputMode &~ flag);
            }
        }
    }

    public static void showKeyboard(EditText text) {
        showKeyboard(null, text);
    }
    public static void showKeyboard(Window wd, EditText text) {
        int flag = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        if (wd != null) {
            wd.setSoftInputMode(wd.getAttributes().softInputMode &~ flag);
        }
        text.clearFocus();
        text.requestFocus();
        Context context = text.getContext();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
        //System.out.println("show keyboard" + text);
    }

    private static Toast mToast;
    public static void showToast(Context context, String message)
    {
        if(context == null) return;
        showToast(context,message,false);
    }

    public static void showToast(Context context, String message, boolean longLength)
    {
        if(context == null) return;
        if(mToast!=null)
            mToast.cancel();
        mToast = Toast.makeText(context,message,longLength? Toast.LENGTH_LONG: Toast.LENGTH_SHORT);
        mToast.show();
    }
}
