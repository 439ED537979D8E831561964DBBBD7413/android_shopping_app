package course.android.shopping_example_app.Objects;


import android.content.ContentValues;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Utills.Constants;
import course.android.shopping_example_app.Utills.Converter;
import course.android.shopping_example_app.Utills.E_Category;

public class Item implements Serializable {

    private String id;
    private String name;
    private E_Category category;
    private User user;
    private String desc;
    private long price;
    private Bitmap image;
    private boolean sold;
    private boolean hasOnlineImage;
    private String uri;

    public Item(String id) {
        this.id = id;
    }

    public Item(String id, String name, E_Category category, long price, String desc, Bitmap image, User user) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.desc = desc;
        this.image = image;
        this.user = user;
        this.price = price;
        sold = false;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Bitmap getThumbnail() {
        if (image != null) {
            Bitmap t = ThumbnailUtils.extractThumbnail(image, image.getWidth(), 160);
            return t;
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public E_Category getCategory() {
        return category;
    }

    public void setCategory(E_Category category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setHasImage(boolean hasOnlineImage) {
        this.hasOnlineImage = hasOnlineImage;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public static Item parseJSON(JSONObject object){
        Item item =  null;
        try {

            String id =  object.getString(Constants.ID);
            String name = object.getString(Constants.NAME);
            String desc = object.getString(Constants.DESCRIPTION);
            String cate = object.getString(Constants.CATEGORY);
            long price = object.getLong(Constants.PRICE);
            User user = SysData.getInstance().getUser(object.getString(Constants.USERNAME));
            boolean sold = object.getBoolean(Constants.SOLD);

            boolean hasImage = object.getBoolean(Constants.HAS_IMAGE);

            item = new Item(id, name, E_Category.valueOf(cate), price, desc, null, user);
            item.setSold(sold ? 1 :0);
            item.setHasImage(hasImage);

            return item;

        } catch (JSONException e) {
            Log.e("Exception :", e.getMessage());
            return null;
        }

    }

    public boolean isHasOnlineImage() {
        return hasOnlineImage;
    }

    public ContentValues getContent(String table) {
        ContentValues cv = new ContentValues();

        if(table.equals(Constants.FAVOURITE_ITEM)) {
            cv.put(Constants.ID, id);
            cv.put(Constants.USERNAME, user.getUsername());
        }else {
            cv.put(Constants.ID, id);
            cv.put(Constants.NAME, name);
            cv.put(Constants.DESCRIPTION, desc);
            cv.put(Constants.CATEGORY, category.getType());
            cv.put(Constants.PRICE, price);
            if (image != null)
                cv.put(Constants.IMAGE, Converter.getBitmapAsByteArray(image));
            cv.put(Constants.USERNAME, user.getUsername());
            cv.put(Constants.SOLD, sold);
            cv.put(Constants.URI, uri);
        }

        return cv;
    }

    public void setSold(int sold) {
        if (sold == 1)
            this.sold = true;

        else if (sold == 0)
            this.sold = false;
    }

    public boolean isSold() {
        return sold;
    }

    public String getUri() {
        return uri;
    }
}
