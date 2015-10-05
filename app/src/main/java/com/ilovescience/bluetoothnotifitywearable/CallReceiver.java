package com.ilovescience.bluetoothnotifitywearable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 9/27/2015.
 */
public class CallReceiver extends BroadcastReceiver {



    public void onReceive(Context context, Intent intent){

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);                         // 3
        String msg = "Phone state changed to " + state;

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {                                   // 4
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);  // 5
            NotificationRule[] myRules = NotificationRule.reconstructRules(context);

            List<String> myContactNumbers = new ArrayList<>();

            for(int i =0; i<myRules.length; i++)
            {
                myContactNumbers.add(myRules[i].getmPhoneNumber());
            }

            int ruleIndex = myContactNumbers.indexOf(incomingNumber);

            if(ruleIndex> -1)
            {
                BLEConnectionService.startBLEConnectionService(context,myRules[ruleIndex].makeGson());
            }


 /*           msg += ". Incoming number is " + incomingNumber;
            Intent answerReject = new Intent(context, AnswerCallActivity.class);
            answerReject.putExtra(Constants.KEY_SENDER, incomingNumber);
            answerReject.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(answerReject);*/


        }



        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}