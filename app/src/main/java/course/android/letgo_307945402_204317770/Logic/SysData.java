package course.android.letgo_307945402_204317770.Logic;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import course.android.letgo_307945402_204317770.Network.NetworkConnector;
import course.android.letgo_307945402_204317770.Network.NetworkResListener;
import course.android.letgo_307945402_204317770.Network.ResStatus;
import course.android.letgo_307945402_204317770.Objects.Chat;
import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.Objects.Message;
import course.android.letgo_307945402_204317770.Objects.User;
import course.android.letgo_307945402_204317770.R;
import course.android.letgo_307945402_204317770.Utills.Check;
import course.android.letgo_307945402_204317770.Utills.Constants;
import course.android.letgo_307945402_204317770.Utills.Converter;
import course.android.letgo_307945402_204317770.Utills.DateUtil;
import course.android.letgo_307945402_204317770.Utills.E_Category;

public class SysData implements NetworkResListener{

    // for Singleton design pattern implementation
    private static SysData instance;
    private static SqlDatabase sqlData;
    private User user;
    private ArrayList<Item> items;
    private ArrayList<Chat> chats;
    private Context context;
    private ArrayList<User> currentUsers;

    public static boolean dataEdited;
    public static boolean itemSold;

    private SysData(){

    }

    /**
     * initializes the SysData if wasn't already have benn done, else returns existing object
     * @return SysData object
     */
    public static SysData getInstance() {
        if(instance != null)
            return instance;

        else{
            instance = new SysData();
            return instance;
        }
    }

    /**
     * create and open database
     * @param ctx
     */
    public void openDB(Context ctx) {
        context = ctx;
        sqlData = new SqlDatabase(context);
    }

    /**
     * close database and save preferences if needed
     */
    public void closeDataBase() {
        if (sqlData != null)
            sqlData.close();

        sqlData = null;
    }

    /**
     * load items from database
     */
    public void initItems(boolean takeFromDB) {
        items = new ArrayList<>();
        // get items that hasn't been sold yet
        if (takeFromDB)
            sqlData.getItems(items, Constants.SOLD + " = ?",new String[]{String.valueOf(0)},null,null);

    }

    /**
     * init user
     * @return
     */
    public User initUser(){
        SharedPreferences preferences = context.getSharedPreferences(R.string.app_name+"prefs", Context.MODE_PRIVATE);
        String username = preferences.getString(Constants.USERNAME,"--");

        return initUser(username);

    }

    /**
     * save username for automatic login
     * @param username
     */
    private void saveUserPrefs(String username) {
        SharedPreferences preferences = context.getSharedPreferences(R.string.app_name+"prefs", Context.MODE_PRIVATE);
        preferences.edit().putString(Constants.USERNAME, username)
                .apply();
    }

    /**
     *
     * initialize user, to be used in log in activity when created.
     * @param username user's entered username
     * @return user object if user logged in, if not return null, which indicates that user doesn't exist in database
     */
    public User initUser(String username) {
        user = sqlData.getUser(username);  // load user from database

        if(user != null){ // user logged in, load content, else user doesn't exist in database and didn't log in
            saveUserPrefs(username);
            currentUsers = new ArrayList<>();
        }

        return user;
    }

