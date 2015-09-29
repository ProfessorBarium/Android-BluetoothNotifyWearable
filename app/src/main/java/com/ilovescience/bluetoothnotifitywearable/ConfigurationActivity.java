package com.ilovescience.bluetoothnotifitywearable;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigurationActivity extends AppCompatActivity  {
    //private static Other inst;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Set<String> myContactNumbers;
    Set<NotificationRule> myRules; //maybe this should be an arrayList???
    Button addContact;
    Button addKeywordtext;
    Button addKeywordEmail;
    Button resetSharedPrefs;
    Button addBLEconnectionInfo;
    Button testAT;
    Button addRule;
    EditText contactInput;
    final static String myMacAddress = "B4:99:4C:68:4A:59";
    //static final int PICK_CONTACT_REQUEST = 1;  // The request code

    Context mContext;

    private static ConfigurationActivity inst;

    private BluetoothAdapter mBluetoothAdapter;
    //private BluetoothLeScanner mBluetoothLeScanner;
    private HM10 mHM10;
    //private BroadcastReceiver mAdapterReceiver;
    private String senderForRuleChecking;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        final Context context = this;
        sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        mContext = this;


        addContact = (Button)findViewById(R.id.button_add_contact);
        addKeywordtext = (Button)findViewById(R.id.button_add_keyword_text);
        addKeywordEmail = (Button)findViewById(R.id.button_add_keyword_email_subj);
        resetSharedPrefs = (Button)findViewById(R.id.button_reset);
        contactInput = (EditText)findViewById(R.id.editText_phone_contact);
        addBLEconnectionInfo = (Button)findViewById(R.id.button_AddBLEinfo);
        testAT = (Button)findViewById(R.id.button_AT_SEND);
        addRule = (Button)findViewById(R.id.button_NEW_RULE);

        /*final BluetoothManager manager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();*/

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();


        myContactNumbers = sharedPref.getStringSet(getString(R.string.my_set_saved_Callers), new HashSet<String>());//Retrieve the saved list of phone Numbers
        //INDEX_CONTACT_SHARED_PREFS = getContactIndex(); //look this number up, ALSO in the preference file

        addContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //pickContact();

                /*String newContactString = contactInput.getText().toString();
                String testString;
                String[] testStringArray;
                Set<String> testSet;
                setAddContact(newContactString);
                contactInput.setText("");//clear the input
                testSet = sharedPref.getStringSet(getString(R.string.my_set_saved_Callers), myContactNumbers);*/
                //int testInt = testSet.size();rs);
            }


        });

        resetSharedPrefs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editor.clear();
                editor.commit();
                myContactNumbers = sharedPref.getStringSet(getString(R.string.my_set_saved_Callers),new HashSet<String>());

                //use this to only reset phone numbers
                // myContactNumbers=null;
                // editor.putStringSet(getString(R.string.my_set_saved_Callers), myContactNumbers);
            }
        });
        addBLEconnectionInfo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

            tryConnection();
                //startScan();

            }
        });
        testAT.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mHM10.sendATCommand("AT");
                //startScan();

            }
        });

        addRule.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

             Intent intent = new Intent(mContext,RuleEditActivity.class);
                 mContext.startActivity(intent);

            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                senderForRuleChecking= null;
            } else {
                senderForRuleChecking= extras.getString(Constants.KEY_SENDER);
            }
        } else {
            senderForRuleChecking= (String) savedInstanceState.getSerializable(Constants.KEY_SENDER);
        }

        if(senderForRuleChecking!= null)
        {
            tryConnection();
        }
        else {
            Toast.makeText(this, "KEY_SENDER Extra not found :(", Toast.LENGTH_SHORT).show();
        }


    }




    private void tryConnection()
    {
        if(mBluetoothAdapter.getScanMode()==BluetoothAdapter.SCAN_MODE_CONNECTABLE)
        {
            connectKnownDevice();
        }
        else
        {
            setBluetooth(true);
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
        }
    }




public void onDestroy() {
    super.onDestroy();
    if(mReceiver!=null) {
        unregisterReceiver(mReceiver);
    }

}
/*    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Begin scanning for LE devices

    }*/
public boolean setBluetooth(boolean enable) {

    boolean isEnabled = mBluetoothAdapter.isEnabled();
    if (enable && !isEnabled) {
        return mBluetoothAdapter.enable();

    }
    else if(!enable && isEnabled) {
        return mBluetoothAdapter.disable();
    }
    // No need to change bluetooth state
    return true;
}

    public void connectKnownDevice()
    {
        connectHM10(myMacAddress);
    }

    public void connectHM10(String macAddress)
    {
        mHM10 = new HM10(macAddress,mContext);
    }


    public void checkContactList(Context context, SmsMessage mySMS)
    {
        String address = mySMS.getOriginatingAddress();

        if(address.equals("+16048162747")||address.equals("+16045625376"))
        {
            Toast.makeText(context, "BUZZZZ", Toast.LENGTH_SHORT).show();
            //openApp(context,"com.tumaku.msmble");

        }
        else
        {
            Toast.makeText(context, "Poop", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAddContact(String contactNumber)
    {
        //better way to do this is probably to find the last item in the array, then append... consider an Object (struct?) type with all the info
        //phone number, contact name, buzz, light, light colour, sharedPreferences name
        //int nextAvailableIndex=i; //which digit is next
        //Resources res = getResources();
        //String[] phoneNumbers = res.getStringArray(R.array.caller_array);
        //editor.putString(phoneNumbers[nextAvailableIndex], contactNumber);
        //Set<String> myPhoneNumbers = sharedPref.getStringSet(getString(R.string.set_saved_Callers),new HashSet<String>());//Retrieve the saved list of phone Numbers
        myContactNumbers.add(contactNumber);
        editor.putStringSet(getString(R.string.my_set_saved_Callers),myContactNumbers); //duplicates not allowed, so sharedPrefs StringSet may be shorter than myContactNumbers
        editor.commit();
    }
    public void clearAllSharedPreferences()
    {
        editor.clear();
        editor.commit();

    }
    public int getContactIndex()
    {
        int contactIndex;
        //Set<String> nullSet = new HashSet<String>(){{}};
        Set<String> myPhoneNumbers = sharedPref.getStringSet(getString(R.string.my_set_saved_Callers),new HashSet<String>());//Retrieve the saved list of phone Numbers
        contactIndex = myPhoneNumbers.size();//number of elements stored
        return contactIndex;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//        if (id == R.id.action_addContacts)
//        {
//            //editor.putString(getString(R.string.saved_caller1), "+16045625376");
//            //editor.commit();
//            // String testString = sharedPref.getString(getString(R.string.saved_caller1),"default");
//            //Toast.makeText(this, testString, Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//listens for changes to the state of BluetoothAdapter. When app starts and !BluetoothAdapter.isEnabled(), wait until STATE_ON to trigger the connection

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if(mReceiver!=null) {
                            unregisterReceiver(mReceiver);
                        }
                        Toast.makeText(mContext,"Unregistering", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(mContext,"STATE_ON...please connect", Toast.LENGTH_SHORT).show();
                        connectKnownDevice();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }
            }
        }
    };



}