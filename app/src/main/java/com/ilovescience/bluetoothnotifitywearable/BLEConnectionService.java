package com.ilovescience.bluetoothnotifitywearable;

import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * Created by Sam on 10/1/2015.
 */
public class BLEConnectionService extends IntentService {

    BluetoothAdapter mBluetoothAdapter;
    HM10 mHM10;
    String mMacAddress;
    Context mContext;

    NotificationRule triggeringRule;

    public BLEConnectionService() {
        super("BLEConnectionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mContext = this;
        Bundle myExtras = intent.getExtras();
       SharedPreferences sharedPreferences =  mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        triggeringRule= new Gson().fromJson(myExtras.getString(Constants.KEY_TRIGGERING_RULE), NotificationRule.class);
        //mMacAddress = myExtras.getString(Constants.KEY_BLUETOOTH_ADDRESS, "B4:99:4C:68:4A:59");
        mMacAddress = sharedPreferences.getString(Constants.KEY_BLUETOOTH_ADDRESS,"B4:99:4C:68:4A:59");
        tryConnection();


    }
    private void tryConnection()
    {
        if(mBluetoothAdapter.getScanMode()== BluetoothAdapter.SCAN_MODE_CONNECTABLE)
        {
            connectHM10();
        }
        else
        {
            //turn on Bluetooth, and wait for state to change to ON
            setBluetooth(true);
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
        }
    }

    public void connectHM10()
    {
        mHM10 = new HM10(mMacAddress,mContext);
    }

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
                        Toast.makeText(mContext, "Unregistering", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(mContext,"STATE_ON...please connect", Toast.LENGTH_SHORT).show();
                        connectHM10();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:

                        break;
                }
            }
        }
    };

    //Connect to default BLE device. To be called from any triggering event
    static void startBLEConnectionService(Context mContext,String ruleAsString)
    {
        Intent myIntent = new Intent(mContext, BLEConnectionService.class);
        myIntent.putExtra(Constants.KEY_TRIGGERING_RULE,ruleAsString);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startService(myIntent);
    }
}
