package smsapp;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import utils.Utils;

/**
 * Created by Miao on 01/06/2014.
 */
public class XMLCreateService extends Service {

    List<SMSData> smsList = new ArrayList<SMSData>();
    List<CallLogData> callLogList = new ArrayList<CallLogData>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.showToast(this, "Creating XML files");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.showToast(this, "Creating XML files Service started");

        createSMSXML();
        createCallLogXML();

        stopSelf();
        return START_STICKY;
    }

    public void createSMSXML() {
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

    public void createCallLogXML() {
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
