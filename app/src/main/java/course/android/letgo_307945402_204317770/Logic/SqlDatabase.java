package course.android.letgo_307945402_204317770.Logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import course.android.letgo_307945402_204317770.Objects.Chat;
import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.Objects.Message;
import course.android.letgo_307945402_204317770.Objects.User;
import course.android.letgo_307945402_204317770.R;
import course.android.letgo_307945402_204317770.Utills.Constants;
import course.android.letgo_307945402_204317770.Utills.Converter;
import course.android.letgo_307945402_204317770.Utills.E_Category;

public class SqlDatabase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = R.string.app_name+".db";

    /** table entries **/
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + Constants.USER + " (" +
                    Constants.USERNAME + " VARCHAR(12) PRIMARY KEY," +
                    Constants.FNAME + " VARCHAR(12)," +
                    Constants.LNAME + " VARCHAR(12), " +
                    Constants.EMAIL +" VARCHAR(128), " +
                    Constants.PASSWORD +" VARCHAR(25)," +
                    Constants.IMAGE +" BLOB " +
                    ")";

    private static final String SQL_CREATE_ITEM_TABLE =
            "CREATE TABLE " + Constants.ITEM + " (" +
                    Constants.ID + " VARCHAR(12) PRIMARY KEY," +
                    Constants.NAME + " VARCHAR(12)," +
                    Constants.DESCRIPTION + " TEXT, " +
                    Constants.CATEGORY + " VARCHAR(12), " +
                    Constants.PRICE + " INTEGER, " +
                    Constants.IMAGE + " BLOB, " +
                    Constants.USERNAME + " VARCHAR(12), " +
                    Constants.SOLD + " BOOLEAN, " +
                    Constants.URI + " TEXT, " +
                    "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER +" (" + Constants.USERNAME + ") " +
                    ")";

    private static final String SQL_CREATE_FAROUTIE_ITEM_TABLE =
            "CREATE TABLE " + Constants.FAVOURITE_ITEM + " (" +
                    Constants.ID + " VARCHAR(12), " +
                    Constants.USERNAME + " VARCHAR(12), " +
                    "FOREIGN KEY (" + Constants.ID + ") REFERENCES " + Constants.ITEM + " (" +Constants.ID + "), "+
                    "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER +" (" + Constants.USERNAME + "), " +
                    "CONSTRAINT PK1 PRIMARY KEY (" + Constants.ID + "," + Constants.USERNAME + ")" +
                    " )";

    private static final String SQL_CREATE_CHAT_TABLE =
            "CREATE TABLE " + Constants.CHAT + " (" +
                    Constants.USERNAME + " VARCHAR(12), " +
                    Constants.SECONDUSERNAME + " VARCHAR(12), " +
                    "CONSTRAINT PK2 PRIMARY KEY (" + Constants.USERNAME + "," + Constants.SECONDUSERNAME +")" +
                    " )";

    private static final String SQL_CREATE_MESSAGE_TABLE =
            "CREATE TABLE " + Constants.MESSAGE + " (" +
                    Constants.USERNAME + " VARCHAR(12), " +
                    Constants.SECONDUSERNAME + " VARCHAR(12), " +
                    Constants.DESCRIPTION + " TEXT, " +
                    Constants.DATE + " DATE, " +
                    "FOREIGN KEY (" + Constants.USERNAME + ") REFERENCES " + Constants.USER +" (" + Constants.USERNAME + "), " +
                    "FOREIGN KEY (" + Constants.SECONDUSERNAME + ") REFERENCES " + Constants.USER +" (" + Constants.USERNAME + "), " +
                    "CONSTRAINT PK3 PRIMARY KEY (" + Constants.USERNAME + "," + Constants.SECONDUSERNAME + "," + Constants.DESCRIPTION + "," + Constants.DATE + ")" +
                    " )";

    /** delete entries **/
    private static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + Constants.USER;

    private static final String SQL_DELETE_CHAT_TABLE =
            "DROP TABLE IF EXISTS " + Constants.CHAT;

    private static final String SQL_DELETE_ITEM_TABLE =
            "DROP TABLE IF EXISTS " + Constants.ITEM;

    private static final String SQL_DELETE_FAVOURITE_ITEM_TABLE =
            "DROP TABLE IF EXISTS " + Constants.FAVOURITE_ITEM;

    private static final String SQL_DELETE_MESSAGE_TABLE =
            "DROP TABLE IF EXISTS " + Constants.MESSAGE;


    private static final String SQL_DELETE_USERS =
            "DELETE FROM " + Constants.USER;

    private static final String SQL_DELETE_CHATS =
            "DELETE FROM  " + Constants.CHAT;

    private static final String SQL_DELETE_ITEMS =
            "DELETE FROM " + Constants.ITEM;

    private static final String SQL_DELETE_FAVOURITS =
            "DELETE FROM " + Constants.FAVOURITE_ITEM;

    private static final String SQL_DELETE_MESSAGESE =
            "DELETE FROM " + Constants.MESSAGE;


    protected SqlDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        // create tables
        db.execSQL(SQL_CREATE_USER_TABLE);
        ContentValues cv = new ContentValues();

        cv.put(Constants.USERNAME,"aNas");
        cv.put(Constants.FNAME,"Amjad");
        cv.put(Constants.LNAME,"Nassar");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");

        db.insert(Constants.USER,null,cv);

        cv.put(Constants.USERNAME,"aYoun");
        cv.put(Constants.FNAME,"Aiman");
        cv.put(Constants.LNAME,"Younis");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");

        db.insert(Constants.USER,null,cv);

        cv.put(Constants.USERNAME,"jSmith");
        cv.put(Constants.FNAME,"John");
        cv.put(Constants.LNAME,"Smith");
        cv.put(Constants.EMAIL,"asdas@dsad.com");
        cv.put(Constants.PASSWORD,"123456");

        db.insert(Constants.USER,null,cv);

        db.execSQL(SQL_CREATE_ITEM_TABLE);
        db.execSQL(SQL_CREATE_FAROUTIE_ITEM_TABLE);
        db.execSQL(SQL_CREATE_CHAT_TABLE);
        db.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        db.execSQL(SQL_DELETE_MESSAGE_TABLE);
        db.execSQL(SQL_DELETE_CHAT_TABLE);
        db.execSQL(SQL_DELETE_FAVOURITE_ITEM_TABLE);
        db.execSQL(SQL_DELETE_ITEM_TABLE);
        db.execSQL(SQL_DELETE_USER_TABLE);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void clearDatabase(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(SQL_DELETE_MESSAGESE);
        db.execSQL(SQL_DELETE_CHATS);
        db.execSQL(SQL_DELETE_FAVOURITS);
        db.execSQL(SQL_DELETE_ITEMS);

        db.close();
    }

    /**
     * load items by conditions
     * @param items list of items to be loaded
     * @param where conditions
     * @param whereArgs based on values
     * @param groupBy group by what row
     * @param sort sort by what order
     */
    public void getItems(ArrayList<Item> items, String where, String[] whereArgs, String groupBy, String sort){
        SQLiteDatabase db = getReadableDatabase();

        // columns to be selected
        String[] projection = {
                Constants.ID,
                Constants.NAME,
                Constants.DESCRIPTION,
                Constants.CATEGORY,
                Constants.PRICE,
                Constants.IMAGE,
                Constants.USERNAME,
                Constants.SOLD,
                Constants.URI
        };

        Cursor c = db.query(Constants.ITEM, projection, where, whereArgs, groupBy, null, sort);

        Item it;
        byte [] bt;
        Bitmap bitmp = null;

        while (c.moveToNext()){
            // load image

            bt = c.getBlob(5);
            bitmp = Converter.decodeImage(bt);

            User user = getUser(c.getString(6));

            it =  new Item(c.getString(0), c.getString(1), E_Category.valueOf(c.getString(3)), c.getLong(4), c.getString(2) ,bitmp, user);
            it.setSold(c.getInt(7));
            it.setUri(c.getString(8));
            items.add(it);

        }

        c.close();
        db.close();
    }

    public boolean insert(String table, ContentValues cv){
        SQLiteDatabase db = getWritableDatabase();

        long l = db.insert(table,null,cv);

        db.close();

        return l >= 0;
    }

    /**
     * update row in a given table
     * @param table to be updated
     * @param cv content
     * @param cols based on which columns
     * @return if update was successful
     */
    public boolean update(String table, ContentValues cv, String where, String...cols){
        SQLiteDatabase db = getWritableDatabase();

        int i = db.update(table, cv, where, cols);

        db.close();

        return i > 0;
    }

    /**
     * delete rows from a given table
     * @param table what table
     * @param where based on what condition
     * @param cols column vlaues
     * @return if deletion was successful
     */
    public boolean delete(String table, String where, String...cols){
        SQLiteDatabase db = getWritableDatabase();

        int i = db.delete(table, where, cols);

        db.close();

        return i > 0;
    }

    /**
     * get user's items by name, used for search
     * @param list to be filled
     * @param qString item name
     */
    public void getItemByName(ArrayList<Item> list, String qString) {

        final String where = Constants.NAME +" LIKE ?";

        getItems(list, where, new String[]{"%"+ qString + "%"}, null, null);

    }

    /**
     *
     * @param wArgs what column values
     * @return user from database
     */
    public User getUser(String ... wArgs) {

        SQLiteDatabase db = getReadableDatabase();
        String str;

        String[] args = new String[] {Constants.USERNAME ,
                Constants.FNAME ,
                Constants.LNAME ,
                Constants.EMAIL ,
                Constants.IMAGE,
                Constants.PASSWORD

        };

        byte [] bt;
        Bitmap bitmp = null;
        User user = null;
        int i;

        str = Constants.USERNAME + " = ? ";

        Cursor c = db.query(Constants.USER, args, str, wArgs, null, null, null, null);
        // fill args if found
        if(c.moveToNext()) {
            user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), bitmp);
            if(!c.isNull(4)) {
                // load image
                bt = c.getBlob(4);
                bitmp = Converter.decodeImage(bt);
                user.setThumb(bitmp);
            }
        }

        if (user == null)
            Log.e("USER"," no user found");

        c.close();
        db.close();

        return user;
    }

    /**
     * get favourite items for current user
     * @param list to be filed
     * @param where conditions
     * @param wArgs what columns values to get picked
     */
    public void getFaveItems(ArrayList<Item> list, String where, String[] wArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(Constants.FAVOURITE_ITEM, new String[]{Constants.ID},where, wArgs, null, null, null);

        while (c.moveToNext())
            getItems(list, Constants.ID + " = ?", new String[]{c.getString(0)}, null, null);

        c.close();
        db.close();
    }

    public void getChatLogs(ArrayList<Chat> chats, User user) {
        Chat chat;
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + Constants.CHAT + " WHERE " +Constants.USERNAME + " = '"+ user.getUsername()
                + "' UNION " +
                "SELECT * FROM " + Constants.CHAT + " WHERE " + Constants.SECONDUSERNAME + " = '"+ user.getUsername()+"'", null);

        while (c.moveToNext()){
            String uname = c.getString(0);
            String sUname = c.getString(1);

            if(uname.equals(user.getUsername()))
             chat = new Chat(user, getUser(sUname));
            else
                chat = new Chat(getUser(uname), user);


            chat.setMessages(getMsgsForChat(chat.getSender(), chat.getGuest()));


            chats.add(chat);
        }

        c.close();
        db.close();
    }

    public ArrayList<Message> getMsgsForChat(User sender, User user){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Message> msgs = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + Constants.MESSAGE + " WHERE " + Constants.USERNAME + " = '"+ user.getUsername() + "' AND "
                + Constants.SECONDUSERNAME + " = '" + sender.getUsername()
                + "' UNION " +
                "SELECT * FROM " + Constants.MESSAGE + " WHERE " + Constants.USERNAME + " = '"+ sender.getUsername() + "' AND "
                + Constants.SECONDUSERNAME + " = '" + user.getUsername()+"' ORDER BY " + Constants.DATE + " ASC", null);
        Message obj;

        while (c.moveToNext()){
            String uname = c.getString(0),
                    msg = c.getString(2),
                    date = c.getString(3);

            obj = new Message(getUser(uname), msg);
            obj.setDate(date);
            msgs.add(obj);

        }

        c.close();
        db.close();
        return msgs;
    }
}
