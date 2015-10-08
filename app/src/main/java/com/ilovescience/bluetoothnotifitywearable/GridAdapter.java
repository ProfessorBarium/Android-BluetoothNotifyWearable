package com.ilovescience.bluetoothnotifitywearable;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Sam on 10/4/2015.
 */
public class GridAdapter extends BaseAdapter{
    private Context mContext;

    public  GridAdapter(Context context)
    {
        mContext = context;
        Toast.makeText(mContext, Integer.toString(getCount()), Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getCount() {
        return NotificationRule.reconstructRules(mContext).length;

    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if(convertView ==null)
        {
            button = new Button(mContext);
            button.setLayoutParams(new GridView.LayoutParams(200,55));
            button.setPadding(8,8,8,8);
        }
        else{
            button = (Button)convertView;
        }
        //display the contact name of each button
        button.setText(NotificationRule.reconstructRules(mContext)[position].getmContactName());
        button.setTextColor(Color.WHITE);
        //button.setBackgroundResource(R.drawable.button);


        return button;
    }
}
