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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Reis on 28/05/2014.
 */
public class Utils {

    static final String mainFolder = "/CloudBackupFolder/";

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

}
