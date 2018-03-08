package course.android.letgo_307945402_204317770.Network;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import course.android.letgo_307945402_204317770.Objects.Chat;
import course.android.letgo_307945402_204317770.Objects.Item;
import course.android.letgo_307945402_204317770.Objects.User;
import course.android.letgo_307945402_204317770.Utills.Constants;
import course.android.letgo_307945402_204317770.Utills.Converter;


public class NetworkConnector {

    private static NetworkConnector mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    // server address
    private final String PORT = "8080";
    private final String IP = "132.74.213.48";
    private final String HOST_URL = "http://" + IP + ":" + PORT +"/";
    private final String BASE_URL = HOST_URL + "projres";

    // server requests
    public static final String GET_ALL_ITEMS_JSON_REQ = "0";
    public static final String GET_USER_JSON_REQ = "1";
    public static final String GET_MESSAGES_OF_CHAT_JSON_REQ = "2";
    public static final String INSERT_USER_REQ = "3";
    public static final String INSERT_USER_IMAGE_REQ = "4";
    public static final String INSERT_ITEM_REQ = "5";
    public static final String INSERT_MESSAGE_REQ = "6";
    public static final String DELETE_ITEM_REQ = "7";
    public static final String DELETE_USER_REQ = "8";
    public static final String GET_ITEM_IMAGE_REQ = "10";
    public static final String GET_USER_IMAGE_REQ = "11";
    public static final String INSERT_ITEM_FAVE_REQ = "13";
    public static final String GET_FAVE_ITEM_USER_REQ = "14";
    public static final String REMOVE_FAVE_ITEM = "15";
    public static final String GET_SOLD_ITEM_USER_REQ = "16";
    public static final String UNSYNCED_MESSAGES = "17";

    private String tempReq;
    private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
    private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

    public static final String REQ = "req";


    private NetworkConnector() {

    }

