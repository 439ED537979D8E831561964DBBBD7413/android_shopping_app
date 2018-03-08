package com.letgo.objects;


import com.letgo.utils.Constants;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;

public class User {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private byte[] image = null;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String firstName, String lastName, String email, byte[] image) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.image = image;

    }

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String username, String firstName, String lastName, String email, String password, byte[] image) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return username.equals(user.username);

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    private boolean isImageExists() {
        if (image == null || image.length == 0) {
            return false;
        }
        return true;
    }

    public JSONObject toObjJson() throws  JSONException{
        JSONObject object = null;

        object = new JSONObject();
        object.put(Constants.USERNAME,  username);
        object.put(Constants.FNAME, firstName);
        object.put(Constants.LNAME, lastName);
        object.put(Constants.EMAIL, email);
        object.put(Constants.HAS_IMAGE, isImageExists());

        return object;
    }

    public String toJson(){
        try {
            return toObjJson().toString();

        }catch (JSONException e){
            return null;
        }
    }

    public byte[] getImage() {
        return image;
    }

    public static String toListJson(List<User> users){
        JSONObject object;
        JSONArray arr;

        try {
            if (users == null) {
                return null;
            }

            if (users.size() == 0) {
                return null;
            }
            object = new JSONObject();
            arr = new JSONArray();
            for(User u : users){
                arr.add(u.toObjJson());
            }
            object.put(Constants.USERS, arr);
            return object.toString();

        } catch (JSONException e) {
            return "";
        }


    }

}
