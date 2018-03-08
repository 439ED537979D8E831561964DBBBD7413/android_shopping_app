package course.android.letgo_307945402_204317770.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import course.android.letgo_307945402_204317770.Logic.SysData;
import course.android.letgo_307945402_204317770.Network.NetworkConnector;
import course.android.letgo_307945402_204317770.Network.NetworkResListener;
import course.android.letgo_307945402_204317770.Network.ResStatus;
import course.android.letgo_307945402_204317770.Utills.Constants;


public class BootStartUpReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, NotifyierService.class);
        context.startService(service);

    }
    /*
    private void connect(final Context context, final boolean wasClosed){
        NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.UNSYNCED_MESSAGES, SysData.getInstance().getUser(), new NetworkResListener() {
            @Override
            public void onPreUpdate(String str) {

            }

            @Override
            public void onPostUpdate(JSONObject res, String table, ResStatus status) {
                if(status == ResStatus.SUCCESS){

                    if(SysData.getInstance().getChats() == null)
                        SysData.getInstance().initChats(res);
                    else
                        SysData.getInstance().updateChatsDataBase(res);

                    if (wasClosed)
                        SysData.getInstance().closeDataBase();

                    launchService(context, res);
                }else
                if (wasClosed)
                    SysData.getInstance().closeDataBase();

            }

            @Override
            public void onPostUpdate(Bitmap res, ResStatus status) {

            }
        });
    }

    private void launchService(Context context, JSONObject res) {
        int count = 0;
        try {
            count = res.getJSONArray(Constants.CHATS).length();
        } catch (JSONException e) {
            Log.e("Error: ",e.getMessage());
        }



    }*/
}
