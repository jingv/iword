package com.example.iword;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.translate.demo.TransApi;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class word_remember_start extends Activity implements OnClickListener {
	
	public static final int NEW_GROUP = 0;
	public static final int TRANSLATE = 1;
	public static final int LEARN_WORDS = 2;
	public static final int DATA_CHECK = 3;
	private static final String APP_ID = "20170421000045378";
	private static final String SECURITY_KEY = "uiYGSL8cIPjqWB7LZYC_";

	private ImageView word_remember_top_return;
	
	private long mExitTime; // �������ؼ��ʱ��

	private TextView sendRequest;
	private TextView next;

	private MediaPlayer mediaPlayer = new MediaPlayer();
	private Button play;
	private Button translateYN;

	private TextView fanyi;
	private TextView word;
	private TextView tword_progress;

	int code = 0;
	String message;
	String sum;
	String cur_day;

	private String no;
	private String name;
	private String password;
	private ProgressBar word_progress;

	private boolean OpenOrClose = false;
	private boolean flag_loop = false;
	int i = 0;
	String[] woooo = new String[10];
	String[] hoooo = new String[10];

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ���������UI�������������ʾ��������
			case NEW_GROUP:
				i = 0;
				tword_progress.setText(1 + "/10");
				word_progress.setProgress(1);
				word.setText(woooo[i]);
				translate();
				flag_loop = false;
				break;
			case TRANSLATE:
				String res = (String) msg.obj;
				fanyi.setText(res);
				break;
			case LEARN_WORDS:
				Toast.makeText(word_remember_start.this, "��ϲ����ѧ10������",
						Toast.LENGTH_SHORT).show();
				break;
			case DATA_CHECK:
				String response = (String) msg.obj;
				parseJSONWithJSONObject(response);
				if (code == 1) {
					Intent intent = new Intent(word_remember_start.this,
							iword.class);

					intent.putExtra("no", no); // ѧ��
					intent.putExtra("name", name); // ����
					intent.putExtra("password", password); // ����
					intent.putExtra("all_word", sum); // ����
					intent.putExtra("day_word", cur_day); // ����

					startActivity(intent);
					finish();
				} else {
					Toast.makeText(word_remember_start.this, "������������",
							Toast.LENGTH_SHORT).show();
				}
				break;

			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.word_remember_start_layout);

		Intent intent = getIntent();
		no = intent.getStringExtra("no");
		name = intent.getStringExtra("name");
		password = intent.getStringExtra("password");

		Log.d("word_remember_start", "word_remember_start is running");
		Log.d("word_remember_start", no + " no");
		Log.d("word_remember_start", name + " name");
		Log.d("word_remember_start", password + " password");

		sendRequest = (TextView) findViewById(R.id.next_group);
		play = (Button) findViewById(R.id.sound);
		next = (TextView) findViewById(R.id.next_one);
		fanyi = (TextView) findViewById(R.id.translate);
		tword_progress = (TextView) findViewById(R.id.tword_progress);
		word_remember_top_return = (ImageView) findViewById(R.id.word_remember_top_return);
		translateYN = (Button) findViewById(R.id.translateYN);

		word_progress = (ProgressBar) findViewById(R.id.word_progress);

		word = (TextView) findViewById(R.id.word);
		sendRequest.setOnClickListener(this);
		play.setOnClickListener(this);
		next.setOnClickListener(this);
		word_remember_top_return.setOnClickListener(this);
		translateYN.setOnClickListener(this);

		newGroup();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sound:
			initAndPlayMediaPlayer();
			break;
		case R.id.next_group:
			newGroup();
			break;
		case R.id.next_one:
			i++;
			if (i > 9) {
				i = 0;
				word_progress.setProgress(1);
				if (!flag_loop) {
					// �ύ��ѧ�ĵ�����
					HttpClient_learn_word();
					flag_loop = true;
				}
			}
			tword_progress.setText((i + 1) + "/10");
			word.setText(woooo[i]);
			translate();
			Log.d("MainActivity", "no is " + i);

			/************** ������ *****************/
			int i = word_progress.getProgress();
			i = i + 1;
			word_progress.setProgress(i);

			/***************** ��һ�����ʷ���Ĭ�ϲ���ʾ ****************/
			fanyi.setVisibility(View.GONE); // ��������
			OpenOrClose = false;
			translateYN.setBackgroundResource(R.drawable.translate_close);

			break;
		case R.id.word_remember_top_return:
			Data_login();
			break;
		case R.id.translateYN:
			if (OpenOrClose == false) {
				fanyi.setVisibility(View.VISIBLE); // ������ʾ
				OpenOrClose = true;
				translateYN.setBackgroundResource(R.drawable.translate_open);
			} else {
				fanyi.setVisibility(View.GONE); // ��������
				OpenOrClose = false;
				translateYN.setBackgroundResource(R.drawable.translate_close);
			}
		default:
			break;
		}
	}

	private void newGroup() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					// ָ�����ʵķ�������ַ
					HttpGet httpGet = new HttpGet(
							"http://119.29.206.121:8080/sam_word/servlet_get_words?sum=10");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// �������Ӧ���ɹ���
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Log.d("MainActivity", "response is " + response);
						parseJSON(response);
						Message message = new Message();
						message.what = NEW_GROUP;
						// �����������صĽ����ŵ�Message��
						message.obj = response.toString();
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void translate() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String query = woooo[i];
				TransApi api = new TransApi(APP_ID, SECURITY_KEY);
				Message message = new Message();
				message.what = TRANSLATE;
				String tran_query = api.getTransResult(query, "en", "zh");
				Log.d("MainActivity", "tran_query is " + tran_query);
				String de_tran = translate.decodeUnicode(tran_query);
				Log.d("MainActivity", "de_tran is " + de_tran);

				// ����json����
				try {
					JSONObject jsonObj = new JSONObject(de_tran);
					JSONArray jsonArr = (JSONArray) jsonObj.get("trans_result");
					JSONObject jsonObj_trans_result = jsonArr.getJSONObject(0);
					message.obj = jsonObj_trans_result.get("dst");
					Log.d("MainActivity", "dst is " + message.obj);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendMessage(message);

			}

		}).start();
	}

	private void HttpClient_learn_word() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(
							"http://119.29.206.121:8080/sam_word/servlet_learn_num");
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("no", no));
					params.add(new BasicNameValuePair("num", "10"));
					UrlEncodedFormEntity entity_request = new UrlEncodedFormEntity(
							params, "UTF-8");
					httpPost.setEntity(entity_request);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// �������Ӧ���ɹ���
						Message message = new Message();
						message.what = LEARN_WORDS;
						handler.sendMessage(message);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void parseJSON(String jsonData) {

		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				woooo[i] = jsonObj.getString("word");
				hoooo[i] = jsonObj.getString("voice");
				Log.d("MainActivity", "i is " + i);
				Log.d("MainActivity", "word is " + woooo[i]);
				Log.d("MainActivity", "voice is " + hoooo[i]);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initAndPlayMediaPlayer() {
		try {

			Log.d("MainActivity", "voice is " + hoooo[i]);
			String url = hoooo[i];
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);

			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
	}

	private void Data_login() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					String url = "http://119.29.206.121:8080/sam_word/servlet_login";
					HttpPost httpPost = new HttpPost(url);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("no", no));
					params.add(new BasicNameValuePair("password", password));
					UrlEncodedFormEntity entity_request = new UrlEncodedFormEntity(
							params, "UTF-8");
					httpPost.setEntity(entity_request);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// �������Ӧ���ɹ���
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Log.d("my", "response is " + response);
						Message message = new Message();
						message.what = DATA_CHECK;
						message.obj = response.toString();
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

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
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else 
//			Data_login();
			finish();			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
