package com.yso.mybranch.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yso.mybranch.MyApplication;
import com.yso.mybranch.R;

public class LocationDialog extends AppCompatActivity implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_dialog);

        ImageButton vCheck = findViewById(R.id.v_btn);
        vCheck.setOnClickListener(this);
        TextView noText = findViewById(R.id.n_btn);
        noText.setOnClickListener(this);
        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        LayoutInflater inflater = (LayoutInflater) MyApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setTitle("זיהוי כניסה");
        dialogBuilder.setMessage("הגעת לסניף כעת,\nאנא אשר תחילת פעילות");
        dialogBuilder.setView(dialogView);

        final AlertDialog b = dialogBuilder.create();
//        b.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        b.setCancelable(false);
        ImageButton vCheck = dialogView.findViewById(R.id.v_btn);
        vCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
                finish();
            }
        });

        TextView notThanks = dialogView.findViewById(R.id.n_btn);
        notThanks.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                b.dismiss();
                finish();
            }
        });

        if (!isFinishing() && !b.isShowing())
        {
            b.show();
        }*/
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.v_btn:
            case R.id.n_btn:
                finish();
                break;
        }
    }
}
