package org.hekangping.easypic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.hekangping.easypic.util.NetUtil;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends ActionBarActivity {

	// TODO: 全局异常处理 MyApplication

	private static final String TAG = "MainActivity";

	private Button objLoginButton = null;

	private static final int REQUEST_CODE = 1;

	private ToastShow objToast = null;

	private String loginSuccessUser = null;

	private String loginSuccessPassword = null;

	/** 验证用户登陆的url */
	private static final String LOGIN_URL = "http://exam.szcomtop.com/mobile/login.ac";


	/** 定义按键间隔时间为2秒(这个时间是toast提示的时间)，如果2秒内按了2次返回，则退出程序 */
	private long waitTime = 2000;
	/** 记录上一次按退出键的时间 */
	private long touchTime = 0;

	private ProgressDialog progressDialog;

	/** 存储默认设置 */
	private SharedPreferences prefs;

	/** 首选项中设置的默认用户名 */
	private String prefsName;

	/** 首选项中设置的密码 */
	private String prefsPsw;

	/** 用户名输入框 */
	EditCancel objEditCancel;

	/** 密码输入框 */
	EditText objPswText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()");

		// set layout
		setContentView(R.layout.fragment_main);
		Log.d(TAG, "setContentView(R.layout.fragment_main)");

		// 用户名输入框
		objEditCancel = (EditCancel) findViewById(R.id.username_text);

		// 密码输入框
		objPswText = (EditText) findViewById(R.id.password_text);

		// 首选项
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				// 首选项中设置的默认用户名
				prefsName = prefs.getString("defaultUserName", "");
				// 首选项中设置的密码
				prefsPsw = prefs.getString("defaultUserPassword", "");
				// 将设置的值更新到登录界面
				// TODO : bug 没有更新登录界面
				setUserAndPsw(prefsName, prefsPsw);
			}

		});

		// 首选项中设置的默认用户名
		prefsName = prefs.getString("defaultUserName", "");
		// 首选项中设置的密码
		prefsPsw = prefs.getString("defaultUserPassword", "");

		objEditCancel.setValue(prefsName);
		objPswText.setText(prefsPsw);

		objPswText.setOnEditorActionListener(onEditorActionListener);
		objPswText.addTextChangedListener(objTextWatcher);

		objLoginButton = (Button) this.findViewById(R.id.loginBtn);
		objLoginButton.setOnClickListener(new ClickButtonListener());

		// init Toast
		objToast = new ToastShow(MainActivity.this);

		Log.d(TAG, "onCreate() complete!");

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * 输入法中点了右下角的完成按钮时调用登录方法
	 * 
	 */
	private OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == R.id.login || actionId == EditorInfo.IME_NULL
					|| actionId == EditorInfo.IME_ACTION_DONE) {
				doLogin();
				return true;
			}
			return false;
		}
	};

	private TextWatcher objTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			EditText objPswText = (EditText) findViewById(R.id.password_text);
			objPswText.setError(null);
		}

	};

	class ClickButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginBtn:
				doLogin();
			default:
				break;
			}

		}
	}

	/**
	 * 登录的方法,请求url,获得服务器返回的状态码,根据状态码来判断是否登录成功,如果成功,跳转到SecondActivity
	 * 
	 */
	public void doLogin() {
		Log.d(TAG, "doLogin()");
		// 用户名输入框
		objEditCancel = (EditCancel) findViewById(R.id.username_text);
		// 密码输入框
		objPswText = (EditText) findViewById(R.id.password_text);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(objPswText.getWindowToken(), 0); // 强制隐藏键盘

		String strName = objEditCancel.getValue().toString().trim();
		String strPsw = objPswText.getText().toString().trim();

		if (strName.length() < 1) {
			// objToast.toastShow(getText(R.string.username_null).toString());
			objEditCancel.setValue("");
			objEditCancel.setError(getText(R.string.username_null).toString());
			return;
		}
		if (strPsw.length() < 1) {
			// objToast.toastShow(getText(R.string.password_null).toString());
			objPswText.setError(getText(R.string.password_null).toString());
			return;
		}

		// 检查网络
		boolean bNetWork = NetUtil.isNetworkAvailable(MainActivity.this);
		if (!bNetWork) {
			NetUtil.setNetworkMethod(MainActivity.this);
			/*
			 * String strNetMsg = getText(R.string.network_disabled).toString();
			 * Log.i(TAG, strNetMsg); showToast(strNetMsg);
			 */
			return;
		}

		// objToast.toastShow("正在连接服务器");
		initProgressDialog();

		String strHttpUrl = LOGIN_URL;
		// String strParam = "?user=" + strName + "&password=" + strPsw;
		// 先赋值，登陆失败时清空该值
		loginSuccessUser = strName;
		loginSuccessPassword = strPsw;

		List<NameValuePair> lstParams = new ArrayList<NameValuePair>(2);
		lstParams.add(new BasicNameValuePair("user", strName));
		lstParams.add(new BasicNameValuePair("password", strPsw));

		List<Object> lstObject = new ArrayList<Object>(2);
		lstObject.add(strHttpUrl);
		lstObject.add(lstParams);

		new RetreiveFeedTask().execute(lstObject);

	}

	class RetreiveFeedTask extends AsyncTask<List<Object>, Integer, String> {

		int current = 0;

		@Override
		protected String doInBackground(List<Object>... lstParams) {
			Log.d(TAG, "doInBackground()");
			// publishProgress(current);
			String strRtnMsg = "";
			String strRequestUrl = String.valueOf(lstParams[0].get(0));
			List<NameValuePair> lstPairs = (List<NameValuePair>) lstParams[0]
					.get(1);
			// 字符串形式的参数
			String strParam = Arrays.toString(lstPairs.toArray());
			// 取得HttpClient对象
			HttpClient httpclient = new DefaultHttpClient();
			// HttpPost连接对象
			HttpPost httpPost = new HttpPost(strRequestUrl);
			// 网络异常时的提示信息
			String strConnectFailed = getText(R.string.network_connect_failed)
					.toString();
			try {

				// 设置请求参数并设置编码为utf-8格式
				httpPost.setEntity(new UrlEncodedFormEntity(lstPairs,
						HTTP.UTF_8));
				// 请求HttpClient，取得HttpResponse
				HttpResponse httpResponse = httpclient.execute(httpPost);
				// 请求HttpClient，取得HttpResponse
				Log.d(TAG, "doLogin()  发出请求:" + strRequestUrl + "?" + strParam);
				int iStatusCode = httpResponse.getStatusLine().getStatusCode();
				// 请求成功
				if (iStatusCode == HttpStatus.SC_OK) {
					current = 100;
					Log.d(TAG, "doLogin()  请求成功:" + strRequestUrl + "?"
							+ strParam);
					// 取得返回的字符串
					String strResult = EntityUtils.toString(httpResponse
							.getEntity());
					JSONObject jsonObject = new JSONObject(strResult);
					strRtnMsg = jsonObject.getString("reason");
					if ("OK".equals(jsonObject.getString("result"))) {
						strRtnMsg = "OK";
					} else {
						loginSuccessUser = null;
						loginSuccessPassword = null;
					}
					progressDialog.setProgress(current);

				} else {
					Log.d(TAG, "doLogin()   " + strRequestUrl + " StatusCode"
							+ iStatusCode);
					strRtnMsg = strConnectFailed + "(" + iStatusCode + ")";
				}
			} catch (Exception e) {
				Log.e(TAG, strConnectFailed, e);
				strRtnMsg = strConnectFailed;
			}
			return strRtnMsg;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "onPostExecute()");
			progressDialog.dismiss();
			if ("OK".equals(result)) {
				Intent objIntent = new Intent();
				objIntent.setClass(MainActivity.this, SecondActivity.class);
				MyApplication application = (MyApplication) getApplication();
				// 全局保存用户名
				application.setUsername(loginSuccessUser);
				objIntent.putExtra("username", loginSuccessUser);
				// 全局保存密码
				application.setPassword(loginSuccessUser);
				objIntent.putExtra("password", loginSuccessPassword);
				startActivityForResult(objIntent, REQUEST_CODE);
				// 添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持
				int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
				if (version >= 5) {
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					/*
					 * overridePendingTransition(android.R.anim.slide_out_right,
					 * android.R.anim.slide_in_left);
					 */
				}
			} else {
				showToast(result);
				if (result.contains("用户")) {
					// 用户名输入框
					objEditCancel = (EditCancel) findViewById(R.id.username_text);
					// EditText objUserNameText = objEditCancel.getEt();
					// objUserNameText.setError(result);
					objEditCancel.focus();
					// objEditCancel.setEt(objUserNameText);

				}
				if (result.contains("密码")) {
					// 密码输入框
					objPswText = (EditText) findViewById(R.id.password_text);
					// objPswText.setError(result);
					objPswText.requestFocus();
				}
			}
		}

	}

	public void showToast(String showMsg) {
		Log.d(TAG, "showToast()");
		objToast.toastShow(showMsg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult()");
		if (requestCode == REQUEST_CODE
				&& resultCode == SecondActivity.RESULT_CODE) {
			Bundle objBundle = data.getExtras();
			objEditCancel = (EditCancel) findViewById(R.id.username_text);
			objEditCancel.setValue(objBundle.getString("username"));

			objPswText = (EditText) findViewById(R.id.password_text);
			objPswText.setText(objBundle.getString("password"));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "onKeyDown()");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			Log.d(TAG, new Date(currentTime) + "用户按退出键");
			if ((currentTime - touchTime) >= waitTime) {
				objToast.toastShow("再按一次退出！");
				touchTime = currentTime;
				return false;
			} else {
				// 退出
				this.finish();
				Log.d(TAG, new Date(currentTime) + "用户退出程序");
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(MainActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 加了就报错 android.util.AndroidRuntimeException: requestFeature() must be
		// called before adding content
		// progressDialog.setContentView(R.layout.customprogressdialog);
		progressDialog.setMax(100);
		progressDialog.setMessage(getText(R.string.loading_text));
		progressDialog.setProgress(0);
		progressDialog.setIndeterminate(false);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent objIntent = new Intent();
			objIntent.setClass(MainActivity.this, PrefsActivity.class);
			startActivity(objIntent);
			return true;
		} else if (id == R.id.quit_menu) {
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 设置首选项后更新登录界面
	 * 
	 * @param user
	 *            默认用户名
	 * @param psw
	 *            密码
	 */
	public void setUserAndPsw(String user, String psw) {
		this.objEditCancel.setValue(user);
		this.objPswText.setText(psw);
	}
}
