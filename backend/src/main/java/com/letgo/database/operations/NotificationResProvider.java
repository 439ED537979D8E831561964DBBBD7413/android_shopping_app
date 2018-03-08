package com.letgo.database.operations;


import com.letgo.objects.MyNotification;
import com.letgo.utils.Constants;

import java.util.List;

public class NotificationResProvider {

    private static final String INSERT_NOTIF = "INSERT INTO " + Constants.NOTIFICATIONS + "(" +
            Constants.ID + ", " +
            Constants.USERNAME + ", " +
            Constants.DESCRIPTION + ", " +
            Constants.DATE + ", " +
            ") VALUES (NULL, ?, ?, ?); ";

    private static final String DELETE_NOTIF = "DELETE FROM TABLE " + Constants.NOTIFICATIONS + " WHERE " + Constants.ID + " =?;";
    private static final String GET_NOTIFIC_FOR_USER = "SELECT * FROM " + Constants.NOTIFICATIONS + " WHERE " + Constants.USERNAME + " =?;";

    public boolean insetNotific(){
        return false;
    }

    public List<MyNotification> getNotificationsForUser(String username){
        return null;
    }

    public boolean deleteNotific(){
        return false;
    }
}