    /**
     * init chat objects
     */
    public void initChats(){
        chats = new ArrayList<>();
        sqlData.getChatLogs(chats, user);

        if(chats.isEmpty()) {
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_MESSAGES_OF_CHAT_JSON_REQ, user, new NetworkResListener() {
                @Override
                public void onPreUpdate(String str) {

                }

                @Override
                public void onPostUpdate(JSONObject res, String table, ResStatus status) {
                    if (status == ResStatus.SUCCESS) {
                        updateChatsDataBase(res);
                    }
                }

                @Override
                public void onPostUpdate(Bitmap res, ResStatus status) {

                }
            });
        }
    }

    /**
     * used to update chat messages or
     * @param res
     */
    public void initChats(JSONObject res) {
        Chat c, chat;
        JSONArray arr = null;

        try {
            ContentValues cv, mcv;

            int i;
            arr = res.getJSONArray(Constants.CHATS);

            for(i = 0; i < arr.length(); i++){
                c = Chat.parseJSON(arr.getJSONObject(i));
                if(c != null){

                    cv = c.getContent();

                    if (!c.getSender().equals(user))
                        sqlData.insert(Constants.USER, c.getSender().getContent());
                    else
                        sqlData.insert(Constants.USER, c.getGuest().getContent());

                    sqlData.insert(Constants.CHAT, cv);

                    for(Message m : c.getMessages()){
                        mcv = new ContentValues();

                        // matching who sent and who received the message
                        if(m.getSender().equals(user)){
                            mcv.put(Constants.USERNAME, user.getUsername());
                            if(c.getGuest().equals(user))
                                mcv.put(Constants.SECONDUSERNAME, c.getSender().getUsername());
                            else
                                mcv.put(Constants.SECONDUSERNAME, c.getGuest().getUsername());
                        }else {

                            mcv.put(Constants.USERNAME, m.getSender().getUsername());
                            if(c.getGuest().equals(user)){
                                mcv.put(Constants.SECONDUSERNAME, c.getGuest().getUsername());
                            }else {
                                mcv.put(Constants.SECONDUSERNAME, c.getSender().getUsername());
                            }

                        }
                        mcv.put(Constants.DESCRIPTION, m.getMessage());
                        mcv.put(Constants.DATE, DateUtil.getCurrentDate(m.getDateFormat()));
                        sqlData.insert(Constants.MESSAGE, mcv);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("HIII", e.getMessage());
        }
    }

    /**
     * update download chat convos
     * @param res
     */
    public void updateChatsDataBase(JSONObject res) {
        Chat c, chat;
        JSONArray arr = null;
        try {
            ContentValues cv, mcv;

            int i;
            arr = res.getJSONArray(Constants.CHATS);

            for(i = 0; i < arr.length(); i++){
                c = Chat.parseJSON(arr.getJSONObject(i));
                if(c != null){
                    if(chats.contains(c)) {
                        chat = chats.get(chats.indexOf(c));
                        for (Message m : c.getMessages()) {
                            chat.addMessege(m);
                        }
                    } else {
                        cv = c.getContent();

                        if (!c.getSender().equals(user))
                            sqlData.insert(Constants.USER, c.getSender().getContent());
                        else
                            sqlData.insert(Constants.USER, c.getGuest().getContent());

                        sqlData.insert(Constants.CHAT, cv);
                        chats.add(c);
                    }
                    for(Message m : c.getMessages()){
                        mcv = new ContentValues();
                        // matching who sent and who received the message
                        if(m.getSender().equals(user)){
                            mcv.put(Constants.USERNAME, user.getUsername());
                            if(c.getGuest().equals(user))
                                mcv.put(Constants.SECONDUSERNAME, c.getSender().getUsername());
                            else
                                mcv.put(Constants.SECONDUSERNAME, c.getGuest().getUsername());
                        }else {

                            mcv.put(Constants.USERNAME, m.getSender().getUsername());
                            if(c.getGuest().equals(user)){
                                mcv.put(Constants.SECONDUSERNAME, c.getGuest().getUsername());
                            }else {
                                mcv.put(Constants.SECONDUSERNAME, c.getSender().getUsername());
                            }

                        }
                        mcv.put(Constants.DESCRIPTION, m.getMessage());
                        mcv.put(Constants.DATE, DateUtil.getCurrentDate(m.getDateFormat()));
                        sqlData.insert(Constants.MESSAGE, mcv);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

    }

    public ArrayList<Chat> getChats() {
        return chats;
    }


    /**
     *
     * @return all downloaded items
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * query items that matches category
     * @param e_category item's category
     * @return ArrayList<Item> list items by given category
     */
    public ArrayList<Item> getItems(ArrayList<Item> its, E_Category e_category) {
        ArrayList<Item> list = new ArrayList<>();

        for(Item t : its)
            if(t.getCategory().equals(e_category))
                list.add(t);

        return list;
    }

    /**
     *
     * @return user's item stored in database
     */
    public ArrayList<Item> getUserItems(){
        ArrayList<Item> list = new ArrayList<>();

        //sqlData.getItems(list, Constants.USERNAME + " = ? AND " + Constants.SOLD + " = ?", new String[]{user.getUsername(), String.valueOf(0)} , null, null);
        for(Item t : items)
            if(t.getUser().equals(user))
                list.add(t);

        return list;
    }

    /**
     * get sold items from database, for currently logged user
     * @return sold items
     */
    public void getSoldItems() {
        final ArrayList<Item> soldItems = new ArrayList<>();
        user.setSoldItems(soldItems);
        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_SOLD_ITEM_USER_REQ, user, new NetworkResListener() {
            @Override
            public void onPreUpdate(String str) {

            }

            @Override
            public void onPostUpdate(JSONObject res, String table, ResStatus status) {
                if (status == ResStatus.SUCCESS)
                    updateDataBase(null, soldItems, Constants.ITEM, res);
                else {
                    sqlData.getItems(soldItems, Constants.USERNAME + " = ? AND " + Constants.SOLD + " = ?", new String[]{user.getUsername(), String.valueOf(1)}, null, null);
                }

            }

            @Override
            public void onPostUpdate(Bitmap res, ResStatus status) {

            }
        });

    }

    /**
     * get favourite items from database, for currently logged user
     * @return favourite items
     */
    public void getFaveItems() {
        final ArrayList<Item> list = new ArrayList<>();
        user.setFavItems(list);
        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_FAVE_ITEM_USER_REQ, user, new NetworkResListener() {
            @Override
            public void onPreUpdate(String str) {

            }

            @Override
            public void onPostUpdate(JSONObject res, String table, ResStatus status) {
                if (status == ResStatus.SUCCESS)
                    updateDataBase(null, list, Constants.FAVOURITE_ITEM, res);
                else {
                    sqlData.getFaveItems(list, Constants.USERNAME + " = ? ", new String[]{user.getUsername()});
                }
            }

            @Override
            public void onPostUpdate(Bitmap res, ResStatus status) {

            }
        });
    }

    /**
     * insert a given item to database
     * @param img item image
     * @param price item price
     * @param args item name, description, category
     * @return if the item was inserted to database and system
     */
    public boolean sellItem(Bitmap img, long price,  String ... args){
        Random r = new Random(System.currentTimeMillis());
        RandomString rs = new RandomString(12,r);  // get random string id

        if(Check.isLegalString(args[0]) && Check.isLegalString(args[1]) &&
                Check.isLegalString(args[2])) {
            ContentValues cv;

            Item it = new Item(rs.nextString(), args[0], E_Category.valueOf(args[2]), price, args[1], img, user);
            it.setUri(args[3]);
            // use ContentValues to pass the columns and the values to be inserted
            cv = it.getContent(Constants.ITEM);

            if (insertToDatabase(Constants.ITEM, cv)) {
                items.add(it);
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_REQ, it, this);
                return user.sellItem(it);
            }
        }
        return false;
    }

    /**
     *
     * @return current logged user
     */
    public User getUser() {
        return user;
    }

    /**
     * insret rows to given table
     * @param table name
     * @param cv object values
     * @return if insert was successful
     */
    public boolean insertToDatabase(String table, ContentValues cv){
        return sqlData.insert(table, cv);
    }

    /**
     * update item details in database after edit
     * @param it the item
     * @param img the image
     * @return if update was successful
     */
    public boolean updateItems(Item it, Bitmap img, long price, boolean updateOnline,  String ... args){
        dataEdited = false;
        if(Check.isLegalString(args[0]) && Check.isLegalString(args[1]) &&
                Check.isLegalString(args[2])) {
            Item temp = new Item(it.getId(), args[0], E_Category.valueOf(args[2]), price, args[1], img, it.getUser()); // copy new values


            //String uri = Converter.saveImage(temp.getId(), img);
            temp.setUri(args[3]);

            ContentValues cv = temp.getContent(Constants.ITEM);
            final String where = Constants.ID + " = ?";
            if (sqlData.update(Constants.ITEM, cv, where, temp.getId())){ // if new values updated append original

                it.setName(args[0]);
                it.setDesc(args[1]);
                it.setUri(args[3]);
                it.setCategory(E_Category.valueOf(args[2]));
                it.setPrice(price);
                it.setImage(img);

                if(updateOnline)
                    NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_REQ, it, this);
                dataEdited = true;
                return true;
            }

        }
        return false;

    }

    /**
     * mark product as sold
     * @param pos
     * @return
     */
    public boolean markSold(int pos){

        Item it = user.getItemOnSale().get(pos);

        itemSold = false;
        dataEdited = false;

        it.setSold(1);
        final String where = Constants.ID + " = ?";
        if(sqlData.update(Constants.ITEM, it.getContent(Constants.ITEM), where, it.getId())){
            user.getItemOnSale().remove(pos);
            items.remove(it);
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_REQ, it, this);
            itemSold = true;
            dataEdited = true;
            return user.getSoldItems().add(it);
        }

        return false;

    }

    /**
     * delete row from a given table
     * @param args table name, where condition, condition value
     * @return if delete was successful
     */
    public boolean deleteFromDatabase(String ... args){
        dataEdited = false;
        if(sqlData.delete(args[0], args[1], args[2])){

            if(args[0].equals(Constants.FAVOURITE_ITEM))
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.REMOVE_FAVE_ITEM, new Item(args[2]), this);
            else
                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.DELETE_ITEM_REQ, new Item(args[2]), this);

            dataEdited = true;
        }
        return dataEdited;
    }

    /**
     * get item by given name
     * @param qString item name for search
     * @return items that match name pattern
     */
    public ArrayList<Item> getItemsByQuery(String qString) {
        ArrayList<Item> list = new ArrayList<>();

        sqlData.getItemByName(list, qString);

        return list;
    }

    /**
     * add item to favourites table
     * @param it item to be added
     * @return if addition was successful
     */
    public boolean addToFavourites(Item it) {
        ContentValues cv = it.getContent(Constants.FAVOURITE_ITEM);

        if (sqlData.insert(Constants.FAVOURITE_ITEM, cv)) {
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_ITEM_FAVE_REQ, it, this);
            return user.getFavItems().add(it);
        }

        return false;
    }

    /**
     *
     * @param index of item in on sale items list
     * @return item
     */
    public Item getItem(int index) {
        return user.getItemOnSale().get(index);
    }

    public boolean saveProfChanges(String uFname, String uLname, String uEmail, Bitmap img) {

        ContentValues cv;
        final String where = Constants.USERNAME + "=  ?";

        if(Check.isName(uFname) && Check.isName(uLname)) {

            cv = new User(user.getUsername(), uFname, uLname, uEmail, img).getContent();

            if (sqlData.update(Constants.USER, cv, where, user.getUsername())) {
                user.setFirstName(uFname);
                user.setLastName(uLname);
                user.setEmail(uEmail);
                user.setThumb(img);

                NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_USER_IMAGE_REQ, user, this);

                return true;
            }
        }
        return false;
    }

    /**
     * create new chat object
     * @param guest
     * @return
     */
    public int newChat(User guest){
        if(guest != user) {
            Chat chat = new Chat(user, guest);
            if (!chats.contains(chat)) {
                ContentValues cv = chat.getContent();

                if (sqlData.insert(Constants.CHAT, cv)) {
                    chats.add(chat);
                }
            }
            return chats.indexOf(chat);
        }
        return -1;
    }

    /**
     * send message to other user
     * @param chat
     * @param msg
     * @return
     */
    public boolean snedMessage(Chat chat, String msg) {
        ContentValues cv = new ContentValues();
        // matching sender and receiver and sending message
        cv.put(Constants.USERNAME, user.getUsername());
        if(chat.getGuest().equals(user))
            cv.put(Constants.SECONDUSERNAME, chat.getSender().getUsername());
        else
            cv.put(Constants.SECONDUSERNAME, chat.getGuest().getUsername());
        cv.put(Constants.DESCRIPTION, msg);
        cv.put(Constants.DATE, DateUtil.getCurrentDate(System.currentTimeMillis()));

        if (sqlData.insert(Constants.MESSAGE, cv)) {

            Message messageYemp = new Message(user, msg);
            chat.addMessege(messageYemp);
            NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.INSERT_MESSAGE_REQ, chat, user, this);
            return true;
        }
        return false;
    }

    @Override
    public void onPreUpdate(String str) {

    }

    @Override
    public void onPostUpdate(JSONObject res, String table, ResStatus status) {
        if(status != ResStatus.SUCCESS)
            Toast.makeText(context, "sync Unsuccessful", Toast.LENGTH_SHORT).show();
        /*else {
            Toast.makeText(context, "sync Unsuccessful", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onPostUpdate(Bitmap res, ResStatus status) {

    }

    /**
     * clear every thing for log out process
     */
    public void logOut() {

        items = null;
        sqlData.clearDatabase();
        user.setItemOnSale(null);
        user.setFavItems(null);
        user.setSoldItems(null);
        user = null;
        currentUsers = null;
        chats = null;
        saveUserPrefs("--");

    }

    public void updateDataBase(final ItemAdapter adapter, final ArrayList<Item> list, final String table, JSONObject res) {

        try {
            int i;
            JSONArray array = res.getJSONArray(Constants.ITEMS);
            for (i = 0; i < array.length(); i++) {
                final Item item = Item.parseJSON(array.getJSONObject(i));
                if (item != null){
                    if (item.isHasOnlineImage()) { // download item image
                        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.GET_ITEM_IMAGE_REQ, item, new NetworkResListener() {
                            @Override
                            public void onPreUpdate(String str) {

                            }

                            @Override
                            public void onPostUpdate(JSONObject res, String table, ResStatus status) {

                            }

                            @Override
                            public void onPostUpdate(Bitmap res, ResStatus status) {
                                if (status == ResStatus.SUCCESS)
                                    if (res != null) {
                                        item.setImage(res);
                                        item.setUri(Converter.saveImage(item.getId(), res));

                                        if (!sqlData.insert(table, item.getContent(table))){
                                            if (sqlData.update(table, item.getContent(table), Constants.ID + " = ? AND " + Constants.USERNAME + " = ? ", item.getId(), item.getUser().getUsername()))
                                                list.add(item);
                                        }
                                        else
                                            list.add(item);
                                    }
                                if(adapter != null)
                                    adapter.updateList(list);
                            }
                        });
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearItems() {
        items.clear();
    }

    /**
     * get user that is other than current user in session
     * @param username
     * @return
     */
    public User getUser(String username){
        User u;
        if(user.equals(new User(username)))
            return user;

        if(!currentUsers.contains(new User(username))){
            u = sqlData.getUser(username);
            currentUsers.add(u);
        }else
            u = currentUsers.get(currentUsers.indexOf(new User(username)));

        return u;
    }

    // remove chat
    public boolean discardChat(Chat chat) {
        sqlData.delete(Constants.CHAT, Constants.USERNAME + "=? AND " + Constants.SECONDUSERNAME + "=?",
                chat.getSender().getUsername(), chat.getGuest().getUsername());

        return chats.remove(chat);

    }

    public void getChatMsgsFromDB(Chat chat) {
        chat.setMessages(sqlData.getMsgsForChat(chat.getSender(), chat.getGuest()));
    }

    public boolean dbIsClosed() {
        return sqlData == null;
    }

    /**
     *
     * @param its
     * @param pricefrom loest price
     * @param priceto max price
     * @param catagories
     * @param catagories
     * @return all the items after using the filter
     */

    public ArrayList<Item> getItemsAfterFilter(ArrayList<Item> its, Long pricefrom, Long priceto, String catagories){

        ArrayList<Item> afterFilter=new ArrayList<>();
        String[] catagory;
        if(catagories.equals("")){

            if(pricefrom == -1 && priceto == -1){
                return its;
            }

            if(pricefrom != -1 && priceto == -1){
                for(Item m : its) {
                    if (m.getPrice() >= pricefrom) {
                        afterFilter.add(m);
                    }
                }
            }

            if(priceto != -1 && pricefrom ==-1){
                for(Item m : its) {
                    if (m.getPrice() <= priceto) {
                        afterFilter.add(m);
                    }
                }
            }

            if(pricefrom != -1 && priceto != -1){
                for(Item m : its) {
                    if (m.getPrice() <= priceto && m.getPrice() >= pricefrom) {
                        afterFilter.add(m);
                    }
                }
            }
        }else{
            catagory  = catagories.split(" ");
            if(pricefrom == -1 && priceto == -1){
                for(Item m : its){
                    for(String s : catagory){
                        if(m.getCategory().equals(E_Category.valueOf(s))){
                            afterFilter.add(m);
                        }
                    }
                }
            }
            if(pricefrom != -1 && priceto == -1){
                for(Item m : its) {
                    for(String s : catagory){
                        if(m.getCategory().equals(E_Category.valueOf(s))) {
                            if (m.getPrice() >= pricefrom) {
                                afterFilter.add(m);
                            }
                        }
                    }
                }
            }
            if(priceto != -1 && pricefrom ==-1){
                for(Item m : its) {
                    for(String s : catagory){
                        if(m.getCategory().equals(E_Category.valueOf(s))) {
                            if (m.getPrice() <= priceto) {
                                afterFilter.add(m);
                            }
                        }
                    }
                }
            }
            if(pricefrom != -1 && priceto != -1){
                for(Item m : its) {
                    for (String s : catagory) {
                        if (m.getCategory().equals(E_Category.valueOf(s))) {
                            if (m.getPrice() <= priceto && m.getPrice() >= pricefrom) {
                                afterFilter.add(m);
                            }
                        }
                    }
                }
            }
        }

        return afterFilter;
    }



}
