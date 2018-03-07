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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends Activity {

	public static final int LOGIN_CHECK = 0;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private EditText userid;
	private EditText userkey;
	private Button login_btn;
	private CheckBox rememberPass;
	private TextView registered_btn;

	String account;
	String password;

	int code = 0;
	String name;
	String message;
	String sum;
	String cur_day;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		userid = (EditText) findViewById(R.id.userid);
		userkey = (EditText) findViewById(R.id.userkey);
		login_btn = (Button) findViewById(R.id.login_btn);
		rememberPass = (CheckBox) findViewById(R.id.remember);
		registered_btn = (TextView) findViewById(R.id.registered_btn);

		boolean isRemember = pref.getBoolean("remember_password", false);
		if (isRemember) {
			account = pref.getString("account", "");
			password = pref.getString("password", "");
			Log.d("My", "account:" + account);
			Log.d("My", "password:" + password);
			userid.setText(account);
			userkey.setText(password);
			userid.requestFocus();
			userid.setSelection(account.length());
			rememberPass.setChecked(true);
		}
		login_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				account = userid.getText().toString();
				password = userkey.getText().toString();
				if (account.equals("") | password.equals("")) {
					if (account.equals("")) {
						Toast.makeText(login.this, "请输入账户", Toast.LENGTH_SHORT)
								.show();
						userid.requestFocus();
					} else {
						Toast.makeText(login.this, "请输入密码", Toast.LENGTH_SHORT)
								.show();
						userkey.requestFocus();
					}
				} else
					HttpClient_login();
			}
		});
		registered_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(login.this, registered.class);
				startActivity(intent);
			}
		});
	}

	private void HttpClient_login() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					String url = "http://119.29.206.121:8080/sam_word/servlet_login";
					HttpPost httpPost = new HttpPost(url);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("no", account));
					params.add(new BasicNameValuePair("password", password));
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
						message.what = LOGIN_CHECK;
						// 将服务器返回的结果存放到Message中
						message.obj = response.toString();
						Log.d("my", "message.obj is " + message.obj);
						handler.sendMessage(message);
					}
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
			case LOGIN_CHECK:
				Log.d("my", "switch start ");
				String response = (String) msg.obj;
				parseJSONWithJSONObject(response);
				if (code == 1) {
					editor = pref.edit();
					if (rememberPass.isChecked()) {
						editor.putBoolean("remember_password", true);
						editor.putString("account", account);
						editor.putString("password", password);
						Log.d("My", "account2:" + account);
						Log.d("My", "password2:" + password);
					} else {
						editor.clear();
					}
					editor.commit();
					Intent intent = new Intent(login.this,
							iword.class);

					intent.putExtra("no", account);        //学号
					intent.putExtra("name", name);         //姓名
					intent.putExtra("password", password); //密码
					intent.putExtra("all_word", sum);      //待定
					intent.putExtra("day_word", cur_day);  //待定

					startActivity(intent);
					finish();
				} else {
					Toast.makeText(login.this, "账户或密码错误", Toast.LENGTH_SHORT)
							.show();

					editor = pref.edit();
					if (rememberPass.isChecked()) {
						editor.putBoolean("remember_password", false);
						editor.clear();
					} else
						editor.clear();
					editor.commit();

					userkey.requestFocus();
					userkey.setSelection(password.length());
					Log.d("password", "" + password.length() + " " + password);
				}
			}
		}
	};

	private void parseJSONWithJSONObject(String jsonData) {
		try {
			JSONObject JSONObject = new JSONObject(jsonData);
			code = JSONObject.getInt("code");
			name = JSONObject.getString("name");
			message = JSONObject.getString("message");
			sum = JSONObject.getString("sum");
			cur_day = JSONObject.getString("cur_day");
			Log.d("MainActivity", "code is " + code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
