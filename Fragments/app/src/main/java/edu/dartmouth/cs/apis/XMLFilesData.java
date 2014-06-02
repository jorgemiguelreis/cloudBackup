package edu.dartmouth.cs.apis;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import smsapp.CallLogData;
import smsapp.SMSData;
import utils.Utils;
import utils.XMLParser;

public final class XMLFilesData {

    public String[] SMS_TITLES;
    public String[] SMS_DIALOGUE;

    public String[] CallLog_TITLES;
    public String[] CallLog_DIALOGUE;

    public XMLFilesData() {

        /**************************************************
         *          Load Data From SMS XML files          *
         *************************************************/
        File SMSFolder = new File(Utils.SMSFolder);
        File[] listOfSMSFiles = SMSFolder.listFiles();
        String[] TITLES = new String[listOfSMSFiles.length];
        String[] DIALOGUE = new String[listOfSMSFiles.length];

        for (int i = 0; i < listOfSMSFiles.length; i++) {
            if (listOfSMSFiles[i].isFile()) {
                Log.i("File ", listOfSMSFiles[i].getName());
                TITLES[i] = listOfSMSFiles[i].getName();

                XMLParser parser = new XMLParser(Utils.SMSFolder, listOfSMSFiles[i].getName());
                ArrayList<SMSData> smsList = parser.parseXMLfile();
                StringBuffer sb = new StringBuffer();
                for(int j=0; j<smsList.size(); j++)
                {
                    sb.append("\nPhone Number:--- " + smsList.get(j).getNumber() +
                            " \nType:--- " +  smsList.get(j).getTypeFormatted() +
                            " \nDate:--- " +  smsList.get(j).getDateString() +
                            " \nBody :--- " +  smsList.get(j).getBody());
                    sb.append("\n----------------------------------");

                    DIALOGUE[i] = sb.toString();
                }
                //Toast.makeText(getBaseContext(), "File Loaded", Toast.LENGTH_SHORT).show();
            }
        }
        this.SMS_TITLES = TITLES;
        this.SMS_DIALOGUE = DIALOGUE;


        /**************************************************
         *        Load Data From Call Log XML files       *
         *************************************************/
        File CallLogFolder = new File(Utils.CallLogFolder);
        File[] listOfCallLogFiles = CallLogFolder.listFiles();
        String[] CallLog_TITLES = new String[listOfCallLogFiles.length];
        String[] CallLog_DIALOGUES = new String[listOfCallLogFiles.length];

        for (int i = 0; i < listOfCallLogFiles.length; i++) {
            if (listOfCallLogFiles[i].isFile()) {
                Log.i("File ", listOfCallLogFiles[i].getName());
                CallLog_TITLES[i] = listOfCallLogFiles[i].getName();

                XMLParser parser = new XMLParser(Utils.CallLogFolder, listOfCallLogFiles[i].getName());
                ArrayList<CallLogData> callLogList = parser.parseCallLogXML();
                StringBuffer sb = new StringBuffer();
                for(int j=0; j<callLogList.size(); j++)
                {
                    sb.append("\nPhone Number:--- " + callLogList.get(j).getNumber() +
                            " \nType:--- " +  callLogList.get(j).getTypeFormatted() +
                            " \nDate:--- " +  callLogList.get(j).getDateString() +
                            " \nDuration :--- " +  callLogList.get(j).getDuration());
                    sb.append("\n----------------------------------");

                    CallLog_DIALOGUES[i] = sb.toString();
                }
                //Toast.makeText(getBaseContext(), "File Loaded", Toast.LENGTH_SHORT).show();
            }
        }
        this.CallLog_TITLES = CallLog_TITLES;
        this.CallLog_DIALOGUE = CallLog_DIALOGUES;
    }
}
