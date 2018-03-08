package com.letgo.objects;


import com.letgo.utils.Constants;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private User sender, guest;
    private ArrayList<Message> messages;
    
    public Chat() {
        messages = new ArrayList<>();
    }
    
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
        return messages.get(messages.size() - 1);
    }

    public JSONObject toJson() throws JSONException{
        JSONObject object = null;
        JSONArray msgArr = null;

        object = new JSONObject();
        msgArr = new JSONArray();

        object.put(Constants.SENDER, sender.getUsername());
        object.put(Constants.RECEIVER, guest.getUsername());

        for (Message msg : messages){
            msgArr.add(msg.toJson());
        }

        object.put(Constants.MESSAGES, msgArr);

        return object;
    }

    public static String toJson(List<Chat>  chats){
        JSONObject object;
        try {
            object = new JSONObject();
            JSONArray arr = new JSONArray();

            if (chats == null) {
                return null;
            }

            if (chats.size() == 0) {
                return null;
            }

            for (Chat ch : chats) {
                if (ch != null) {
                    arr.add(ch.toJson());
                }
            }
            object.put(Constants.CHATS, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }

    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setGuest(User guest) {
        this.guest = guest;
    }
}
