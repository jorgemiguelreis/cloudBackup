package com.vrs.smsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vrs.utils.Utils;
import com.vrs.utils.XMLParser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessageBox extends Activity implements OnClickListener {

    // GUI Widget
    Button btnSent, btnInbox, btnDraft, btnCreate, btnLoad, btnContacts, btnCallLogs, btnCallLogsXML, btnLoadCallLog;
    TextView lblMsg, lblNo;
    ListView lvMsg;

    // Cursor Adapter
    SimpleCursorAdapter adapter;

    List<SMSData> smsList = new ArrayList<SMSData>();
    List<CallLogData> callLogList = new ArrayList<CallLogData>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagebox);

        // Init GUI Widget
        btnInbox = (Button) findViewById(R.id.btnInbox);
        btnInbox.setOnClickListener(this);

        btnSent = (Button) findViewById(R.id.btnSentBox);
        btnSent.setOnClickListener(this);

        btnDraft = (Button) findViewById(R.id.btnDraft);
        btnDraft.setOnClickListener(this);

        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(this);

        btnContacts = (Button) findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(this);

        btnCallLogs = (Button) findViewById(R.id.btnCallLogs);
        btnCallLogs.setOnClickListener(this);

        btnCallLogsXML = (Button) findViewById(R.id.btnCallLogsXML);
        btnCallLogsXML.setOnClickListener(this);

        btnLoadCallLog = (Button) findViewById(R.id.btnLoadCallLog);
        btnLoadCallLog.setOnClickListener(this);

        lvMsg = (ListView) findViewById(R.id.lvMsg);
    }

    @Override
    public void onClick(View v) {

        if (v == btnInbox) {

            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

            // Get Content Resolver object, which will deal with Content
            // Provider

            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "date", "type"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lbldate, R.id.lbltype}
            );
            lvMsg.setAdapter(adapter);

            //Utils.CreateBlankDocument(getBaseContext(), getApplicationContext());

            //c.close();

            /*
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list, cursor,
            new String[] { Definition.Item.TITLE, Definition.Item.CREATE_DATE }, new int[] { R.id.title, R.id.createDate});

adapter.setViewBinder(new ViewBinder() {

    public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {

        if (aColumnIndex == 2) {
                String createDate = aCursor.getString(aColumnIndex);
                TextView textView = (TextView) aView;
                textView.setText("Create date: " + MyFormatterHelper.formatDate(getApplicationContext(), createDate));
                return true;
         }

         return false;
    }
});
             */
        }

        if (v == btnSent) {

            // Create Sent box URI
            Uri sentURI = Uri.parse("content://sms/sent");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(sentURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "date", "type"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lbldate, R.id.lbltype}
            );
            lvMsg.setAdapter(adapter);

        }

        if (v == btnDraft) {
            // Create Draft box URI
            Uri draftURI = Uri.parse("content://sms/draft");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(draftURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "date", "type"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lbldate, R.id.lbltype}
            );
            lvMsg.setAdapter(adapter);

        }

        if (v == btnCreate) {

            String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

            Uri inboxURI = Uri.parse("content://sms");

            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);

            if(c.moveToFirst()) {
                do
                {
                    int type = c.getInt(c.getColumnIndex("type"));
                    String number = c.getString(c.getColumnIndex("address"));
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
                    String body = c.getString(c.getColumnIndex("body"));

                    SMSData sms = new SMSData(number, body, date, type);
                    sms.setPerson(Utils.getContactName(this, number));

                    smsList.add(sms);
                }while (c.moveToNext());
            }
            Utils.createXml(getBaseContext(), smsList);
        }

        if (v == btnLoad) {
            XMLParser parser = new XMLParser(Utils.mainFolder+"SMSs.xml");
            // Construct the data source
            ArrayList<SMSData> smsList = parser.parseXMLfile();

            Toast.makeText(getBaseContext(), "File Loaded", Toast.LENGTH_SHORT).show();

            //Display SMS's

            // Create the adapter to convert the array to views
            SMSAdapter adapter1 = new SMSAdapter(this, smsList);

            // Attach the adapter to a ListView
            ListView lv = (ListView) findViewById(R.id.lvMsg);
            lv.setAdapter(adapter1);
        }

        if (v == btnCallLogs) {
            // Create Draft box URI
            Uri callsURI = CallLog.Calls.CONTENT_URI;

            // List required columns
            String[] reqCols = new String[]{"_id", CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(callsURI, reqCols, null, null, null);


            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lbldate, R.id.lbltype}
            );

            Log.i("main", "chegou aqui?");

            lvMsg.setAdapter(adapter);

            Toast.makeText(this, "Calls Listed", Toast.LENGTH_SHORT).show();

        }

        if (v == btnCallLogsXML) {

            String[] reqCols = new String[]{"_id", CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};

            Uri callsURI = CallLog.Calls.CONTENT_URI;

            ContentResolver cr = getContentResolver();

            Cursor c = cr.query(callsURI, reqCols, null, null, null);

            if(c.moveToFirst()) {
                do
                {
                    int type = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
                    String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
                    String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));

                    CallLogData callLog = new CallLogData(number, duration, date, type);
                   // String p = Utils.getContactName(this, number);
                    //if(p != null)
                      //  callLog.setPerson(Utils.getContactName(this, number));

                    callLogList.add(callLog);
                }while (c.moveToNext());
            }
            Utils.createCallLogXML(getBaseContext(), callLogList);
        }


        if (v == btnLoadCallLog) {
            XMLParser parser = new XMLParser(Utils.mainFolder+"CallLogs.xml");
            // Construct the data source
            ArrayList<CallLogData> callLogList = parser.parseCallLogXML();

            Toast.makeText(getBaseContext(), "File Loaded", Toast.LENGTH_SHORT).show();

            // Create the adapter to convert the array to views
            CallLogAdapter adapter1 = new CallLogAdapter(this, callLogList);

            // Attach the adapter to a ListView
            ListView lv = (ListView) findViewById(R.id.lvMsg);
            lv.setAdapter(adapter1);
        }
    }
}
