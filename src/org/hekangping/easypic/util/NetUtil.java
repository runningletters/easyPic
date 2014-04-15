package org.hekangping.easypic.util;

import org.hekangping.easypic.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetUtil {

	private static final String TAG = NetUtil.class.getName();

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return true 网络可用 ; false 网络不可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		Log.d(TAG, "isNetworkAvailable()");
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 打开设置网络界面
	 */
	public static void setNetworkMethod(final Context context) {
		// 提示对话框
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(context.getText(R.string.network_setting_title).toString())
				.setMessage(context.getText(R.string.network_setting_message).toString())
				.setPositiveButton(context.getText(R.string.action_settings).toString(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = null;
						// 判断手机系统的版本 即API大于10 就是3.0或以上版本
						if (android.os.Build.VERSION.SDK_INT > 10) {
							intent = new Intent(
									android.provider.Settings.ACTION_WIRELESS_SETTINGS);
						} else {
							intent = new Intent();
							ComponentName component = new ComponentName(
									"com.android.settings",
									"com.android.settings.WirelessSettings");
							intent.setComponent(component);
							intent.setAction("android.intent.action.VIEW");
						}
						context.startActivity(intent);
					}
				})
				.setNegativeButton(context.getText(R.string.cancle).toString(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
}
