package com.example.iword;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class Fragment_list extends Fragment {

	public static int SHOW_RESPONSE ;
	String http;
	String status;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle bundle=getArguments();
		status = bundle.getString("status");
		Log.d("status==","status is "+status);
		
		sendRequestWithHttpClient(status);
		
		return inflater
				.inflate(R.layout.fragment_list_layout, container, false);
	}

	private void sendRequestWithHttpClient(String status) {
		
		if(status.equals("listAll")) {
			http = "http://119.29.206.121:8080/sam_word/servlet_order_by_all";
			SHOW_RESPONSE = 1;
		}
		else {
			http = "http://119.29.206.121:8080/sam_word/servlet_order_by_day";
			SHOW_RESPONSE = 2;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HttpClient httpClient = new DefaultHttpClient();
					// 指定访问的服务器地址
					HttpGet httpGet = new HttpGet(http);
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 请求和响应都成功了
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						// 将服务器返回的结果存放到Message中
						Log.d("status==","SHOW_RESPONSE is "+SHOW_RESPONSE);
						message.obj = response.toString();
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
			case 1:				
				Log.d("status==","case 1 is running");
				String response = (String) msg.obj;
				// 在这里进行UI操作，将结果显示到界面上
				Gson gson = new Gson();
				final List<Student> studentList = gson.fromJson(response,
						new TypeToken<List<Student>>() {
						}.getType());
				for (int i = 0; i < studentList.size(); i++) {
					String serial = String.valueOf(i + 1);
					studentList.get(i).setSerialno(serial);
				}
				StudentAdapter adapter = new StudentAdapter(
						Fragment_list.this.getActivity(), R.layout.student_item,
						studentList);
				ListView listView = (ListView) getActivity().findViewById(R.id.list_view);
				listView.setAdapter(adapter);
				break;
			case 2:
				Log.d("status==","case 2 is running");
				String response2 = (String) msg.obj;
				Gson gson2 = new Gson();
				final List<Student> studentList2 = gson2.fromJson(response2,
						new TypeToken<List<Student>>() {
						}.getType());
				for (int i = 0; i < studentList2.size(); i++) {
					String serial = String.valueOf(i + 1);
					studentList2.get(i).setSerialno(serial);
				}
				StudentAdapter2 adapter2 = new StudentAdapter2(
						Fragment_list.this.getActivity(), R.layout.student_item,
						studentList2);
				ListView listView2 = (ListView) getActivity().findViewById(R.id.list_view);
				listView2.setAdapter(adapter2);
				break;
			}
		}

	};
}
