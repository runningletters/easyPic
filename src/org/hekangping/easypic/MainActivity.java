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
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private Button objLoginButton = null;

    private static final int REQUEST_CODE = 1;

    private ToastShow objToast = null;

    private String loginSuccessUser = null;

    private String loginSuccessPassword = null;

    /** ��֤�û���½��url */
    private static final String LOGIN_URL = "http://exam.szcomtop.com/mobile/login.ac";

    /** ���尴�����ʱ��Ϊ2��(���ʱ����toast��ʾ��ʱ��)�����2���ڰ���2�η��أ����˳����� */
    private long waitTime = 2000;
    /** ��¼��һ�ΰ��˳�����ʱ�� */
    private long touchTime = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // ����������δ������쳣

        Log.d(TAG, "crashHandler.init(this)");

        // set layout
        setContentView(R.layout.fragment_main);
        Log.d(TAG, "setContentView(R.layout.fragment_main)");

        // �û��������
        EditCancel objEditCancel = (EditCancel) findViewById(R.id.username_text);
        objEditCancel.setValue("hello10");
        // ���������
        EditText objPswText = (EditText) findViewById(R.id.password_text);
        objPswText.setText("hello10");

        objPswText.setOnEditorActionListener(onEditorActionListener);
        objPswText.addTextChangedListener(objTextWatcher);

        objLoginButton = (Button) this.findViewById(R.id.loginBtn);
        objLoginButton.setOnClickListener(new ClickButtonListener());

        // init Toast
        objToast = new ToastShow(MainActivity.this);

        Log.d(TAG, "onCreate() complete!");

    }

    /**
     * ���뷨�е������½ǵ���ɰ�ťʱ���õ�¼����
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
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

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
     * ��¼�ķ���,����url,��÷��������ص�״̬��,����״̬�����ж��Ƿ��¼�ɹ�,����ɹ�,��ת��SecondActivity
     * 
     */
    public void doLogin() {
        Log.d(TAG, "doLogin()");
        // �û��������
        EditCancel objEditCancel = (EditCancel) findViewById(R.id.username_text);
        // ���������
        EditText objPswText = (EditText) findViewById(R.id.password_text);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(objPswText.getWindowToken(), 0); // ǿ�����ؼ���

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

        // objToast.toastShow("�������ӷ�����");
        initProgressDialog();

        String strHttpUrl = LOGIN_URL;
        // String strParam = "?user=" + strName + "&password=" + strPsw;
        // �ȸ�ֵ����½ʧ��ʱ��ո�ֵ
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
            List<NameValuePair> lstPairs = (List<NameValuePair>) lstParams[0].get(1);
            // �ַ�����ʽ�Ĳ���
            String strParam = Arrays.toString(lstPairs.toArray());
            // ȡ��HttpClient����
            HttpClient httpclient = new DefaultHttpClient();
            // HttpPost���Ӷ���
            HttpPost httpPost = new HttpPost(strRequestUrl);
            try {
                // ����HttpClient��ȡ��HttpResponse
                Log.d(TAG, "doLogin()  ��������:" + strRequestUrl);
                // ����������������ñ���Ϊutf-8��ʽ
                httpPost.setEntity(new UrlEncodedFormEntity(lstPairs, HTTP.UTF_8));
                // ����HttpClient��ȡ��HttpResponse
                HttpResponse httpResponse = httpclient.execute(httpPost);
                int iStatusCode = httpResponse.getStatusLine().getStatusCode();
                // ����ɹ�
                if (iStatusCode == HttpStatus.SC_OK) {
                    current = 100;
                    Log.d(TAG, "doLogin()  ����ɹ�:" + strRequestUrl);
                    // ȡ�÷��ص��ַ���
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
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
                    Log.d(TAG, "doLogin()  ����ʧ��:" + strRequestUrl + " StatusCode" + iStatusCode);
                    strRtnMsg = "���ӷ�����ʧ�ܣ�������������" + "(" + iStatusCode + ")";
                }
            } catch (Exception e) {
                Log.e(TAG, "���ӷ�����ʧ�ܣ�������������", e);
                strRtnMsg = "���ӷ�����ʧ�ܣ�������������";
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
                objIntent.putExtra("username", loginSuccessUser);
                objIntent.putExtra("password", loginSuccessPassword);
                startActivityForResult(objIntent, REQUEST_CODE);
                // ��ӽ����л�Ч����ע��ֻ��Android��2.0(SdkVersion�汾��Ϊ5)�Ժ�İ汾��֧��
                int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
                if (version >= 5) {
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    /*
                     * overridePendingTransition(android.R.anim.slide_out_right,
                     * android.R.anim.slide_in_left);
                     */
                }
            } else {
                showToast(result);
                if (result.contains("�û�")) {
                    // �û��������
                    EditCancel objEditCancel = (EditCancel) findViewById(R.id.username_text);
                    // EditText objUserNameText = objEditCancel.getEt();
                    // objUserNameText.setError(result);
                    objEditCancel.focus();
                    // objEditCancel.setEt(objUserNameText);

                }
                if (result.contains("����")) {
                    // ���������
                    EditText objPswText = (EditText) findViewById(R.id.password_text);
                    // objPswText.setError(result);
                    objPswText.requestFocus();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "onProgressUpdate()");
            // ������UI���߳�
            super.onProgressUpdate(values);
            System.out.println(values[0]);
        }
    }

    public void showToast(String showMsg) {
        Log.d(TAG, "showToast()");
        objToast.toastShow(showMsg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        if (requestCode == REQUEST_CODE && resultCode == SecondActivity.RESULT_CODE) {
            Bundle objBundle = data.getExtras();
            EditCancel objEditCancel = (EditCancel) findViewById(R.id.username_text);
            objEditCancel.setValue(objBundle.getString("username"));

            EditText objPswText = (EditText) findViewById(R.id.password_text);
            objPswText.setText(objBundle.getString("password"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown()");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            Log.d(TAG, new Date(currentTime) + "�û����˳���");
            if ((currentTime - touchTime) >= waitTime) {
                objToast.toastShow("�ٰ�һ���˳���");
                touchTime = currentTime;
                return false;
            } else {
                // �˳�
                this.finish();
                Log.d(TAG, new Date(currentTime) + "�û��˳�����");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // ���˾ͱ��� android.util.AndroidRuntimeException: requestFeature() must be
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
