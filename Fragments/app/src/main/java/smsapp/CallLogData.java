
package smsapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    public int getType() { return type; }

    public String getTypeFormatted()
    {
        if(type == 1)
            return "Incoming Call";
        else if(type == 2)
            return "Outgoing Call";
        else
            return "Missed Call";
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

    public void setDurationFormatted(String callDuration) {
        int seconds = Integer.parseInt(callDuration);

        int hours, minutes;
        hours = seconds / 3600;
        minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;

        if(hours == 0)
        {
            if(minutes == 0)
                duration = seconds+" secs";
            else
                duration = minutes+" mins "+seconds+" secs";
        }
        else
            duration = hours+" hours "+minutes+" mins "+seconds+" secs";
    }

}