    public static synchronized NetworkConnector getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkConnector();
        }
        return mInstance;
    }

    public void initialize(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    private void addToRequestQueue(String query, final NetworkResListener listener) {

        String reqUrl = BASE_URL + "?" + query;
        notifyPreUpdateListeners(listener);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, reqUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        notifyPostUpdateListeners(response, ResStatus.SUCCESS, listener);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        JSONObject err = null;
                        try {
                            err = new JSONObject(RESOURCE_FAIL_TAG);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(err, ResStatus.FAIL, listener);
                        }

                    }
                });

        getRequestQueue().add(jsObjRequest);
    }

    private void addImageRequestToQueue(String query, final NetworkResListener listener){

        String reqUrl = BASE_URL + "?" + query;

        notifyPreUpdateListeners(listener);

        getImageLoader().get(reqUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                notifyPostBitmapUpdateListeners(bm, ResStatus.SUCCESS, listener);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                notifyPostBitmapUpdateListeners(null, ResStatus.FAIL, listener);
            }
        });
    }

    private ImageLoader getImageLoader() {
        return mImageLoader;
    }


    private void uploadItemImage(final Item item, final NetworkResListener listener) {

        String reqUrl = HOST_URL + "web_item_manage?";
        notifyPreUpdateListeners(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyPostUpdateListeners(obj, ResStatus.SUCCESS, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(obj, ResStatus.FAIL, listener);
                        }

                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.ID, item.getId());
                params.put(Constants.NAME, item.getName());
                params.put(Constants.DESCRIPTION,  item.getDesc());
                params.put(Constants.CATEGORY, item.getCategory().getType());
                params.put(Constants.PRICE, String.valueOf(item.getPrice()));
                params.put(Constants.USERNAME, item.getUser().getUsername());
                params.put(Constants.SOLD, String.valueOf(item.isSold()));
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                byte[] pic = Converter.getBitmapAsByteArray(item.getImage());
                params.put("fileField", new DataPart(imagename + ".png", pic));
                return params;
            }
        };

        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }


    public void sendRequestToServer(String requestCode, Item data, NetworkResListener listener){

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_ITEM_REQ:{

                uploadItemImage(data, listener);

                break;
            }
            case DELETE_ITEM_REQ:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , data.getId());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }
            case GET_ITEM_IMAGE_REQ: {

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , data.getId());

                String query = builder.build().getEncodedQuery();
                addImageRequestToQueue(query, listener);
                break;
            }

            case REMOVE_FAVE_ITEM:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.ID , data.getId());
                builder.appendQueryParameter(Constants.USERNAME , data.getUser().getUsername());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }
        }
    }


    public void update(NetworkResListener listener){

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter(REQ , GET_ALL_ITEMS_JSON_REQ);
        String query = builder.build().getEncodedQuery();

        addToRequestQueue(query, listener);
    }

    /**
     * used for chat upload
     * @param requestCode
     * @param data
     * @param listener
     */
    public void sendRequestToServer(String requestCode, Chat data, User user, NetworkResListener listener) {

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_MESSAGE_REQ:{
                builder.appendQueryParameter(REQ , requestCode);

                builder.appendQueryParameter(Constants.USERNAME , user.getUsername());
                if(!data.getSender().equals(user)){
                    builder.appendQueryParameter(Constants.SECONDUSERNAME , data.getSender().getUsername());
                }else{
                    builder.appendQueryParameter(Constants.SECONDUSERNAME , data.getGuest().getUsername());
                }

                builder.appendQueryParameter(Constants.DESCRIPTION , data.getLastMessage().getMessage());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }
        }
    }

    /**
     * used for register user
     * @param requestCode
     * @param data
     * @param listener
     */
    public void sendRequestToServer(String requestCode, User data, NetworkResListener listener) {

        if(data==null){
            return;
        }

        Uri.Builder builder = new Uri.Builder();
        tempReq = requestCode;

        switch (requestCode){
            case INSERT_USER_REQ:{

                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME ,data.getUsername());
                builder.appendQueryParameter(Constants.FNAME ,data.getFirstName());
                builder.appendQueryParameter(Constants.LNAME ,data.getLastName());
                builder.appendQueryParameter(Constants.EMAIL ,data.getEmail());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }
            case INSERT_USER_IMAGE_REQ: {
                uploadUserImage(data, listener);
                break;
            }
            case DELETE_USER_REQ: case GET_SOLD_ITEM_USER_REQ: case GET_FAVE_ITEM_USER_REQ:case GET_USER_JSON_REQ:case GET_MESSAGES_OF_CHAT_JSON_REQ:case UNSYNCED_MESSAGES:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME ,data.getUsername());

                String query = builder.build().getEncodedQuery();
                addToRequestQueue(query, listener);
                break;
            }

            case GET_USER_IMAGE_REQ:{
                builder.appendQueryParameter(REQ , requestCode);
                builder.appendQueryParameter(Constants.USERNAME ,data.getUsername());

                String query = builder.build().getEncodedQuery();
                addImageRequestToQueue(query, listener);
                break;
            }
        }
    }

    private void uploadUserImage(final User item, final NetworkResListener listener) {

        String reqUrl = HOST_URL + "web_user_manage?";
        notifyPreUpdateListeners(listener);


        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, reqUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            notifyPostUpdateListeners(obj, ResStatus.SUCCESS, listener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show();
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(RESOURCE_FAIL_TAG );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            notifyPostUpdateListeners(obj, ResStatus.FAIL, listener);
                        }

                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.USERNAME ,item.getUsername());
                params.put(Constants.FNAME ,item.getFirstName());
                params.put(Constants.LNAME ,item.getLastName());
                params.put(Constants.EMAIL ,item.getEmail());
                return params;
            }
            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                if (item.getThumb() != null) {
                    byte[] pic = Converter.getBitmapAsByteArray(item.getThumb());
                    params.put("fileField", new DataPart(imagename + ".png", pic));
                }
                return params;
            }
        };

        //adding the request to volley
        getRequestQueue().add(volleyMultipartRequest);
    }

    private void clean() {

    }


    private  void notifyPostBitmapUpdateListeners(final Bitmap res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onPostUpdate(res, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private  void notifyPostUpdateListeners(final JSONObject res, final ResStatus status, final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                    listener.onPostUpdate(res, tempReq, status);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }

    private void notifyPreUpdateListeners(final NetworkResListener listener) {

        Handler handler = new Handler(mCtx.getMainLooper());

        Runnable myRunnable = new Runnable() {

            @Override
            public void run() {
                try{
                        listener.onPreUpdate(tempReq);
                }
                catch(Throwable t){
                    t.printStackTrace();
                }
            }
        };
        handler.post(myRunnable);

    }
}
