package com.ilovescience.bluetoothnotifitywearable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 9/22/2015.
 */
public class RuleEditActivity extends Activity{

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.DATA
    };

    Context mContext;
    EditText personName;
    EditText phoneInput;
    EditText emailInput;
    EditText keywordInput;
    Spinner vibrateTypeSpinner;
    Spinner vibrateDurationSpinner;
    Spinner colorSpinner;

    Button buttonGetContact;
    Button buttonSetRule;
    Button buttonEraseRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule);
        mContext = this;
        personName = (EditText)findViewById(R.id.editText_contact_name);
        phoneInput = (EditText)findViewById(R.id.editText_contact_phone);
        emailInput = (EditText)findViewById(R.id.editText_contact_email);
        keywordInput = (EditText)findViewById(R.id.editText_keyword);
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

        buttonGetContact = (Button)findViewById(R.id.button_Get_Contact);
        buttonSetRule = (Button)findViewById(R.id.button_set_rule);
        buttonEraseRule= (Button)findViewById(R.id.button_Erase_rule);

        buttonGetContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              /*  Intent openContactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); //not sure what this does..
                startActivityForResult(openContactsIntent,Constants.PICK_CONTACT_REQUEST);*/
                pickContact();
            }
        });

        buttonSetRule.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              /*  Intent openContactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); //not sure what this does..
                startActivityForResult(openContactsIntent,Constants.PICK_CONTACT_REQUEST);*/
               NotificationRule myRule = new NotificationRule(personName.getText().toString(),phoneInput.getText().toString(),emailInput.getText().toString(),keywordInput.getText().toString());
                Toast.makeText(mContext,myRule.getmContactName(),Toast.LENGTH_LONG).show();

            }
        });

    }
    private void pickContact()
    {
        //Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        //pickContactIntent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        //pickContactIntent.setTypeAndNormalize(ContactsContract.Contacts.CONTENT_TYPE);
        //startActivityForResult(pickContactIntent, Constants.PICK_CONTACT_REQUEST);
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, Constants.PICK_CONTACT_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.PICK_CONTACT_REQUEST) {

                Cursor cursor = null;
                String phoneNumber = "";
                List<String> allNumbers = new ArrayList<String>();
                int phoneIdx = 0;
                try {
                    Uri result = data.getData();
                    personName.setText(nameFromUri(result));

                    //TODO: impliment email in the same way as phone, to give options.
                    emailInput.setText(getEmailFromUri(result));

                    //find contact id that was selected - unique integer value
                    String id = result.getLastPathSegment();

                    cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                    phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                    if (cursor.moveToFirst()) {
                        while (cursor.isAfterLast() == false) {
                            phoneNumber = cursor.getString(phoneIdx);
                            allNumbers.add(phoneNumber);
                            cursor.moveToNext();
                        }
                    } else {
                        //no results actions
                    }
                } catch (Exception e) {
                    //error actions
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }

                    final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RuleEditActivity.this);
                    builder.setTitle("Choose a number");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedNumber = items[item].toString();
                            //selectedNumber = selectedNumber.replace("-", "");
                            phoneInput.setText(cleanPhoneNumber(selectedNumber));
                        }
                    });
                    AlertDialog alert = builder.create();
                    if(allNumbers.size() > 1) {
                        alert.show();
                    } else {
                        String selectedNumber = phoneNumber.toString();
                        selectedNumber = selectedNumber.replace("-", "");
                        phoneInput.setText(selectedNumber);
                    }

                    if (phoneNumber.length() == 0) {
                        //no numbers found actions
                    }
                }
            }
        } else {
            //activity result error actions
        }

    }





    private String nameFromUri(Uri contactUri) {
        String name;
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();

        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        name = cursor.getString(column);
        cursor.close();
        return name;
    }

    private String phoneFromUri(Uri contactUri)
    {
        // We only need the NUMBER column, because there will be only one row in the result
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for the given URI)
        // CAUTION: The query() method should be called from a separate thread to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
        // Consider using CursorLoader to perform the query.????
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, null, null, null);
        cursor.moveToFirst();

        // Retrieve the phone number from the NUMBER column
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String number = cursor.getString(column);
        cursor.close();
        return number;
    }

    private String getEmailFromUri(Uri contactUri)
    {
        String id = contactUri.getLastPathSegment();
        int emailIndex = 0;
        String foundEmail = id;

        //Toast.makeText(this, id,Toast.LENGTH_LONG).show();
        Cursor cursor = null;
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id }, null);
        if (cursor.moveToFirst()) {

                foundEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));


        }
        return foundEmail;

    }

    private String emailFromUri(Uri contactUri) {
        //String[] projection = {ContactsContract.CommonDataKinds.Email.CONTACT_ID};

        //Cursor cursor = getContentResolver()
          //      .query(contactUri, projection, null, null, null);
       // cursor.moveToFirst();
        String id = contactUri.getLastPathSegment();
Cursor cursor = getContentResolver().
        query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,  null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?", new String[] { id }, null);
        int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

        String address = cursor.getString(column);
        return address;
    }

        //TO-DO: use one method to retreive all data, like this
/*             ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, null);
        String address = "";
        if (cursor != null) {
            try {
                final int contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int emailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                long contactId;
                String displayName;


                    contactId = cursor.getLong(contactIdIndex);
                    displayName = cursor.getString(displayNameIndex);
                    address = cursor.getString(emailIndex);

            } finally {
                cursor.close();
            }


        }  */

    //currently only implemented for North American phone numbers
    protected static String cleanPhoneNumber(String dirtyNumber)
    {
        String formattedNumber;
        int dirtyLength = dirtyNumber.length();
        StringBuilder mStringBuilder = new StringBuilder("+");
        Character mCharacter;
        for(int i=0; i <dirtyLength; i++)
        {
            mCharacter = dirtyNumber.charAt(i);
            if(Character.isDigit(mCharacter)) {
                mStringBuilder.append(mCharacter);
            }
        }
        if(mStringBuilder.length()==11) { //it is missing the country code
            mStringBuilder.insert(1, Constants.AREA_CODE_NORTH_AMERICA);
        }
        formattedNumber = mStringBuilder.toString();
        return formattedNumber;
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
