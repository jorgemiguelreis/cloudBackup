package com.vrs.smsapp;

/***
 *    Application Name : MessageBox 
 *    Author : Vimal Rughani
 *    Website : http://pulse7.net
 *    For more details visit http://pulse7.net/android/read-sms-message-inbox-sent-draft-android/
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vrs.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MessageBox extends Activity implements OnClickListener {

    // GUI Widget
    Button btnSent, btnInbox, btnDraft, btnFile;
    TextView lblMsg, lblNo;
    ListView lvMsg;

    // Cursor Adapter
    SimpleCursorAdapter adapter;

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

        btnFile = (Button) findViewById(R.id.btnFile);
        btnFile.setOnClickListener(this);

        lvMsg = (ListView) findViewById(R.id.lvMsg);

    }

    @Override
    public void onClick(View v) {

        String[] reqCols = new String[]{"_id", "address", "body", "person", "date", "type", "read", "status"};

        if (v == btnInbox) {

            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            //String[] reqCols = new String[]{"_id", "address", "body", "person", "date", "type", "read", "status"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "person", "date", "type", "read", "status"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lblperson, R.id.lbldate, R.id.lbltype, R.id.lblread, R.id.lblstatus}
            );
            lvMsg.setAdapter(adapter);

            c.moveToFirst();
            String body = c.getString(c.getColumnIndex("body"));
            String date = c.getString(c.getColumnIndex("date"));
            String address = c.getString(c.getColumnIndex("address"));
            Utils.writeToFile("mensagens",Utils.getContactName(this,address)+", "+body+", "+Utils.millisToDate(Long.parseLong(date))+"\n");
          /*  while(c.moveToNext()) {
                String body = c.getString(c.getColumnIndex("body"));
                String date = c.getString(c.getColumnIndex("date"));
                String address = c.getString(c.getColumnIndex("address"));

                Utils.writeToFile("mensagens",address+", "+body+", "+Utils.millisToDate(Long.parseLong(date))+"\n");
            }*/

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
            //String[] reqCols = new String[]{"_id", "address", "body"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(sentURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "person", "date", "type", "read", "status"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lblperson, R.id.lbldate, R.id.lbltype, R.id.lblread, R.id.lblstatus}
            );
            lvMsg.setAdapter(adapter);

        }

        if (v == btnDraft) {
            // Create Draft box URI
            Uri draftURI = Uri.parse("content://sms/draft");

            // List required columns
            //String[] reqCols = new String[]{"_id", "address", "body"};

            // Get Content Resolver object, which will deal with Content
            // Provider
            ContentResolver cr = getContentResolver();

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = cr.query(draftURI, reqCols, null, null, null);

            // Attached Cursor with adapter and display in listview
            adapter = new SimpleCursorAdapter(this, R.layout.row, c,
                    new String[]{"body", "address", "person", "date", "type", "read", "status"}, new int[]{
                    R.id.lblMsg, R.id.lblNumber, R.id.lblperson, R.id.lbldate, R.id.lbltype, R.id.lblread, R.id.lblstatus}
            );
            lvMsg.setAdapter(adapter);

        }

        if (v == btnFile) {

        /*    try {
                String s1 = "string teste";
                File myFile = new File("/sdcard/CloudBackupFolder/teste.txt");
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(s1);
                myOutWriter.close();
                fOut.close();
                Toast.makeText(getBaseContext(), s1, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            */
            Utils.writeToFile("testFile", "test String");

        }

    }
}