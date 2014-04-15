package org.hekangping.easypic;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		// 监听并处理未捕获的异常
		CrashHandler crashHandler = CrashHandler.getInstance();
		//crashHandler.init(this.getApplicationContext());
		Log.d(TAG, "MyApplication.onCreate() ");
	}

}
