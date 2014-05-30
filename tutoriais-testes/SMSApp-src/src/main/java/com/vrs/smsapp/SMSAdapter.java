package com.vrs.smsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Reis on 30/05/2014.
 */
public class SMSAdapter extends ArrayAdapter<SMSData> {

    public SMSAdapter(Context context, ArrayList<SMSData> smslist) {
        super(context, R.layout.row, smslist);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SMSData sms = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
        }

        // Lookup view for data population
        TextView tvType = (TextView) convertView.findViewById(R.id.lbltype);
        TextView tvNumber = (TextView) convertView.findViewById(R.id.lblNumber);
        TextView tvDate = (TextView) convertView.findViewById(R.id.lbldate);
        TextView tvBody = (TextView) convertView.findViewById(R.id.lblMsg);

        // Populate the data into the template view using the data object
        tvType.setText(Integer.toString(sms.getType()));
        tvNumber.setText(sms.getNumber());
        tvDate.setText(sms.getDateString());
        tvBody.setText(sms.getBody());

        // Return the completed view to render on screen
        return convertView;
    }

}