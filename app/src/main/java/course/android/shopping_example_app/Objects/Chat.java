package course.android.shopping_example_app.Objects;


import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Utills.Constants;

public class Chat {

    private User sender, guest;
    private ArrayList<Message> messages;

    public Chat(User sender, User guest) {
        this.sender = sender;
        this.guest = guest;

        messages = new ArrayList<>();
    }


    public User getGuest() {
        return guest;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessege(Message msg){
        messages.add(msg);
    }

    public Message getLastMessage() {
        if(!messages.isEmpty())
            return messages.get(messages.size()-1);
        return new Message(sender,"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

        if(sender.equals(chat.getSender()) && guest.equals(chat.getGuest()))
            return true;
        else if(guest.equals(chat.getSender()) && sender.equals(chat.getGuest()))
            return true;

        return false;

    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + guest.hashCode();
        return result;
    }

    public User getSender() {
        return sender;
    }

    public static Chat parseJSON(JSONObject jsonObject) {
        try{
            Chat chat;
            JSONArray arr = jsonObject.getJSONArray(Constants.MESSAGES);
            User sender  = SysData.getInstance().getUser(jsonObject.getString(Constants.SENDER));
            User rec = SysData.getInstance().getUser(jsonObject.getString(Constants.RECEIVER));

            Message m;
            chat = new Chat(sender, rec);

            for(int i = 0; i < arr.length(); i++){
                m = Message.parseJSON(chat, arr.getJSONObject(i));
                chat.addMessege(m);

            }

            return chat;

        }catch (Exception e){
            return null;
        }

    }

    public ContentValues getContent() {
        ContentValues cv = new ContentValues();
        cv.put(Constants.USERNAME, sender.getUsername());
        cv.put(Constants.SECONDUSERNAME, guest.getUsername());
        return cv;
    }
}
