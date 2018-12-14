package course.android.shopping_example_app.Services;

import java.util.List;

import course.android.shopping_example_app.GUI.ChatLogsActivity;
import course.android.shopping_example_app.Logic.SysData;
import course.android.shopping_example_app.Network.NetworkConnector;
import shopping_example_app.R;
import course.android.shopping_example_app.Utills.Constants;
import course.android.shopping_example_app.Utills.CyclerManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotifyierService extends Service implements IResultReceiver {
	// broadcast receiver name/action
	public static final String BROADCAST = "course.android.shopping_example_app.ActivityReceiver";
	 // Notification ID to allow for future updates
	public static final int MY_NOTIFICATION_ID = 1;

	public NotifyierService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

			NetworkConnector.getInstance().initialize(getApplicationContext());
			boolean wasClosed = SysData.getInstance().dbIsClosed();

			if (wasClosed) {
				SysData.getInstance().openDB(getApplicationContext());

			}
			if (SysData.getInstance().getUser() == null){
				if(SysData.getInstance().initUser() != null){
					CyclerManager.getInstance().start();
					CyclerManager.getInstance().subscribeForResults(this);
				}
			}else {
				CyclerManager.getInstance().start();
				CyclerManager.getInstance().subscribeForResults(this);
			}


	}

	@Override
	public void onDestroy() {
		//Toast.makeText(getApplicationContext(), "MessagesNotifier Service Stopped", Toast.LENGTH_SHORT).show();
		//CyclerManager.getInstance().unsubscibeForResults(this);
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(getApplicationContext(), "MessagesNotifier Service Working", Toast.LENGTH_SHORT).show();
		return Service.START_STICKY;

	}

	@Override
	public void onRecieve(List<JSONObject> results) {
		notifyUser(results);
	}

	private void notifyUser(List<JSONObject> results) {

		// Notification Action Elements
		Intent mNotificationIntent;
		PendingIntent mContentIntent;

		mNotificationIntent = new Intent(getApplicationContext(), ChatLogsActivity.class);
		mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mNotificationIntent,
				Intent.FILL_IN_ACTION);

		// Define the Notification's expanded message and Intent:
		// Notification Sound and Vibration on Arrival
		Uri soundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long[] mVibratePattern = { 0, 200, 200, 300 };

		// Notification Text Elements
		String contentTitle = "Letgo";
		StringBuffer sb = new StringBuffer();

		JSONArray arr;
		JSONObject j;
		String str;

		// building notification string
		for(JSONObject s : results) {
			haneleResults(s);
			try {
				arr = s.getJSONArray(Constants.CHATS);

				for (int i = 0;i < arr.length(); i++){
					j = arr.getJSONObject(i);
					str = j.getJSONArray(Constants.MESSAGES).getJSONObject(0).getString(Constants.SENDER);
					sb.append("You have new messages from " + str +"\n");

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		Intent intent = new Intent();
		intent.setAction(BROADCAST);

		// sending broadcast to activity receiver
		getApplicationContext().sendBroadcast(intent);

		// building notification
		String contentText = sb.toString();

		Notification.Builder notificationBuilder = new Notification.Builder(getApplicationContext());

		notificationBuilder.setSmallIcon(R.drawable.logo);
		notificationBuilder.setAutoCancel(true);
		notificationBuilder.setContentTitle(contentTitle);
		notificationBuilder.setContentText(contentText);
		notificationBuilder.setContentIntent(mContentIntent).setSound(soundURI);
		notificationBuilder.setVibrate(mVibratePattern);

		// Pass the Notification to the NotificationManager:
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(MY_NOTIFICATION_ID, notificationBuilder.build());

	}

	private void haneleResults(JSONObject res){
		boolean wasClosed = SysData.getInstance().dbIsClosed();

		if (wasClosed)
			SysData.getInstance().openDB(getApplicationContext());
		SysData.getInstance().initChats(res);

		if (wasClosed)
			SysData.getInstance().closeDataBase();

	}

}
