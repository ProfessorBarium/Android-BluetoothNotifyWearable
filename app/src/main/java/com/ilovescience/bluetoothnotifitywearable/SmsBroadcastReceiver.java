package com.ilovescience.bluetoothnotifitywearable;

/**
 *Code from  http://javapapers.com/android/android-receive-sms-tutorial/
 * Adapted by Professor Barium on 9/2/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


import java.util.HashSet;
import java.util.Set;


public class SmsBroadcastReceiver extends BroadcastReceiver {


    public static final String SMS_BUNDLE = "pdus";
    private static Context mContext;
    //private static Intent mIntent;

    SharedPreferences sharedPref;
    Set<String> myContactNumbers;
    SharedPreferences.Editor editor;
    String smsAddress;




    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            mContext = context;
            //mIntent = intent;

            String smsMessageStr = "";
            SmsMessage firstMessage = SmsMessage.createFromPdu((byte[]) sms[0]);


            //Check for a multiple message SMS... I think.
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String smsBody = smsMessage.getMessageBody().toString();
                smsAddress = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + smsAddress + "\n";
                smsMessageStr += smsBody + "\n";



            }

            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            //ConfigurationActivity inst = new ConfigurationActivity();
            String prefKey = mContext.getString(R.string.preference_file_key); //not sure why I can't call this inside getSharedPreferences...???
            String callerString = mContext.getString(R.string.my_set_saved_Callers); //not sure why I can't call this inside getSharedPreferences...???

            sharedPref = mContext.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
            myContactNumbers = sharedPref.getStringSet(callerString, new HashSet<String>());//Retrieve the saved list of phone Numbers
            if(myContactNumbers.contains(smsAddress))
            {
                Intent myIntent = new Intent(mContext, ConfigurationActivity.class);
                myIntent.putExtra(Constants.KEY_SENDER,smsAddress);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(myIntent);
            }
            else {
                Integer count = myContactNumbers.size();
                String testString = count.toString();
                String[] aStrings = new String[myContactNumbers.size()];
                aStrings = myContactNumbers.toArray(aStrings);

                Toast.makeText(mContext, "Hashset Length" + testString, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Incoming address" + smsAddress, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < count; i++) {

                    Toast.makeText(mContext, "Saved address" + aStrings[i], Toast.LENGTH_SHORT).show();
                }

                //inst.updateList(smsMessageStr);
                //inst.checkSender(context,firstMessage);
            }

        }
    }


}