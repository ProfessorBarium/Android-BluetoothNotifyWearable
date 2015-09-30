package com.ilovescience.bluetoothnotifitywearable;

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

    private boolean usesKeyword;


    public NotificationRule(String contactName)
    {
        mContactName=contactName;
    }

    public NotificationRule(String contactName,String phoneNumber,String emailAddress )
    {
        mContactName=contactName;
        mPhoneNumber=phoneNumber;
        mEmailAddress=emailAddress;
        usesKeyword = false;
    }
    public NotificationRule(String contactName,String phoneNumber,String emailAddress,String sKeyword )
    {
        mContactName=contactName;
        mPhoneNumber=phoneNumber;
        mEmailAddress=emailAddress;
        mKeyword=sKeyword;
        usesKeyword = true;
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

}
