package course.android.letgo_307945402_204317770.Objects;


import org.json.JSONException;
import org.json.JSONObject;


import course.android.letgo_307945402_204317770.Utills.Constants;
import course.android.letgo_307945402_204317770.Utills.DateUtil;

public class Message {

    private User sender;
    private String message;
    private long dateFormat;

    public Message(User sender, String message) {
        this.sender =sender;
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

    public void setDate(long date){
        dateFormat = date;
    }

    public void setDate(String date) {
        dateFormat = DateUtil.getCurrentDate(date);
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public static Message parseJSON(Chat c, JSONObject jsonObject) {

        try {
            User sender;
            String username = c.getSender().getUsername();
            String str = jsonObject.getString(Constants.SENDER);

            if(str.equals(username))
                sender = c.getSender();
            else
                sender = c.getGuest();
            String desc = jsonObject.getString(Constants.DESCRIPTION);
            long df = jsonObject.getLong(Constants.DATE);

            Message m = new Message(sender, desc);
            m.setDate(df);

            return m;

        } catch (JSONException e) {
            return null;
        }

    }
}
