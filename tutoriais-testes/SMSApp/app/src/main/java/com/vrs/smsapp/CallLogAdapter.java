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
public class CallLogAdapter extends ArrayAdapter<CallLogData> {

    public CallLogAdapter(Context context, ArrayList<CallLogData> callLogList) {
        super(context, R.layout.row, callLogList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CallLogData callLog = getItem(position);

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
       /* tvType.setText(Integer.toString(callLog.getType()));
        tvNumber.setText(callLog.getNumber());
        tvDate.setText(callLog.getDateString());
        tvBody.setText(callLog.getDuration());
*/
        tvNumber.setText(callLog.getTypeFormatted());
        tvBody.setText(callLog.getNumber());
        tvDate.setText(callLog.getDateString());
        tvType.setText(callLog.getDuration());

        // Return the completed view to render on screen
        return convertView;
    }

}