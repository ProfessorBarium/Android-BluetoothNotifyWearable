/* Created by Javier Montaner  (twitter: @tumaku_) during M-week (February 2014) of MakeSpaceMadrid
 * http://www.makespacemadrid.org
 * @ 2014 Javier Montaner
 * 
 * Licensed under the MIT Open Source License 
 * http://opensource.org/licenses/MIT
 * 
 * Many thanks to Yeelight (special mention to Daping Liu) and Double Encore (Dave Smith)
 * for their support and shared knowlegde
 * 
 * Based on the API released by Yeelight:
 * http://www.yeelight.com/en_US/info/download
 * 
 * Based on the code created by Dave Smith (Double Encore):
 * https://github.com/devunwired/accessory-samples/tree/master/BluetoothGatt
 * http://www.doubleencore.com/2013/12/bluetooth-smart-for-android/
 * 
 * 
 * Scan Bluetooth Low Energy devices and their services and characteristics.
 * If the Yeelight Service is found, an activity can be launched to control colour and intensity of Yeelight Blue bulb
 * 
 * Tested on a Nexus 7 (2013)
 * 
 */
package com.ilovescience.bluetoothnotifitywearable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HM10  {
	
	/* SensorTag BLE procedure:
	 * The sensors in SensorTag require a special mechanism to use them.
	 * In order to save battery power, every sensor needs to be enabled (i.e. activated) prior 
	 * to reading it or subscribing to notifications on value changes.
	 * This is done by writing a byte into a characteristic present in every service (*_CONF)
	 * Once the sensor is enabled, standard BLE mechanisms apply:
	 * - you can read its value
	 * - you can (un)subscribe to notifications  writing the *-DATA characteristic descriptor
	 * 
	 * A special case is the Key service that controls the two buttons on sensor tag. This service 
	 * does not require to be enabled. To interact with this service, only the notification mechanism 
	 * applies (read value is not supported - to be confirmed)
	 */


    private final static int WSTATE_CONNECT = 0;
    private final static int WSTATE_SEARCH_SERVICES = 1;
    private final static int WSTATE_NOTIFY_KEY = 2;
    private final static int WSTATE_READ_KEY = 3;
    private final static int WSTATE_DUMMY = 4;
    private final static int WSTATE_WRITE_KEY = 5;
    private final static String AT_IO_STATE = "AT+COL??";

    private final static int INPUT_PIN_COUNT = 8;

    private TextView mTextReceived;
    private TextView mTextLongReceived;
    //private EditText mTextSent;
    private String mSendString;
    private String mPreviousTextSent;
    private TextView mTextInfo;
    private TextView mTextNotification;
    private Context mContext;
    private HM10BroadcastReceiver mBroadcastReceiver;
    private String mDeviceAddress;
    private int mState = 0;


    private TumakuBLE mTumakuBLE = null;


    int myCurrentPinStates = 0;
    int myPwrOnPinStates = 0;
    int myOnConnectedPinStates = 0;


    public HM10(String deviceAddress, Context context)
	{
        mContext = context;
        mDeviceAddress = deviceAddress;

        mTumakuBLE = mTumakuBLE.getInstance(context);
        mTumakuBLE.setDeviceAddress(mDeviceAddress);



        if (mDeviceAddress == null) {
            if (Constant.DEBUG)
                Log.i("JMG", "No device address received to start SensorTag Activity");

        }
        //mTumakuBLE = ((TumakuBLEApplication) getApplication()).getTumakuBLEInstance(this);



		mBroadcastReceiver = new HM10BroadcastReceiver();
		IntentFilter filter = new IntentFilter(TumakuBLE.WRITE_SUCCESS);
		filter.addAction(TumakuBLE.READ_SUCCESS);
		filter.addAction(TumakuBLE.DEVICE_CONNECTED);
		filter.addAction(TumakuBLE.DEVICE_DISCONNECTED);
		filter.addAction(TumakuBLE.SERVICES_DISCOVERED);
		filter.addAction(TumakuBLE.NOTIFICATION);
		filter.addAction(TumakuBLE.WRITE_DESCRIPTOR_SUCCESS);
		mContext.registerReceiver(mBroadcastReceiver, filter);//this probably isn't correct... mContext not defined

		if (mTumakuBLE.isConnected()){
			mState=WSTATE_NOTIFY_KEY;
			nextState();
			updateInfoText("Resume connection to device");
		} else {
			mState=WSTATE_CONNECT;
			nextState();
			updateInfoText("Start connection to device");
		}


    }
	public void sendATCommand(String ATcommand) {
		if ((mState == WSTATE_DUMMY)) {
			mState = WSTATE_WRITE_KEY;
			mPreviousTextSent = ATcommand;
            mSendString = ATcommand;
			nextState();
		} else
			Toast.makeText(mContext, "Cannot send data in current state. Do a reset first.", Toast.LENGTH_SHORT).show();
	}

	public void reconnect() {
        mState = WSTATE_CONNECT;
        mTumakuBLE.resetTumakuBLE();
        mTumakuBLE.setDeviceAddress(mDeviceAddress);
        nextState();
    }



	void disconnect()
	{
        mTumakuBLE.disconnect();
	}

//Do the disconnecting in the parent application
/*	@Override
	public void onStop(){
		super.onStop();
        this.unregisterReceiver(this.mBroadcastReceiver);      
	}*/




	protected void nextState(){
			switch(mState) {			   
			   case (WSTATE_CONNECT):
			       if (Constant.DEBUG) Log.i("JMG", "State Connected");
                   //Toast.makeText(mContext, "Trying to connect??!.", Toast.LENGTH_SHORT).show();
			       mTumakuBLE.connect();
			       break;
			   case(WSTATE_SEARCH_SERVICES):
			       if (Constant.DEBUG) Log.i("JMG", "State Search Services");
				   mTumakuBLE.discoverServices();
			       break;			   
			   case(WSTATE_READ_KEY):
			       if (Constant.DEBUG) Log.i("JMG", "State Read Key");
			   	   mTumakuBLE.read(TumakuBLE.SENSORTAG_KEY_SERVICE,TumakuBLE.SENSORTAG_KEY_DATA);
				   break;
			   case(WSTATE_NOTIFY_KEY):
			       if (Constant.DEBUG) Log.i("JMG", "State Notify Key");
		   	       mTumakuBLE.enableNotifications(TumakuBLE.SENSORTAG_KEY_SERVICE,TumakuBLE.SENSORTAG_KEY_DATA,true);
				   break;
			   case(WSTATE_WRITE_KEY):
				   //String tmpString=mTextSent.getText().toString();
                   String tmpString = mSendString;
			   	   //mTextSent.setText("");

			       if (Constant.DEBUG) Log.i("JMG", "State Write State " + tmpString);
			       byte tmpArray []= new byte[tmpString.length()];
			       for (int i=0; i<tmpString.length();i++) tmpArray[i]=(byte)tmpString.charAt(i);
		   	       mTumakuBLE.write(TumakuBLE.SENSORTAG_KEY_SERVICE,TumakuBLE.SENSORTAG_KEY_DATA, tmpArray);
				   break;

			   default:
				   
			}			
			
		}
		protected void updateInfoText(String text) {
			//mTextInfo.setText(text);
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}

		protected void updateNotificationText(String text) {
            //mTextNotification.setText(text);
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}

		protected void displayText(String text) {
			//mTextReceived.setText(text);
            Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
  


    private class HM10BroadcastReceiver extends BroadcastReceiver {

	    public String bytesToString(byte[] bytes){
	    	  StringBuilder stringBuilder = new StringBuilder(
	                    bytes.length);
	            for (byte byteChar : bytes)
	                stringBuilder.append(String.format("%02X ", byteChar));
	            return stringBuilder.toString();
	    }

        private void ATreplyToAction(String numericalATreply)//Only called in response to AT commands
        {
            int ATvalue = Integer.parseInt(numericalATreply, 16);
            int previousMessageLength = mPreviousTextSent.length();


            if(previousMessageLength != 2) //Applies to the "OK" reply only
            {
                String sentShort = mPreviousTextSent.substring(3,6);
                switch (sentShort) {
                    case "AFT": //consider adding strings to the global namespace
                        myOnConnectedPinStates = ATvalue;
                    case "BEF":
                        myPwrOnPinStates = ATvalue;
                        default:
                }
            }
        }
		public String ATtoDescriptor(String ATstring)
		{

			String replyType = "";
			int replyValue;
			String myDescriptor = "";
			if (ATstring.length() <4)
			{
				myDescriptor = "Connection is OK";
			}
			else {
				replyType = ATstring.substring(3,6); //some discriptors are only 3 characters long
				switch (replyType)
				{
					case "Col":
						myDescriptor = "Pin Values:";
						replyValue = Integer.parseInt(ATstring.substring(7), 16); //data is given in hex
						StringBuilder bitBuilder = new StringBuilder(myDescriptor);
						for (int i =INPUT_PIN_COUNT; i > 0; i--)
						{
							bitBuilder.append(replyValue>> (i-1) & 1);
						}
							myDescriptor = bitBuilder.toString();
						break;
					case "Set":
						myDescriptor = "Successfully changed!";
                        ATreplyToAction(ATstring.substring(7));
                        break;
                    case "Get":
                        myDescriptor = "Data received. Variables updated";
                        ATreplyToAction(ATstring.substring(7));
                        break;
                    case "ADC":
                            myDescriptor = "Voltage on pin ";
                            int pinNumber = Integer.parseInt(ATstring.substring(6, 7), 16);
                            StringBuilder floatBuilder = new StringBuilder(myDescriptor);
                            float voltageFloat = Float.parseFloat(ATstring.substring(8, 12));
                            floatBuilder.append(pinNumber);
                            floatBuilder.append(": ");
                            floatBuilder.append(voltageFloat);
                            myDescriptor = floatBuilder.toString();
                            break;
					default:
						myDescriptor ="Unknown AT Response";

				}
			}
			return myDescriptor;
		}

		/*
		Each value pin is summative
		80 = PIO4 ,pin 27, P0_7
		40 = PIO5, pin 28, P0_6
		20 = PIO6, pin 29, P0_5
		10 = PIO7, pin 30, P0_4
		08 = PIO8, pin 31, P0_3
		04 = PIO9, pin 32, P0_2
		02 = PIOA, pin 33, P0_1
		01 = PIOB, pin 34, P0_0
		 */



			@Override
       public void onReceive(Context context, Intent intent) {
           if (intent.getAction().equals(TumakuBLE.DEVICE_CONNECTED)) {
		       if (Constant.DEBUG) {
                   Log.i("JMG", "DEVICE_CONNECTED message received");
                   updateInfoText("Received connection event");
               }
        	   mState=WSTATE_SEARCH_SERVICES;
        	   nextState();
        	   return;
           }
           if (intent.getAction().equals(TumakuBLE.DEVICE_DISCONNECTED)) {
		       if (Constant.DEBUG) Log.i("JMG", "DEVICE_DISCONNECTED message received");
		       //This is an unexpected device disconnect situation generated by Android BLE stack
		       //Usually happens on the service discovery step :-(
		       //Try to reconnect
	    	   String fullReset=intent.getStringExtra(TumakuBLE.EXTRA_FULL_RESET);
	    	   if (fullReset!=null){
			       if (Constant.DEBUG) Log.i("JMG", "DEVICE_DISCONNECTED message received with full reset flag");
	    		   Toast.makeText(mContext, "Unrecoverable BT error received. Launching full reset", Toast.LENGTH_SHORT).show();
	        	   mState=WSTATE_CONNECT;
	       		   mTumakuBLE.resetTumakuBLE();
	       		   mTumakuBLE.setDeviceAddress(mDeviceAddress);
	       		   mTumakuBLE.setup();
	        	   nextState();
	        	   return;	    		   
	    	   } else {		       		       
			       if (mState!=WSTATE_CONNECT){
		    		   Toast.makeText(mContext, "Device disconnected unexpectedly. Reconnecting.", Toast.LENGTH_SHORT).show();
		        	   mState=WSTATE_CONNECT;
		       		   mTumakuBLE.resetTumakuBLE();
		       		   mTumakuBLE.setDeviceAddress(mDeviceAddress);
		        	   nextState();
		        	   return;
			       }
	    	   }
           }
           if (intent.getAction().equals(TumakuBLE.SERVICES_DISCOVERED)) {
		       if (Constant.DEBUG) Log.i("JMG", "SERVICES_DISCOVERED message received");
		       
        	   updateInfoText("Received services discovered event");
        	   mState=WSTATE_NOTIFY_KEY;
        	   nextState();
        	   return;
           }

           if (intent.getAction().equals(TumakuBLE.READ_SUCCESS)) {
		       if (Constant.DEBUG) Log.i("JMG", "READ_SUCCESS message received");
		       String readValue= intent.getStringExtra(TumakuBLE.EXTRA_VALUE);
		       byte [] readByteArrayValue= intent.getByteArrayExtra(TumakuBLE.EXTRA_VALUE_BYTE_ARRAY);
		
		       if (readValue==null) updateInfoText("Received Read Success Event but no value in Intent"  );
		       else {
		    	   updateInfoText("Received Read Success Event: " + readValue);
		       }
		       if (readValue==null) readValue="null";

        	   if (mState==WSTATE_READ_KEY) {
        		   if (readByteArrayValue!=null) displayText(readValue);
        		   mState=WSTATE_DUMMY;
        		   nextState();
        		   return;
        	   }
        	   return;
           }

           if (intent.getAction().equals(TumakuBLE.WRITE_SUCCESS)) {
		       if (Constant.DEBUG) Log.i("JMG", "WRITE_SUCCESS message received");
        	   updateInfoText("Received Write Success Event");
        	   if (mState==WSTATE_WRITE_KEY) {
        		   mState=WSTATE_DUMMY;
        		   nextState();
        		   return;
        	   }    
               return;
           }                
           
           if (intent.getAction().equals(TumakuBLE.NOTIFICATION)) {
		       String notificationValue= intent.getStringExtra(TumakuBLE.EXTRA_VALUE);
		       String characteristicUUID= intent.getStringExtra(TumakuBLE.EXTRA_CHARACTERISTIC);
			   byte [] notificationValueByteArray =  intent.getByteArrayExtra(TumakuBLE.EXTRA_VALUE_BYTE_ARRAY);

			   if (notificationValue==null) notificationValue="NULL";
		       if (characteristicUUID==null) characteristicUUID="MISSING";
 		       if (Constant.DEBUG) {
 		    	   Log.i("JMG", "NOTIFICATION message received");
 		    	   Log.i("JMG", "Characteristic: " + characteristicUUID);
 		    	   Log.i("JMG", "Value: " + notificationValue);
 		       }
		       updateNotificationText("Received Notification Event: Value: " + notificationValue +
					   " -  Characteristic UUID: " + characteristicUUID);

		       if (!notificationValue.equalsIgnoreCase("null")) {
		    	   if (characteristicUUID.equalsIgnoreCase(TumakuBLE.SENSORTAG_KEY_DATA)) {
		 		       if (Constant.DEBUG) Log.i("JMG", "NOTIFICATION of Key Service");
		 		       if (notificationValueByteArray==null) {
		 		    	  if (Constant.DEBUG) Log.i("JMG", "No notificationValueByteArray received. Discard notification");
		 		    	  return;
		 		       }
		    	   }
		       }
         	   return;
           }  
 
           if (intent.getAction().equals(TumakuBLE.WRITE_DESCRIPTOR_SUCCESS)) {
		       if (Constant.DEBUG) Log.i("JMG", "WRITE_DESCRIPTOR_SUCCESS message received");
        	   updateInfoText("Received Write Descriptor Success Event");
        	   if (mState==WSTATE_NOTIFY_KEY) {
        		   mState=WSTATE_READ_KEY;
        		   nextState();
        	   }
        	   return;
           }     
 
   
       }
       
    }

}
