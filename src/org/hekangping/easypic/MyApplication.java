package org.hekangping.easypic;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {

	private static final String TAG = "MyApplication";

	/** ȫ�ֱ����û��� */
	private String username;

	/** ȫ�ֱ������� */
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// ����������δ������쳣
		CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(this.getApplicationContext());
		Log.d(TAG, "MyApplication.onCreate() ");
	}

}
