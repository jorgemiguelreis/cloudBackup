package com.vrs.utils;

import android.os.Message;
import android.util.Xml;

import com.vrs.smsapp.CallLogData;
import com.vrs.smsapp.SMSData;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reis on 29/05/2014.
 */
public class XMLParser {

    //URL fileURL;
    String path;

    public XMLParser(String url) {
        /*try {
            this.fileURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }*/
        path=url;
    }
/*
    protected InputStream getInputStream() {
        try {
            return fileURL.openConnection().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
*/
    public ArrayList<SMSData> parseXMLfile() {
        ArrayList<SMSData> smsList = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            // auto-detect the encoding from the stream
            parser.setInput(fis, null);
            int eventType = parser.getEventType();
            SMSData currentSms = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        smsList = new ArrayList<SMSData>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("SMS")){
                            currentSms = new SMSData();
                        } else if (currentSms != null) {
                            if (name.equalsIgnoreCase("Type")){
                                currentSms.setType(Integer.parseInt(parser.nextText()));
                            } else if (name.equalsIgnoreCase("Person")){
                                currentSms.setPerson(parser.nextText());
                            } else if (name.equalsIgnoreCase("Number")){
                                currentSms.setNumber(parser.nextText());
                            } else if (name.equalsIgnoreCase("Date")){
                                currentSms.setDateString(parser.nextText());
                            } else if(name.equalsIgnoreCase("body")) {
                                currentSms.setBody(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("SMS") &&
                                currentSms != null){
                            smsList.add(currentSms);
                        } else if (name.equalsIgnoreCase("SMSRecord")){
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return smsList;
    }



    public ArrayList<CallLogData> parseCallLogXML() {
        ArrayList<CallLogData> callLogList = null;
        XmlPullParser parser = Xml.newPullParser();
        try {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            // auto-detect the encoding from the stream
            parser.setInput(fis, null);
            int eventType = parser.getEventType();
            CallLogData currentCallLog = null;
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done){
                String name = null;
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        callLogList = new ArrayList<CallLogData>();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("CallLog")){
                            currentCallLog = new CallLogData();
                        } else if (currentCallLog != null) {
                            if (name.equalsIgnoreCase("Type")){
                                currentCallLog.setType(Integer.parseInt(parser.nextText()));
                            } else if (name.equalsIgnoreCase("Person")){
                                currentCallLog.setPerson(parser.nextText());
                            } else if (name.equalsIgnoreCase("Number")){
                                currentCallLog.setNumber(parser.nextText());
                            } else if (name.equalsIgnoreCase("Date")){
                                currentCallLog.setDateString(parser.nextText());
                            } else if(name.equalsIgnoreCase("Duration")) {
                                currentCallLog.setDuration(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("CallLog") &&
                                currentCallLog != null){
                            callLogList.add(currentCallLog);
                        } else if (name.equalsIgnoreCase("CallLogRecord")){
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return callLogList;
    }

}