package course.android.letgo_307945402_204317770.Objects;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import course.android.letgo_307945402_204317770.Utills.Constants;
import course.android.letgo_307945402_204317770.Utills.Converter;

public class User {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Bitmap thumb;
    private boolean hasOnlineImage;
    private ArrayList<Item> itemOnSale;
    private ArrayList<Item> soldItems;
    private ArrayList<Item> favItems;

    public User(String username, String firstName, String lastName, String email, Bitmap thumb) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.thumb = thumb;

        itemOnSale = new ArrayList<>();
        soldItems = new ArrayList<>();
        favItems = new ArrayList<>();

    }

    public User(String username, String firstName, String lastName, String email) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public User(String username) {
        this.username = username;
    }

    public boolean sellItem(Item item){
        if(item != null){
            item.setUser(this);
            return itemOnSale.add(item);
        }
        return false;
    }

    public Bitmap getThumbnail() {
        if (thumb != null) {
            Bitmap t = ThumbnailUtils.extractThumbnail(thumb, 50, 50);
            return t;
        }
        return null;
    }

    public ArrayList<Item> getFavItems() {
        return favItems;
    }

    public void setFavItems(ArrayList<Item> favItems) {
        this.favItems = favItems;
    }

    public ArrayList<Item> getItemOnSale() {
        return itemOnSale;
    }

    public void setItemOnSale(ArrayList<Item> itemOnSale) {
        this.itemOnSale = itemOnSale;
    }

    public ArrayList<Item> getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(ArrayList<Item> soldItems) {
        this.soldItems = soldItems;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return username.equals(user.username);

    }

    public ContentValues getContent(){

        ContentValues cv = new ContentValues();

        cv.put(Constants.USERNAME, username);
        cv.put(Constants.FNAME, firstName);
        cv.put(Constants.LNAME, lastName);
        cv.put(Constants.EMAIL, email);

        if(thumb != null)
            cv.put(Constants.IMAGE, Converter.getBitmapAsByteArray(thumb));

        return cv;

    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    public static User parseJSON(JSONObject object){
        User user;
        try {
            String username = object.getString(Constants.USERNAME);
            String fname = object.getString(Constants.FNAME);
            String lname = object.getString(Constants.LNAME);
            String email =  object.getString(Constants.EMAIL);
            boolean has =  object.getBoolean(Constants.HAS_IMAGE);

            user = new User(username, fname, lname, email);
            user.setHasOnlineImage(has);
            return user;



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void setHasOnlineImage(boolean hasOnlineImage) {
        this.hasOnlineImage = hasOnlineImage;
    }

    public boolean isHasOnlineImage() {
        return hasOnlineImage;
    }
}
