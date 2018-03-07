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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class changekey extends Activity {
	
	public static final int CHANGE_CHECK = 0;

	private EditText oldkey;
	private EditText newkey;
	private EditText newkey_sure;

	private ImageView changekey_top_return;
	private Button SureToChange_btn;

	String no;
	String OldKey;
	String NewKey;
	String NewKey_Sure;
	
	int code = 0;
	String name;
	String message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.changekey_layout);
		
		no = getIntent().getStringExtra("no");
		Log.d("changekeyactivity",""+no);

		oldkey = (EditText) findViewById(R.id.oldkey);
		newkey = (EditText) findViewById(R.id.newkey);
		newkey_sure = (EditText) findViewById(R.id.newkey_sure);

		changekey_top_return = (ImageView) findViewById(R.id.changekey_top_return);
		SureToChange_btn = (Button) findViewById(R.id.SureToChange_btn);

		changekey_top_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		SureToChange_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				OldKey = oldkey.getText().toString();
				NewKey = newkey.getText().toString();
				NewKey_Sure = newkey_sure.getText().toString();
				if (OldKey.equals("")) {

					Toast.makeText(changekey.this, "请输入旧密码", Toast.LENGTH_SHORT)
							.show();
					oldkey.requestFocus();
				} else if (NewKey.equals("")) {

					Toast.makeText(changekey.this, "请输入新密码", Toast.LENGTH_SHORT)
							.show();
					newkey.requestFocus();
				} else if (NewKey_Sure.equals("")) {

					Toast.makeText(changekey.this, "请再次输入新密码",
							Toast.LENGTH_SHORT).show();
					newkey_sure.requestFocus();
				} else if (OldKey.length() < 6) {

					Toast.makeText(changekey.this, "原密码错误", Toast.LENGTH_SHORT)
							.show();
					oldkey.requestFocus();
				} else if (NewKey.length() < 6) {

					Toast.makeText(changekey.this, "新密码不能小于6位",
							Toast.LENGTH_SHORT).show();
					newkey.requestFocus();
				} else if (!NewKey_Sure.equals(NewKey)) {

					Toast.makeText(changekey.this, "密码不一致", Toast.LENGTH_SHORT)
							.show();
					newkey_sure.requestFocus();
				} else
					HttpClient_changekey();
			}
		});
	}
	
	private void HttpClient_changekey() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					String url = "http://119.29.206.121:8080/sam_word/servlet_change_pw";
					HttpPost httpPost = new HttpPost(url);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("no", no));
					params.add(new BasicNameValuePair("pw_old", OldKey));
					params.add(new BasicNameValuePair("pw_new", NewKey));
					UrlEncodedFormEntity entity_request = new UrlEncodedFormEntity(
							params, "UTF-8");
					httpPost.setEntity(entity_request);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Log.d("my", "response is " + response);
						Message message = new Message();
						message.what = CHANGE_CHECK;
						// 将服务器返回的结果存放到Message中
						message.obj = response.toString();
						Log.d("my", "message.obj is " + message.obj);
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHANGE_CHECK:
				String response = (String) msg.obj;
				parseJSONWithJSONObject(response);
				if (code == 1) {
					Toast.makeText(changekey.this, "修改成功，请重新登陆", Toast.LENGTH_SHORT)
					.show();
					Intent intent = new Intent(changekey.this,
							login.class);
					startActivity(intent);
					finish();
					iword.instance.finish();  //结束主活动
				} else {
					Toast.makeText(changekey.this, "修改失败，请检查原密码是否正确", Toast.LENGTH_SHORT)
							.show();
					oldkey.requestFocus();
					oldkey.setSelection(OldKey.length());
				}
			}
		}
	};
	
	private void parseJSONWithJSONObject(String jsonData) {
		try {
			JSONObject JSONObject = new JSONObject(jsonData);
			code = JSONObject.getInt("code");
			message = JSONObject.getString("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
