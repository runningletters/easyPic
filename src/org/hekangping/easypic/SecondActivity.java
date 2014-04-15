package org.hekangping.easypic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SecondActivity extends ListActivity {
	// TODO: http://www.eoeandroid.com/thread-52996-1-1.html
	// TODO:

	private static final String TAG = "SecondActivity";

	private Button objBackButton = null;

	private Button objQuitButton = null;

	private ToastShow objToast = null;

	public static final int RESULT_CODE = 2;

	private static String strUserName = "";

	private String strPsw = "";

	private List<HashMap<String, Object>> listData = null;

	private MyAdapter adapter = null;

	/** 查询数据的url */
	private static final String QUERY_URL = "http://exam.szcomtop.com/mobile/list.ac";

	/** 查询数据支持分页的url */
	private static final String QUERY_PAGE_URL = "http://exam.szcomtop.com/mobile/list2.ac";

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		setContentView(R.layout.fragment_main2);

		objToast = new ToastShow(SecondActivity.this);

		Intent objIntent = getIntent();
		Bundle objBundle = objIntent.getExtras();
		strUserName = objBundle.getString("username");
		strPsw = objBundle.getString("password");
		// String strResult = objBundle.getString("result");
		// objToast.toastShow("用户" + strUserName + "登陆成功!");

		// 显示用户名
		TextView objUserNameText = (TextView) findViewById(R.id.secondtext);
		objUserNameText.setText(strUserName);

		listData = new ArrayList<HashMap<String, Object>>(10);
		// 测试行列值溢出的数据
		/*
		 * HashMap<String, Object> objTestData = new HashMap<String, Object>();
		 * objTestData .put("id",
		 * "1234567980132456789013456789012345678shMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<Strin90132456789"
		 * ); objTestData .put("text",
		 * "abcdefg23145654655445744132shMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<Strin121254jijklmnopqrstuvwxyz"
		 * ); listData.add(objTestData);
		 */

		// / 返回按钮绑定事件
		objBackButton = (Button) this.findViewById(R.id.backButton);
		objBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// objToast.toastShow("返回首页");
				Intent objIntent = new Intent();
				objIntent.setClass(SecondActivity.this, MainActivity.class);
				objIntent.putExtra("username", strUserName);
				objIntent.putExtra("password", strPsw);
				setResult(SecondActivity.RESULT_CODE, objIntent);
				finish();
				// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
				int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
				if (version >= 5) {
					overridePendingTransition(R.anim.in_from_left,
							R.anim.out_to_right);
				}
			}

		});

		// / 注销按钮绑定事件
		objQuitButton = (Button) this.findViewById(R.id.quitButton);
		objQuitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SecondActivity.this)
						.setTitle(R.string.quitBtn)
						.setMessage(R.string.logouttip)
						.setPositiveButton(R.string.ensure,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Intent objIntent = new Intent();
										objIntent.setClass(SecondActivity.this,
												MainActivity.class);
										// 注销
										objIntent.putExtra("username", "");
										objIntent.putExtra("password", "");
										setResult(RESULT_CODE, objIntent);
										finish();
										// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
										int version = Integer
												.valueOf(android.os.Build.VERSION.SDK_INT);
										if (version >= 5) {
											overridePendingTransition(
													R.anim.in_from_left,
													R.anim.out_to_right);
										}
									}
								})
						.setNegativeButton(R.string.cancle,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();

			}

		});

		// 查询数据
		this.queryList();
	}

	private void queryList() {
		Log.d(TAG, "queryList()");
		String strHttpUrl = QUERY_URL;
		List<NameValuePair> lstParams = new ArrayList<NameValuePair>(2);
		lstParams.add(new BasicNameValuePair("user", strUserName));
		lstParams.add(new BasicNameValuePair("query", "list"));

		List<Object> lstObject = new ArrayList<Object>(2);
		lstObject.add(strHttpUrl);
		lstObject.add(lstParams);
		initProgressDialog();
		// 异步查询数据
		new FetchDataTask().execute(lstObject);

	}

	class FetchDataTask extends AsyncTask<List<Object>, Integer, String> {

		private Exception exception;

		@Override
		protected String doInBackground(List<Object>... lstParams) {
			Log.d(TAG, "doInBackground()");
			String strRequestUrl = String.valueOf(lstParams[0].get(0));
			List<NameValuePair> lstPairs = (List<NameValuePair>) lstParams[0]
					.get(1);
			// 字符串形式的参数
			String strParam = Arrays.toString(lstPairs.toArray());
			// 取得HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost连接对象
			HttpPost httpPost = new HttpPost(strRequestUrl);
			try {
				// 设置请求参数并设置编码为utf-8格式
				httpPost.setEntity(new UrlEncodedFormEntity(lstPairs,
						HTTP.UTF_8));
				Log.d(TAG, "queryList()  发出请求:" + strRequestUrl);
				// 请求HttpClient，取得HttpResponse
				HttpResponse httpResponse = httpclient.execute(httpPost);
				// 请求成功
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.d(TAG, "queryList()  请求成功:" + strRequestUrl + "参数:"
							+ strParam);
					// 取得返回的字符串
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					if (strResult.length() == 0) {
						Log.d(TAG, "queryList()  请求失败:" + strRequestUrl + "参数:"
								+ strParam);
						objToast.toastShow("无数据");
						return "无数据";
					}
					JSONObject jsonObject = new JSONObject(strResult);
					String strRtnMsg = jsonObject.getString("data");
					if (strRtnMsg != null && strRtnMsg.length() > 1) {
						// 解析json数组
						JSONArray arr = new JSONArray(strRtnMsg);
						int iLength = arr.length();
						progressDialog.setMax(iLength);
						for (int i = 0; i < iLength; i++) {
							HashMap<String, Object> objItem = new HashMap<String, Object>();
							JSONObject objDataItem = (JSONObject) arr.get(i);
							String strId = objDataItem.getString("id");
							String strText = objDataItem.getString("text");
							objItem.put("id", strId);
							objItem.put("text", strText);
							listData.add(objItem);
							progressDialog.setProgress(i);
						}
					} else {
						objToast.toastShow(strRtnMsg);
					}

				} else {
					Log.d(TAG, "doLogin()  请求失败:" + strRequestUrl + "参数:"
							+ strParam);
					objToast.toastShow("连接服务器失败，请检查网络连接");
				}

			} catch (Exception e) {
				Log.e(TAG, "连接服务器失败，请检查网络连接", e);
			}
			return strParam;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute()");
			progressDialog.dismiss();
			ListView listView = getListView();

			// 自定义的适配器
			adapter = new MyAdapter(SecondActivity.this, listData,
					R.layout.item);
			// 实现列表的显示
			listView.setAdapter(adapter);
			// 条目点击事件
			listView.setOnItemClickListener(new ItemClickListener());
			// 播放系统提示音
			playSound();
			objToast.toastShow("共有" + listData.size() + "条数据");

		}
	}

	// 获取点击事件
	private final class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG, "onItemClick()");
			ListView listView = (ListView) parent;
			HashMap<String, Object> data = (HashMap<String, Object>) listView
					.getItemAtPosition(position);

			adapter.setSelectItem(position);
			adapter.notifyDataSetInvalidated();

			// 单击条目显示对话框
			dialog(data);
		}
	}

	protected void dialog(HashMap<String, Object> data) {
		Log.d(TAG, "dialog()");
		AlertDialog.Builder builder = new Builder(SecondActivity.this);
		builder.setTitle("详情");
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.detaildialog, null);

		TextView objNameTitle = (TextView) layout.findViewById(R.id.tvname);
		String strId = data.get("id").toString();
		objNameTitle.setText(strId);

		TextView objDetailTitle = (TextView) layout.findViewById(R.id.tvdetail);
		String strText = data.get("text").toString();
		objDetailTitle.setText(strText);

		builder.setView(layout).setPositiveButton("确定", null).show();
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(SecondActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 加了就报错 android.util.AndroidRuntimeException: requestFeature() must be
		// called before adding content
		// progressDialog.setContentView(R.layout.customprogressdialog);
		progressDialog.setMax(100);
		progressDialog.setMessage(getText(R.string.loading_data));
		progressDialog.setProgress(0);
		// 设置为true表示不确定的，进度一出现就显示动态进度；false表示精确的进度，一开始进度是0
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	private void playSound() {

		/*
		 * SoundPool sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		 * HashMap<Integer, Integer> spMap = new HashMap<Integer, Integer>();
		 * spMap.put(1, sp.load(this, R.r, 1)); AudioManager am = (AudioManager)
		 * this.getSystemService(Context.AUDIO_SERVICE); float audioMaxVolumn =
		 * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); float volumnCurrent
		 * = am.getStreamVolume(AudioManager.STREAM_MUSIC); float volumnRatio =
		 * volumnCurrent / audioMaxVolumn; sp.play(spMap.get(1), volumnRatio,
		 * volumnRatio, 1, 1, 1f);
		 */

		/*
		 * MediaPlayer mMediaPlayer = MediaPlayer .create(SecondActivity.this,
		 * RingtoneManager .getActualDefaultRingtoneUri(SecondActivity.this,
		 * RingtoneManager.TYPE_NOTIFICATION)); mMediaPlayer.setLooping(false);
		 * mMediaPlayer.start();
		 */

		/*
		 * Uri notification = RingtoneManager
		 * .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); Ringtone r =
		 * RingtoneManager.getRingtone(getApplicationContext(), notification);
		 * r.play();
		 */

		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(SecondActivity.this, RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final AudioManager audioManager = (AudioManager) SecondActivity.this
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			player.setLooping(false);
			try {
				player.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			player.start();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG, "onConfigurationChanged()");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			objToast.toastShow("横屏");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			objToast.toastShow("竖屏");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown()");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(SecondActivity.this)
					.setTitle(R.string.backBtn)
					.setMessage(R.string.backtip)
					.setPositiveButton(R.string.ensure,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent objIntent = new Intent();
									objIntent.setClass(SecondActivity.this,
											MainActivity.class);
									objIntent.putExtra("username", strUserName);
									objIntent.putExtra("password", strPsw);
									setResult(SecondActivity.RESULT_CODE,
											objIntent);
									finish();
									// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
									int version = Integer
											.valueOf(android.os.Build.VERSION.SDK_INT);
									if (version >= 5) {
										overridePendingTransition(
												R.anim.in_from_left,
												R.anim.out_to_right);
									}
								}
							})
					.setNegativeButton(R.string.cancle,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();

		}
		return super.onKeyDown(keyCode, event);
	}

}
