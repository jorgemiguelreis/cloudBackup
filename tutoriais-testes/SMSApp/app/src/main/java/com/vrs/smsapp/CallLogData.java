
package com.vrs.smsapp;

import com.vrs.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallLogData {

    //public enum smsType {RECEBIDA, ENVIADA};

    // Number from witch the sms was send
    private String number;
    // SMS text body
    private String duration;

    static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

    private Calendar date;
    private String dateString;

    private int type;

    private String person;

    public CallLogData() {}
    public CallLogData(String number, String duration, Calendar date, int type)
    {
        this.number = number;
        this.duration = duration;
        this.date = date;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getPerson() {
        return person;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDateString() {
        return dateString;
    }

    public String getFormattedDate() {
        return FORMATTER.format(date.getTime());
    }

    public String getDuration() {
        return duration;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setDateString(String date) {
        this.dateString = date;
    }

    public void setDuration(String body) {
        this.duration = body;
    }

    /*
    public String getDate() {
        return FORMATTER.format(this.date);
    }

    public void setDate(String date) {
        // pad the date if necessary
        while (!date.endsWith("00")){
            date += "0";
        }
        try {
            this.date = FORMATTER.parse(date.trim());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
*/


/*
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number: ");
        sb.append(number);
        sb.append('\n');
        sb.append("Date: ");
        sb.append(this.getDate());
        sb.append("Body: ");
        sb.append(body);
        return sb.toString();
    }
*/
}

