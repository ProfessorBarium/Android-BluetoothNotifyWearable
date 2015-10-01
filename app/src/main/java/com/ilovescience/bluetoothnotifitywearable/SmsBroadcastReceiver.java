package com.ilovescience.bluetoothnotifitywearable;

/**
 *Code from  http://javapapers.com/android/android-receive-sms-tutorial/
 * Adapted by Professor Barium on 9/2/2015.
 */

import com.google.gson.Gson;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;




import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SmsBroadcastReceiver extends BroadcastReceiver {


    public static final String SMS_BUNDLE = "pdus";
    private static Context mContext;
    //private static Intent mIntent;

    SharedPreferences sharedPref;
    //Set<String> myContactNumbers;
    List<String> myContactNumbers;
    SharedPreferences.Editor editor;
    String smsAddress;
    String smsBody;





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

                smsBody = smsMessage.getMessageBody().toString();
                smsAddress = smsMessage.getOriginatingAddress();

                smsMessageStr += "SMS From: " + smsAddress + "\n";
                smsMessageStr += smsBody + "\n";



            }

            //Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();
            //ConfigurationActivity inst = new ConfigurationActivity();
           //String callerString = mContext.getString(R.string.my_set_saved_Callers); //not sure why I can't call this inside getSharedPreferences...???

            //TODO:Create a class-wide reconstructRulesArray() method
            sharedPref = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            Set<String> myRulesSet = sharedPref.getStringSet(Constants.KEY_RULES, new HashSet<String>());//Retrieve the saved list of phone Numbers
            List<String> myRulesList = new ArrayList<>(myRulesSet);
            int ruleCount = myRulesSet.size();

            String[] ruleStringArray = myRulesSet.toArray(new String[ruleCount]);

            myContactNumbers = new ArrayList<>();

            NotificationRule[] myRulesObjects = new NotificationRule[ruleCount];

            for (int i =0; i < ruleCount; i++)
            {
                myRulesObjects[i] = new Gson().fromJson(ruleStringArray[i],NotificationRule.class);
                myContactNumbers.add(myRulesObjects[i].getmPhoneNumber());
            }


            //myContactNumbers = sharedPref.getStringSet(Constants.KEY_RULES, new HashSet<String>());//Retrieve the saved list of phone Numbers

            //returns -1 if not found
            int ruleIndex = myContactNumbers.indexOf(smsAddress);
            //int ruleIndex = myRulesList.indexOf(smsAddress);



            if(ruleIndex != -1  && checkKeyword(myRulesObjects[ruleIndex]))
            {
                String ruleAsString = new Gson().toJson(myRulesObjects[ruleIndex]);
                Intent myIntent = new Intent(mContext, ConfigurationActivity.class);
                //myIntent.putExtra(Constants.KEY_SENDER, smsAddress);
                myIntent.putExtra(Constants.KEY_TRIGGERING_RULE_INDEX,ruleIndex);
                myIntent.putExtra(Constants.KEY_TRIGGERING_RULE,ruleAsString);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(myIntent);
            }
           else {
                Toast.makeText(mContext, "Rule conditions not met :(", Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, myRulesObjects[0].getmPhoneNumber(), Toast.LENGTH_SHORT).show();
/*                Integer count = myContactNumbers.size();
                String testString = count.toString();
                String[] aStrings = new String[myContactNumbers.size()];
                aStrings = myContactNumbers.toArray(aStrings);

                Toast.makeText(mContext, "Hashset Length" + testString, Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "Incoming address" + smsAddress, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < count; i++) {

                    Toast.makeText(mContext, "Saved address" + aStrings[i], Toast.LENGTH_SHORT).show();*/


                //inst.updateList(smsMessageStr);
                //inst.checkSender(context,firstMessage);
            }


        }
    }

    //Returns true if usesKeyword is false, or if true and keyword is present
private boolean checkKeyword(NotificationRule thisRule)
{
    //not defined
    if(thisRule.getmKeyword().length()==0) {
        return true;
    }
    else if(smsBody.contains(thisRule.getmKeyword())) {
        return  true;
    }
    else
    {
        return false;
    }



}

}