package com.example.iword;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class aboutiword extends Activity {
	
	private ImageView aboutiword_top_return;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aboutiword_layout);
		
		aboutiword_top_return=(ImageView) findViewById(R.id.aboutiword_top_return);
		aboutiword_top_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}	
}
