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

	/** ��ѯ���ݵ�url */
	private static final String QUERY_URL = "http://exam.szcomtop.com/mobile/list.ac";

	/** ��ѯ����֧�ַ�ҳ��url */
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
		// objToast.toastShow("�û�" + strUserName + "��½�ɹ�!");

		// ��ʾ�û���
		TextView objUserNameText = (TextView) findViewById(R.id.secondtext);
		objUserNameText.setText(strUserName);

		listData = new ArrayList<HashMap<String, Object>>(10);
		// ��������ֵ���������
		/*
		 * HashMap<String, Object> objTestData = new HashMap<String, Object>();
		 * objTestData .put("id",
		 * "1234567980132456789013456789012345678shMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<Strin90132456789"
		 * ); objTestData .put("text",
		 * "abcdefg23145654655445744132shMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<StrinshMap<String, Object> objTestData = new HashMap<Strin121254jijklmnopqrstuvwxyz"
		 * ); listData.add(objTestData);
		 */

		// / ���ذ�ť���¼�
		objBackButton = (Button) this.findViewById(R.id.backButton);
		objBackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// objToast.toastShow("������ҳ");
				Intent objIntent = new Intent();
				objIntent.setClass(SecondActivity.this, MainActivity.class);
				objIntent.putExtra("username", strUserName);
				objIntent.putExtra("password", strPsw);
				setResult(SecondActivity.RESULT_CODE, objIntent);
				finish();
				// ��ӽ����л�Ч����ע��ֻ��Android��2.0(SdkVersion�汾��Ϊ5)�Ժ�İ汾��֧��
				int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
				if (version >= 5) {
					overridePendingTransition(R.anim.in_from_left,
							R.anim.out_to_right);
				}
			}

		});

		// / ע����ť���¼�
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
										// ע��
										objIntent.putExtra("username", "");
										objIntent.putExtra("password", "");
										setResult(RESULT_CODE, objIntent);
										finish();
										// ��ӽ����л�Ч����ע��ֻ��Android��2.0(SdkVersion�汾��Ϊ5)�Ժ�İ汾��֧��
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

		// ��ѯ����
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
		// �첽��ѯ����
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
			// �ַ�����ʽ�Ĳ���
			String strParam = Arrays.toString(lstPairs.toArray());
			// ȡ��HttpClient����
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost���Ӷ���
			HttpPost httpPost = new HttpPost(strRequestUrl);
			try {
				// ����������������ñ���Ϊutf-8��ʽ
				httpPost.setEntity(new UrlEncodedFormEntity(lstPairs,
						HTTP.UTF_8));
				Log.d(TAG, "queryList()  ��������:" + strRequestUrl);
				// ����HttpClient��ȡ��HttpResponse
				HttpResponse httpResponse = httpclient.execute(httpPost);
				// ����ɹ�
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.d(TAG, "queryList()  ����ɹ�:" + strRequestUrl + "����:"
							+ strParam);
					// ȡ�÷��ص��ַ���
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					if (strResult.length() == 0) {
						Log.d(TAG, "queryList()  ����ʧ��:" + strRequestUrl + "����:"
								+ strParam);
						objToast.toastShow("������");
						return "������";
					}
					JSONObject jsonObject = new JSONObject(strResult);
					String strRtnMsg = jsonObject.getString("data");
					if (strRtnMsg != null && strRtnMsg.length() > 1) {
						// ����json����
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
					Log.d(TAG, "doLogin()  ����ʧ��:" + strRequestUrl + "����:"
							+ strParam);
					objToast.toastShow("���ӷ�����ʧ�ܣ�������������");
				}

			} catch (Exception e) {
				Log.e(TAG, "���ӷ�����ʧ�ܣ�������������", e);
			}
			return strParam;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute()");
			progressDialog.dismiss();
			ListView listView = getListView();

			// �Զ����������
			adapter = new MyAdapter(SecondActivity.this, listData,
					R.layout.item);
			// ʵ���б����ʾ
			listView.setAdapter(adapter);
			// ��Ŀ����¼�
			listView.setOnItemClickListener(new ItemClickListener());
			// ����ϵͳ��ʾ��
			playSound();
			objToast.toastShow("����" + listData.size() + "������");

		}
	}

	// ��ȡ����¼�
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

			// ������Ŀ��ʾ�Ի���
			dialog(data);
		}
	}

	protected void dialog(HashMap<String, Object> data) {
		Log.d(TAG, "dialog()");
		AlertDialog.Builder builder = new Builder(SecondActivity.this);
		builder.setTitle("����");
		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.detaildialog, null);

		TextView objNameTitle = (TextView) layout.findViewById(R.id.tvname);
		String strId = data.get("id").toString();
		objNameTitle.setText(strId);

		TextView objDetailTitle = (TextView) layout.findViewById(R.id.tvdetail);
		String strText = data.get("text").toString();
		objDetailTitle.setText(strText);

		builder.setView(layout).setPositiveButton("ȷ��", null).show();
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(SecondActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// ���˾ͱ��� android.util.AndroidRuntimeException: requestFeature() must be
		// called before adding content
		// progressDialog.setContentView(R.layout.customprogressdialog);
		progressDialog.setMax(100);
		progressDialog.setMessage(getText(R.string.loading_data));
		progressDialog.setProgress(0);
		// ����Ϊtrue��ʾ��ȷ���ģ�����һ���־���ʾ��̬���ȣ�false��ʾ��ȷ�Ľ��ȣ�һ��ʼ������0
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
			objToast.toastShow("����");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			objToast.toastShow("����");
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
									// ��ӽ����л�Ч����ע��ֻ��Android��2.0(SdkVersion�汾��Ϊ5)�Ժ�İ汾��֧��
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
