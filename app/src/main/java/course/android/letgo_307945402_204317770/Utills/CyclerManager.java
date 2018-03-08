package course.android.letgo_307945402_204317770.Utills;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import course.android.letgo_307945402_204317770.Logic.SysData;
import course.android.letgo_307945402_204317770.Network.NetworkConnector;
import course.android.letgo_307945402_204317770.Network.NetworkResListener;
import course.android.letgo_307945402_204317770.Network.ResStatus;
import course.android.letgo_307945402_204317770.Services.IResultReceiver;

public class CyclerManager {

	private ResThread rThread = null;
	private  static CyclerManager instance = null;
	private  boolean initialized = false;

	private  List<IResultReceiver> listeneres = new ArrayList<>();
	private  List<JSONObject> resultsList = new ArrayList<>();
	private  List<IResultReceiver>  tempUnsubbed = new ArrayList<>();

	private long delayPeriod = 3000; //XXX milliseconds

	public static CyclerManager getInstance() {

		if (instance == null) {
			instance = new CyclerManager();

		}
		return instance;
	}

	private CyclerManager() {

	}

	public long getDelayPeriod() {
		return delayPeriod;
	}

	public void setDelayPeriod(long delayPeriod) {
		this.delayPeriod = delayPeriod;
	}

	public static void releaseInstance() {
		if (instance != null) {

			instance.stop();
			instance = null;

		}

	}

	public void start() {
		if (!initialized) {
			rThread = new ResThread();
			rThread.start();
			initialized = true;
		}
	}

	public void stop() {
		rThread.stopThread();
		resultsList.clear();
		listeneres.clear();
		tempUnsubbed.clear();
	}

	public void deliverResults() {
		List<JSONObject> result = new ArrayList<>();
		result.addAll(resultsList);
		for (IResultReceiver o : listeneres) {
			o.onRecieve(result);
		}
		resultsList.clear();
	}

	public  void subscribeForResults(IResultReceiver receiver) {
		if (!listeneres.contains(receiver)) {
			listeneres.add(receiver);
		}
	}

	public  boolean unsubscibeForResults(IResultReceiver receiver) {
		if (listeneres.contains(receiver)) {
			return listeneres.remove(receiver);
		}
		return false;
	}

    public void unSubOther(IResultReceiver onNotice) {

		for(IResultReceiver rec : listeneres){
			if(!rec.equals(onNotice))
				tempUnsubbed.add(rec);
		}
		listeneres.clear();
		listeneres.add(onNotice);
    }

    public void subOther(){

		for(IResultReceiver rec : tempUnsubbed){
			listeneres.add(rec);
		}
		tempUnsubbed.clear();
	}

    // thread - the core of the process timing
	public class ResThread extends Thread {

		public static final int Stop = 0;
		public static final int Play = 1;
		public static final int Pause = 2;

		int state;
		
		private boolean mStarted;
		public boolean mRunning = true;
		Random r = new Random();

		public ResThread() {
			super("Thread");
		}

		@Override
		public void run() {
			while (mRunning) {

				try {


					Thread.sleep(delayPeriod);
					String res = "result " + r.nextInt();  //XXX here you can set the message to be displayed to user

					//send request to server
					NetworkConnector.getInstance().sendRequestToServer(NetworkConnector.UNSYNCED_MESSAGES, SysData.getInstance().getUser(), new NetworkResListener() {
							@Override
							public void onPreUpdate(String str) {

							}

							@Override
							public void onPostUpdate(JSONObject res, String table, ResStatus status) {
								if(status == ResStatus.SUCCESS){

									// when getting results from server deliver the results
									resultsList.add(res);
									CyclerManager.getInstance().deliverResults();

								}
							}

							@Override
							public void onPostUpdate(Bitmap res, ResStatus status) {

							}
						});

				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
			mStarted = false;
		}

		public boolean isRunning() {
			return mRunning;
		}

		public void setRunning(boolean mRunning) {
			this.mRunning = mRunning;
		}

		@Override
		public synchronized void start() {
			mStarted = true;
			mRunning = true;
			super.start();
		}

		public boolean getIsStarted() {
			return mStarted;
		}

		public void stopThread() {
			state = Stop;
			mRunning = false;
		}

	}

}
