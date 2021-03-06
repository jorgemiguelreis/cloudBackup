
package data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SMSData {

    //public enum smsType {RECEBIDA, ENVIADA};

    // Number from witch the sms was send
    private String number;
    // SMS text body
    private String body;

    static SimpleDateFormat FORMATTER = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

    private Calendar date;
    private String dateString;

    private int type;

    private String person;

    public SMSData() {}
    public SMSData(String number, String body, Calendar date, int type)
    {
        this.number = number;
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getTypeFormatted()
    {
        if(type == 1)
            return "Inbox";
        else if(type == 2)
            return "Sent";
        else
            return "Draft";
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

    public String getBody() {
        return body;
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

    public void setBody(String body) {
        this.body = body;
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

