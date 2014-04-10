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

	private int ONCLICKUP = 0; // 短按起
	private int ONLONGCLICKDOWN = 0; // 长按下
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
		et.addTextChangedListener(tw);// 为输入框绑定一个监听文字变化的监听器
		// 添加按钮点击事件
		ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ONLONGCLICKDOWN != 1) {
					Log.d(TAG, "-----setOnClickListener--------------->>>");
					inputStr = removeLastStr(inputStr);
					if (inputStr == null) {
						inputStr = "";
						hideBtn();// 隐藏按钮
					}
					et.setText(inputStr);// 设置输入框内容为空
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

	// 当输入框状态改变时，会调用相应的方法
	TextWatcher tw = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		// 在文字改变后调用
		@Override
		public void afterTextChanged(Editable s) {
			inputStr = et.getText().toString();
			if (s.length() == 0) {
				hideBtn();// 隐藏按钮
			} else {
				showBtn();// 显示按钮
			}

			// 设置输入框焦点的位置为最后一个字符后
			Editable etable = et.getText();
			et.requestFocus(); // 请求获取焦点
			// et.clearFocus(); //清除焦点
			// et.setCursorVisible(false); //设置光标不显示,但不能设置光标颜色
			// et.setSelectAllOnFocus(true); // 获得焦点时全选文本
			// 位置不能越界 0 至最后一个字符后
			int offSize = inputStr.length();
			if (offSize < 0) {
				offSize = 0;
			}
			if (offSize > inputStr.length()) {
				offSize = inputStr.length();
			}
			// 意义 PS：当内容过多时，可通过设置光标位置来让该位置的内容显示在屏幕上。
			Selection.setSelection(etable, offSize); // 设置光标的位置
		}
	};

	@Override
	public void hideBtn() {
		// 设置按钮不可见
		if (ib.isShown())
			ib.setVisibility(View.GONE);

	}

	@Override
	public void showBtn() {
		// 设置按钮可见
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
				Bundle b = new Bundle();// 存放数据
				b.putString("color", inputStr);
				b.putBoolean("turn", nullf);
				msg.setData(b);
				myHandler.sendMessage(msg); // 向Handler发送消息,更新UI
			}

		}
	}

	/**
	 * 接受消息,处理消息 ,此Handler会与当前主线程一块运行
	 * */
	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 此处可以更新UI
			Bundle b = msg.getData();
			String color = b.getString("color");
			boolean turn = b.getBoolean("turn");
			if (turn) {
				hideBtn();// 隐藏按钮
			}
			et.setText(color);// 设置输入框内容为空
		}
	}
}

interface EdtInterface {
	public void hideBtn();

	public void showBtn();
}
