package com.example.iword;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment_remember extends Fragment {

	private Button start_remember;
	private TextView all_word;
	private TextView day_word;
	
	String num_day_word;
	String num_all_word;
	String no;
	String name;
	String password;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_remember_layout, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		start_remember = (Button) getActivity().findViewById(
				R.id.start_remember);
		all_word =(TextView) getActivity().findViewById(R.id.all_word);
		day_word =(TextView) getActivity().findViewById(R.id.day_word);
		
		no = getActivity().getIntent().getStringExtra("no");  
		name = getActivity().getIntent().getStringExtra("name");
		password = getActivity().getIntent().getStringExtra("password");
		num_day_word = getActivity().getIntent().getStringExtra("day_word");  
		num_all_word = getActivity().getIntent().getStringExtra("all_word");
		
		Log.d("fragment_remember",num_day_word);
		Log.d("fragment_remember",num_all_word);
		
		all_word.setText(num_all_word+"个");
		day_word.setText(num_day_word+"个");
		
		start_remember.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent 
						(getActivity(), word_remember_start.class);
				
				intent.putExtra("no", no);
				intent.putExtra("name", name);
				intent.putExtra("password", password);
				
				Log.d("fragment_rememnber",""+no+" "+name+" "+password);
				
				startActivity(intent);
				
				iword.instance.finish();  //结束主活动
			}
		});
	}
}
