package com.ilovescience.bluetoothnotifitywearable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Sam on 9/22/2015.
 */
public class RuleEditActivity extends Activity{
    EditText personName;
    EditText phone;
    EditText email;
    EditText keyword;
    Spinner vibrateTypeSpinner;
    Spinner vibrateDurationSpinner;
    Spinner colorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        personName = (EditText)findViewById(R.id.editText_contact_name);
        phone = (EditText)findViewById(R.id.editText_contact_phone);
        email = (EditText)findViewById(R.id.editText_contact_email);
        keyword = (EditText)findViewById(R.id.editText_keyword);
        vibrateTypeSpinner = (Spinner)findViewById(R.id.spinner_buzz_type);
        vibrateDurationSpinner = (Spinner)findViewById(R.id.spinner_duration);
        colorSpinner = (Spinner)findViewById(R.id.spinner_Color);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,R.array.buzz_pattern,android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vibrateTypeSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,R.array.buzz_duration,android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vibrateDurationSpinner.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,R.array.indicator_color,android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter3);

    }
/*
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onRestart() {
        super.onRestart();

    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }*/



}
