package com.yso.mybranch.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yso.mybranch.MyApplication;
import com.yso.mybranch.R;

/**
 * Created by Admin on 07-Nov-17.
 */

public final class LocationDialog
{
    public static void showLocDialog(Context context)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setTitle("זיהוי כניסה");
        dialogBuilder.setMessage("הגעת לסניף כעת,\nאנא אשר תחילת פעילות");
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        ImageButton vCheck = dialogView.findViewById(R.id.v_btn);
        vCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
            }
        });

        TextView notThanks = dialogView.findViewById(R.id.n_btn);
        notThanks.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
            }
        });

        if (!((Activity) context).isFinishing() && !b.isShowing())
        {
            b.show();
        }
    }
}
