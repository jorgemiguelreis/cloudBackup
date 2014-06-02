package com.google.android.gms.drive.sample.quickstart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import utils.Utils;
import data.CallLogData;
import data.SMSData;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;

public class CreateXMLService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * OnStartCommand is the most important method because it is called when we required to start the Service. 
	 * In this method we have the Intent passed at time we run the Service, in this way we can exchange some information with the Service. 
	 * In this method, we implement our logic that can be execute directly inside this method if it isn’t time expensive otherwise we can create a thread
	 * if the integer returned is START_STICKY : Using this return value, if the OS kills our Service it will recreate it but the Intent that was sent to the Service isn’t redelivered. In this way the Service is always running
	 * if the integer returned is START_REDELIVER_INTENT: It is similar to the START_STICKY and in this case the Intent will be redelivered to the service.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		createSMSXML();
		createCallLogXML();

		return START_REDELIVER_INTENT; 

	}
	
	
	public void createSMSXML ()
    {
    	List<SMSData> smsList = new ArrayList<SMSData>();
		 
    	String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

        Uri inboxURI = Uri.parse("content://sms");

        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        if(c.moveToFirst()) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
            String dayOfYear1 = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
            do
            {
                int type = c.getInt(c.getColumnIndex("type"));
                String number = c.getString(c.getColumnIndex("address"));
                date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
                String dayOfYear = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
                String body = c.getString(c.getColumnIndex("body"));
                SMSData sms = new SMSData(number, body, date, type);
                sms.setPerson(Utils.getContactName(this, number));

                if(dayOfYear.equals(dayOfYear1))
                    smsList.add(sms);
                else
                {
                    Utils.createXml(getBaseContext(), smsList, dayOfYear1);
                    smsList.clear();
                    dayOfYear1 = dayOfYear;
                    smsList.add(sms);
                }
            }while (c.moveToNext());
            Utils.createXml(getBaseContext(), smsList, dayOfYear1);
        }
    }
    
    public void createCallLogXML ()
    {
		 List<CallLogData> callLogList = new ArrayList<CallLogData>();
		 String[] reqCols = new String[]{"_id", CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};

         Uri callsURI = CallLog.Calls.CONTENT_URI;

         ContentResolver cr = getContentResolver();

         Cursor c = cr.query(callsURI, reqCols, null, null, null);

         if(c.moveToFirst()) {
             Calendar date = Calendar.getInstance();
             date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
             String dayOfYear1 = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
             do
             {
                 int type = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
                 String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                 date = Calendar.getInstance();
                 date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
                 String dayOfYear = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
                 String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));

                 CallLogData callLog = new CallLogData(number, duration, date, type);
                // String p = Utils.getContactName(this, number);
                 //if(p != null)
                   //  callLog.setPerson(Utils.getContactName(this, number));
                 if(dayOfYear.equals(dayOfYear1))
                     callLogList.add(callLog);
                 else
                 {
                     Utils.createCallLogXML(getBaseContext(), callLogList, dayOfYear1);
                     callLogList.clear();
                     dayOfYear1 = dayOfYear;
                     callLogList.add(callLog);
                 }
             }while (c.moveToNext());
             Utils.createCallLogXML(getBaseContext(), callLogList, dayOfYear1);
         }
    }
	
	

}
