package org.hekangping.easypic.service;

import org.hekangping.easypic.R;
import org.hekangping.easypic.SecondActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

public class FetchDataService extends IntentService {

	private static final String TAG = "FetchDataService";

	public static final int FETCH_DATA = 0;

	public FetchDataService() {
		super("FetchDataService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "");
		startFetchData();
	}

	private void startFetchData() {
		Notification objNotification = new Notification(R.drawable.ic_launcher,
				"Fetching data...", System.currentTimeMillis());
		Intent objIntent = new Intent();
		objIntent.setClass(this, SecondActivity.class);
		PendingIntent objPendingIntent = PendingIntent.getActivity(this, 0,
				objIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		objNotification.setLatestEventInfo(this, "Fetching data",
				"All data loaded.", objPendingIntent);
		objNotification.defaults = Notification.DEFAULT_ALL;
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(FETCH_DATA, objNotification);

	}
}
