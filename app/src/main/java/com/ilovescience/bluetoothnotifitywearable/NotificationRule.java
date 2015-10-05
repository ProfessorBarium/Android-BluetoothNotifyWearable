package com.ilovescience.bluetoothnotifitywearable;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sbarnum on 9/24/2015.
 */
public class NotificationRule  {
    private String mContactName;



    private String mPhoneNumber;
    private String mEmailAddress;
    private String mKeyword;
    private String mPwmCommand;
    private int mVibrationPattern;
    private int mVibrationDuration;

    public NotificationRule(String contactName)
    {
        mContactName=contactName;
    }

    public NotificationRule(String contactName,String phoneNumber,String emailAddress )
    {
        mContactName=contactName;
        mPhoneNumber=phoneNumber;
        mEmailAddress=emailAddress;
        mKeyword = "";
        mVibrationPattern = 0;
        mVibrationDuration=0;
    }
    public NotificationRule(String contactName,String phoneNumber,String emailAddress,String sKeyword )
    {
        mContactName=contactName;
        mPhoneNumber=phoneNumber;
        mEmailAddress=emailAddress;
        mKeyword=sKeyword;


    }

    public String getmContactName() {
        return mContactName;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmEmailAddress() {
        return mEmailAddress;
    }

    public void setmEmailAddress(String mEmailAddress) {
        this.mEmailAddress = mEmailAddress;
    }

    public String getmKeyword() {
        return mKeyword;
    }

    public void setmKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    public String getmPwmCommand() {
        return mPwmCommand;
    }

    public void setmPwmCommand(String mPwmCommand) {
        this.mPwmCommand = mPwmCommand;
    }

    public int getmVibrationPattern() {
        return mVibrationPattern;
    }

    public void setmVibrationPattern(int mVibrationPattern) {
        this.mVibrationPattern = mVibrationPattern;
    }

    public int getmVibrationDuration() {
        return mVibrationDuration;
    }

    public void setmVibrationDuration(int mVibrationDuration) {
        this.mVibrationDuration = mVibrationDuration;
    }

    static protected NotificationRule[] reconstructRules(Context context)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        Set<String> myRulesSet = sharedPref.getStringSet(Constants.KEY_RULES, new HashSet<String>());//Retrieve the saved list of phone Numbers
        int ruleCount = myRulesSet.size();

        String[] ruleStringArray = myRulesSet.toArray(new String[ruleCount]);

        NotificationRule[] myRulesObjects = new NotificationRule[ruleCount];

        for (int i =0; i < ruleCount; i++)
        {
            myRulesObjects[i] = new Gson().fromJson(ruleStringArray[i],NotificationRule.class);

        }
        return myRulesObjects;
    }

    String makeGson()
    {
        return new Gson().toJson(this);
    }

}
