package org.hekangping.easypic;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		// ����������δ������쳣
		CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(this.getApplicationContext());
		Log.d(TAG, "MyApplication.onCreate() ");
	}

}
