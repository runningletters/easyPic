package org.hekangping.easypic;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EditCancel extends LinearLayout implements EdtInterface {

	private EditText et;
	private ImageButton ib;
	private String inputStr = null;
	private final String TAG = "EditCancel";

	private int ONCLICKUP = 0; // �̰���
	private int ONLONGCLICKDOWN = 0; // ������
	private boolean flag = false;
	private MyHandler myHandler;

	public EditCancel(Context context) {
		super(context);
	}

	public EditCancel(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context)
				.inflate(R.layout.edittextclear, this, true);
		init();
	}

	public EditText getEt() {
		return et;
	}

	public void setEt(EditText et) {
		this.et = et;
	}

	public void setValue(CharSequence text) {
		this.et.setText(text);
	}

	public void setError(CharSequence error) {
		this.et.setError(error);
	}

	public boolean focus() {
		return this.et.requestFocus();
	}

	public CharSequence getValue() {
		return this.et.getText();
	}

	private void init() {

		myHandler = new MyHandler();

		ib = (ImageButton) findViewById(R.id.ib);
		et = (EditText) findViewById(R.id.et);
		et.addTextChangedListener(tw);// Ϊ������һ���������ֱ仯�ļ�����
		// ��Ӱ�ť����¼�
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ONLONGCLICKDOWN != 1) {
					Log.d(TAG, "-----setOnClickListener--------------->>>");
					inputStr = removeLastStr(inputStr);
					if (inputStr == null) {
						inputStr = "";
						hideBtn();// ���ذ�ť
					}
					et.setText(inputStr);// �������������Ϊ��
				}
				ONLONGCLICKDOWN = 0;
				flag = true;
				//
			}
		});
		ib.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ONLONGCLICKDOWN = 1;
				Log.d(TAG, "-----setOnLongClickListener--------------->>>");
				flag = false;
				MyThread m = new MyThread();
				new Thread(m).start();
				return false;
			}
		});

	}

	// �������״̬�ı�ʱ���������Ӧ�ķ���
	TextWatcher tw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		// �����ָı�����
		@Override
		public void afterTextChanged(Editable s) {
			inputStr = et.getText().toString();
			if (s.length() == 0) {
				hideBtn();// ���ذ�ť
			} else {
				showBtn();// ��ʾ��ť
			}

			// ��������򽹵��λ��Ϊ���һ���ַ���
			Editable etable = et.getText();
			et.requestFocus(); // �����ȡ����
			// et.clearFocus(); //�������
			// et.setCursorVisible(false); //���ù�겻��ʾ,���������ù����ɫ
			// et.setSelectAllOnFocus(true); // ��ý���ʱȫѡ�ı�
			// λ�ò���Խ�� 0 �����һ���ַ���
			int offSize = inputStr.length();
			if (offSize < 0) {
				offSize = 0;
			}
			if (offSize > inputStr.length()) {
				offSize = inputStr.length();
			}
			// ���� PS�������ݹ���ʱ����ͨ�����ù��λ�����ø�λ�õ�������ʾ����Ļ�ϡ�
			Selection.setSelection(etable, offSize); // ���ù���λ��
		}
	};

	@Override
	public void hideBtn() {
		// ���ð�ť���ɼ�
		if (ib.isShown())
			ib.setVisibility(View.GONE);

	}

	@Override
	public void showBtn() {
		// ���ð�ť�ɼ�
		if (!ib.isShown()) {
			ib.setVisibility(View.VISIBLE);
		}

	}

	private String removeLastStr(String src) {
		if (src == null) {
			return null;
		}
		if (src.length() == 0) {
			return null;
		}
		src = "";
		return src;
	}

	class MyThread implements Runnable {
		boolean nullf = false;

		public void run() {
			while (true) {

				if (nullf) {
					break;
				}
				if (flag) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				inputStr = removeLastStr(inputStr);
				if (inputStr == null) {
					inputStr = "";
					nullf = true;
				}
				Message msg = new Message();
				Bundle b = new Bundle();// �������
				b.putString("color", inputStr);
				b.putBoolean("turn", nullf);
				msg.setData(b);
				myHandler.sendMessage(msg); // ��Handler������Ϣ,����UI
			}

		}
	}

	/**
	 * ������Ϣ,������Ϣ ,��Handler���뵱ǰ���߳�һ������
	 * */
	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// ���������д�˷���,��������
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// �˴����Ը���UI
			Bundle b = msg.getData();
			String color = b.getString("color");
			boolean turn = b.getBoolean("turn");
			if (turn) {
				hideBtn();// ���ذ�ť
			}
			et.setText(color);// �������������Ϊ��
		}
	}
}

interface EdtInterface {
	public void hideBtn();

	public void showBtn();
}
