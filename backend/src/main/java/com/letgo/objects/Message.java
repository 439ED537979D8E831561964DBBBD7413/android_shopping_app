package com.letgo.objects;

import com.letgo.utils.Constants;
import com.letgo.utils.DateUtil;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.Date;


public class Message {

    private User sender;
    private String message;
    private long dateFormat;

    public Message(User sender, String message) {
        this.sender = sender;
        this.message = message;
        dateFormat = System.currentTimeMillis();
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDateFormat() {
        return dateFormat;
    }

    public void setDate(String date) {
        dateFormat = DateUtil.getCurrentDate(date);
    }

    public void setDate(Date date) {
        dateFormat = DateUtil.getCurrentDate(date);
    }

    public void setDate(long date) {
        dateFormat = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (dateFormat != message1.dateFormat) return false;
        return message.equals(message1.message);

    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (int) (dateFormat ^ (dateFormat >>> 32));
        return result;
    }

    public JSONObject toJson(){
        JSONObject object;
        try {
            object = new JSONObject();
            object.put(Constants.SENDER, sender.getUsername());
            object.put(Constants.DESCRIPTION, message);
            object.put(Constants.DATE, dateFormat);

        } catch (JSONException e) {
            return null;
        }
        return object;
    }

}
