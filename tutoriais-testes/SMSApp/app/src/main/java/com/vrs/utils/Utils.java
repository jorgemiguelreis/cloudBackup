package com.vrs.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vrs.smsapp.CallLogData;
import com.vrs.smsapp.SMSData;

import org.w3c.dom.*;
import org.xmlpull.v1.XmlSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;

/**
 * Created by Reis on 28/05/2014.
 */
public class Utils {

    public static final String mainFolder = Environment.getExternalStorageDirectory()+"/CloudBackupFolder/";

    static public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public static String millisToDate(long currentTime) {
        String finalDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        Date date = calendar.getTime();
        finalDate = date.toString();
        return finalDate;
    }

    public static void writeToFile(String fileName, String body)
    {
        /*
        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + mainFolder );

            if (!dir.exists())
            {
                dir.mkdirs();
            }
            String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+mainFolder;
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path+fileName+".txt", true)));
            out.println(body);
            out.close();
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        */
        FileOutputStream fos = null;

        try {
            final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + mainFolder );

            if (!dir.exists())
            {
                dir.mkdirs();
            }

            final File myFile = new File(dir, fileName + ".txt");

            if (!myFile.exists())
            {
                myFile.createNewFile();
                fos = new FileOutputStream(myFile);
                fos.write(body.getBytes());
            }
            else
            {
                fos = new FileOutputStream(myFile);
            }



            fos = new FileOutputStream(myFile);
            fos.write(body.getBytes());
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static public void CreateBlankDocument(Context context, Context appContext){
        Toast.makeText(context,
                "Creating Blank Document",
                Toast.LENGTH_LONG).show();
        try{
            //Create instance of DocumentBuilderFactory
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            //Get the DocumentBuilder
            DocumentBuilder parser = factory.newDocumentBuilder();
            //Create blank DOM Document
            Document doc=parser.newDocument();
            //create the root element
            Element root=doc.createElement("root");
            //all it to the xml tree
            doc.appendChild(root);
            //create a comment
            Comment comment=doc.createComment("This is a comment");
            //add in the root element
            root.appendChild(comment);
            //creat child element
            Element childelement=doc.createElement("child");
            //Add the attribute to the child
            childelement.setAttribute("value", "1");
            root.appendChild(childelement);

            TransformerFactory transformerfactory=
                    TransformerFactory.newInstance();
            Transformer transformer=
                    transformerfactory.newTransformer();

            DOMSource source=new DOMSource(doc);
            FileOutputStream _stream=appContext.openFileOutput("NewDom.xml", appContext.MODE_WORLD_WRITEABLE);
            StreamResult result=new StreamResult(_stream);

            transformer.transform(source, result);
            Toast.makeText(context,
                    "Done File created",
                    Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    static public void createXml(Context context, List<SMSData> smsList) {
        //create a new file called "new.xml" in the SD card
        File newxmlfile = new File(mainFolder+"/SMSs.xml");
        try{
            newxmlfile.createNewFile();
        }catch(IOException e){
            Log.e("IOException", "exception in createNewFile() method");
        }
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
            Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        try {
            //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
            serializer.setOutput(fileos, "UTF-8");
            //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
            serializer.startDocument(null, Boolean.valueOf(true));
            //set indentation option
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            //start a tag called "root"
            serializer.startTag(null, "SMSRecord");

            for(SMSData sms : smsList) {
                if (sms.getNumber() != null && sms.getBody() != null) {
                    serializer.startTag(null, "SMS");
                    //i indent code just to have a view similar to xml-tree
                    serializer.startTag(null, "Type");
                    serializer.text(Integer.toString(sms.getType()));
                    serializer.endTag(null, "Type");

                    if (sms.getPerson() != null) {
                        serializer.startTag(null, "Person");
                        serializer.text(sms.getPerson());
                        serializer.endTag(null, "Person");
                    }

                    serializer.startTag(null, "Number");
                    serializer.text(sms.getNumber());
                    serializer.endTag(null, "Number");

                    serializer.startTag(null, "Date");
                    serializer.text(sms.getFormattedDate());
                    serializer.endTag(null, "Date");

                    serializer.startTag(null, "Body");
                    serializer.text(sms.getBody());
                    serializer.endTag(null, "Body");

                    serializer.endTag(null, "SMS");
                }
            }
            serializer.endTag(null, "SMSRecord");

            serializer.endDocument();
            //write xml data into the FileOutputStream
            serializer.flush();
            //finally we close the file stream
            fileos.close();

            Toast.makeText(context, "XML file created", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Exception","error occurred while creating xml file");
        }
    }


    static public void createCallLogXML(Context context, List<CallLogData> callLogList) {
        //create a new file called "new.xml" in the SD card
        File newxmlfile = new File(mainFolder+"/CallLogs.xml");
        try{
            newxmlfile.createNewFile();
        }catch(IOException e){
            Log.e("IOException", "exception in createNewFile() method");
        }
        //we have to bind the new file with a FileOutputStream
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(newxmlfile);
        }catch(FileNotFoundException e){
            Log.e("FileNotFoundException", "can't create FileOutputStream");
        }
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        try {
            //we set the FileOutputStream as output for the serializer, using UTF-8 encoding
            serializer.setOutput(fileos, "UTF-8");
            //Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null)
            serializer.startDocument(null, Boolean.valueOf(true));
            //set indentation option
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            //start a tag called "root"
            serializer.startTag(null, "CallLogRecord");

            for(CallLogData callLog : callLogList) {
                if (callLog.getNumber() != null && callLog.getDuration() != null) {
                    serializer.startTag(null, "CallLog");
                    //i indent code just to have a view similar to xml-tree
                    serializer.startTag(null, "Type");
                    serializer.text(Integer.toString(callLog.getType()));
                    serializer.endTag(null, "Type");

                    if (callLog.getPerson() != null) {
                        serializer.startTag(null, "Person");
                        serializer.text(callLog.getPerson());
                        serializer.endTag(null, "Person");
                    }

                    serializer.startTag(null, "Number");
                    serializer.text(callLog.getNumber());
                    serializer.endTag(null, "Number");

                    serializer.startTag(null, "Date");
                    serializer.text(callLog.getFormattedDate());
                    serializer.endTag(null, "Date");

                    serializer.startTag(null, "Duration");
                    serializer.text(callLog.getDuration());
                    serializer.endTag(null, "Duration");

                    serializer.endTag(null, "CallLog");
                }
            }
            serializer.endTag(null, "CallLogRecord");

            serializer.endDocument();
            //write xml data into the FileOutputStream
            serializer.flush();
            //finally we close the file stream
            fileos.close();

            Toast.makeText(context, "Call Log XML file created", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("Exception","error occurred while creating xml file");
        }
    }

}
