package course.android.shopping_example_app.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


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
