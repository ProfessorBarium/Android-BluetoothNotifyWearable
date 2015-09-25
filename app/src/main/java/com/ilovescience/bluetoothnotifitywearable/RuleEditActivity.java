package com.ilovescience.bluetoothnotifitywearable;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
    EditText phone;
    EditText email;
    EditText keyword;
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

        buttonGetContact = (Button)findViewById(R.id.button_Get_Contact);
        buttonSetRule = (Button)findViewById(R.id.button_set_rule);
        buttonEraseRule= (Button)findViewById(R.id.button_Erase_rule);

        buttonGetContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent openContactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); //not sure what this does..
                pickContact();
            }
        });

    }
    private void pickContact()
    {
        //Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, Constants.PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.PICK_CONTACT_REQUEST) {
         /*       Cursor cursor = null;
                String sEmail="";
                String sName="";

                try{
                    Uri result= data.getData();
                    Log.v("DEBUG_TAG", "Got a contact result: " + result.toString());
                    String id = result.getLastPathSegment();

                    cursor = getContentResolver()
                            .query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "/?",new String[] {id},null);
                    int nameID = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int emailIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

                    if (cursor.moveToFirst())
                    {
                        sEmail = cursor.getString(emailIdx);
                        sName = cursor.getString(nameID);
                    }
                    else{
                        Log.w("MY DEBUGTAG", "No results");
                    }
                }catch (Exception e){
                    Log.e("MY DEBUGTAG","Failed to get email data", e);
                }
                finally{
                    if(cursor!=null)
                    {
                        cursor.close();
                    }
                    email.setText(sEmail);
                    personName.setText(sName);

                    if (sEmail.length() == 0 && sName.length() == 0)
                    {
                        Toast.makeText(this, "No Email for Selected Contact",Toast.LENGTH_LONG).show();
                    }

                }
*/
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                String temp = phoneFromUri(contactUri);
                String cleanedNumber = cleanPhoneNumber(temp);
                phone.setText(cleanedNumber);

                String name = nameFromUri(contactUri);
                personName.setText(name);

               String myEmail = emailFromUri(contactUri);
                email.setText(myEmail);
                keyword.setText("@urgent");

            }
        }else{
            Log.w("DEBUG_TAG", "Warning: activity result not ok");
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
