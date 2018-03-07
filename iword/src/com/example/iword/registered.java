package com.example.iword;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class registered extends Activity {

	public static final int REGISTERED_CHECK = 0;

	private EditText add_username;
	private EditText add_userid;
	private EditText add_userkey;
	private EditText add_userkeyY;
	private Button registered_btn;

	String username;
	String userid;
	String userkey;
	String userkeyY;

	int code = 0;
	String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registered_layout);

		add_username = (EditText) findViewById(R.id.add_username);
		add_userid = (EditText) findViewById(R.id.add_userid);
		add_userkey = (EditText) findViewById(R.id.add_userkey);
		add_userkeyY = (EditText) findViewById(R.id.add_userkeyY);
		registered_btn = (Button) findViewById(R.id.registered_btn);

		registered_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				username = add_username.getText().toString();
				userid = add_userid.getText().toString();
				userkey = add_userkey.getText().toString();
				userkeyY = add_userkeyY.getText().toString();
				if (username.equals("") | userid.equals("")
						| userkey.equals("") | userkeyY.equals("")) {
					if (userid.equals("")) {
						Toast.makeText(registered.this, "请输入账号",
								Toast.LENGTH_SHORT).show();
						add_userid.requestFocus();
					}

					else if (username.equals("")) {
						Toast.makeText(registered.this, "请输入用户名",
								Toast.LENGTH_SHORT).show();
						add_username.requestFocus();
					} else if (userkey.equals("")) {
						Toast.makeText(registered.this, "请输入密码",
								Toast.LENGTH_SHORT).show();
						add_userkey.requestFocus();
					} else {
						Toast.makeText(registered.this, "请确认密码",
								Toast.LENGTH_SHORT).show();
						add_userkeyY.requestFocus();
					}
				}else if (userid.length() < 2 | userkey.length() < 6 ) {
					
					if (userid.length()<2) {
						Toast.makeText(registered.this, "账号长度太短",
								Toast.LENGTH_SHORT).show();
						add_userid.requestFocus();
						add_userid.setSelection(userid.length());
					}else {
						Toast.makeText(registered.this, "密码长度太短",
								Toast.LENGTH_SHORT).show();
						add_userkey.requestFocus();
						add_userkey.setSelection(userkey.length());
					}
				}else {
					if (userkey.equals(userkeyY))
						HttpClient_registered();
					else {
						Toast.makeText(registered.this, "密码不一致，请重新输入",
								Toast.LENGTH_SHORT).show();
						add_userkeyY.setText("");
						add_userkeyY.requestFocus();
					}
				}
			}
		});
	}

	private void HttpClient_registered() {
		Log.d("my", "sendRequestWithHttpClient is starting");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.d("my", "run is starting");
				try {
					Log.d("my", "1");
					HttpClient httpClient = new DefaultHttpClient();
					Log.d("my", "2");
					// 指定访问的服务器地址
					String url = "http://119.29.206.121:8080/sam_word/servlet_register";
					HttpPost httpPost = new HttpPost(url);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("no", userid));
					params.add(new BasicNameValuePair("name", username));
					params.add(new BasicNameValuePair("password", userkey));
					UrlEncodedFormEntity entity_request = new UrlEncodedFormEntity(
							params, "UTF-8");
					httpPost.setEntity(entity_request);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					Log.d("my", "3");
					Log.d("my", "StatusCode is "
							+ httpResponse.getStatusLine().getStatusCode());
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Log.d("my", "response is " + response);
						Message message = new Message();
						message.what = REGISTERED_CHECK;
						// 将服务器返回的结果存放到Message中
						message.obj = response.toString();
						Log.d("my", "message.obj is " + message.obj);
						handler.sendMessage(message);
					} else
						Toast.makeText(registered.this, "请检查网络设置",
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.d("my", "error");
					e.printStackTrace();
					Log.d("my", e.toString());
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			Log.d("my", "handleMessage start ");
			switch (msg.what) {
			case REGISTERED_CHECK:
				Log.d("my", "switch start ");
				String response = (String) msg.obj;
				parseJSONWithJSONObject(response);
				if (code == 1) {
					Toast.makeText(registered.this, "注册成功", Toast.LENGTH_SHORT)
							.show();
					finish();
				} else {
					Toast.makeText(registered.this, "该账户已被占用，请更换账号后重试",
							Toast.LENGTH_SHORT).show();
					add_userid.requestFocus();
					add_userid.setSelection(userid.length());
				}

			}
		}

	};

	private void parseJSONWithJSONObject(String jsonData) {
		try {
			JSONObject JSONObject = new JSONObject(jsonData);
			code = JSONObject.getInt("code");
			message = JSONObject.getString("message");
			Log.d("Registered", "code is " + code);
			Log.d("Registered", "message is " + code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
