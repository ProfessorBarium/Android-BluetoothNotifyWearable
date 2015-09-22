package com.ilovescience.bluetoothnotifitywearable;

import android.app.Application;
import android.content.Context;

/**
 * Created by Sam on 9/19/2015.
 */
public class App extends Application{
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }
    public static Context getContext(){
        return mContext;
    }

}
