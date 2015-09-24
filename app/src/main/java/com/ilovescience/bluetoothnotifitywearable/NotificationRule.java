package com.ilovescience.bluetoothnotifitywearable;

/**
 * Created by sbarnum on 9/24/2015.
 */
public class NotificationRule {
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
    }
    public NotificationRule(String contactName,String phoneNumber,String emailAddress,String sKeyword )
    {
        mContactName=contactName;
        mPhoneNumber=phoneNumber;
        mEmailAddress=emailAddress;
        mKeyword=sKeyword;

    }



}
