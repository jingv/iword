package com.example.iword;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class iword extends Activity implements OnClickListener {
	
	public static iword instance = null;  //������changekey�н����˻
	
	private long mExitTime; // �������ؼ��ʱ��

	private TextView learnWord;
	private TextView listAll;
	private TextView listDay;
	private TextView name_title;
	
	private ImageView splid;

	private Drawable list_clicked;
	private Drawable list_unclicked;
	private Drawable word_clicked;
	private Drawable word_unclicked; // ͼƬ��Դ
	
	Bundle bundle = new Bundle();
	
	Fragment_list fragment_list;
	Fragment_remember fragment_remember;
	FragmentManager fragmentManager;
	FragmentTransaction transaction;
	
	String name;
	String no;
	String password;
	
	public SlidingMenu splidmenu; // �����󻬲˵�

	public TextView splid_username;
	public TextView splid_no;
	public TextView changekey;
	public TextView aboutus;
	public ImageView splid_exit;// �󻬲˵��ؼ�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.iword_layout);
		
		instance = this; //instanceָ��˻
		
		/**************��ȡ�˻���Ϣ****************/
		no = getIntent().getStringExtra("no");  
		name = getIntent().getStringExtra("name");
		password = getIntent().getStringExtra("password");
		Log.d("iwordactivity",no+" "+name+" "+password);

		learnWord = (TextView) findViewById(R.id.learnWord);
		listAll = (TextView) findViewById(R.id.listAll);
		listDay = (TextView) findViewById(R.id.listDay);
		
		name_title = (TextView) findViewById(R.id.name_title);
		name_title.setText(name);
		
		splid = (ImageView) findViewById(R.id.splid);
		splid.setOnClickListener(this);

		learnWord.setOnClickListener(this);
		listAll.setOnClickListener(this);
		listDay.setOnClickListener(this);

		/********************** ��ȡͼƬ��Դ ***************************/
		list_clicked = getResources().getDrawable(R.drawable.list_clicked);
		list_unclicked = getResources().getDrawable(R.drawable.list_unclicked);
		word_clicked = getResources().getDrawable(R.drawable.word_clicked);
		word_unclicked = getResources().getDrawable(R.drawable.word_unclicked);

		/****************** ��Ƭ���� **********************/
		fragment_remember = new Fragment_remember();
		fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.frameLayoutId, fragment_remember);
		transaction.commit();
		
		/************* �����˵������������� ******************/
		splidmenu = new SlidingMenu(this);
		splidmenu.setMode(SlidingMenu.LEFT);
		// ���ô�����Ļ��ģʽ
		splidmenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		splidmenu.setShadowWidthRes(R.dimen.shadow_width);
		splidmenu.setShadowDrawable(R.drawable.shadow);

		// ���û����˵���ͼ�Ŀ��
		splidmenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// ���ý��뽥��Ч����ֵ
		splidmenu.setFadeDegree(0.35f);
		/**
		 * SLIDING_WINDOW will include the Title/ActionBar in the content
		 * section of the SlidingMenu, while SLIDING_CONTENT does not.
		 */
		splidmenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// Ϊ�໬�˵����ò���
		splidmenu.setMenu(R.layout.splidmenu);

		/************* �����˵��Ŀؼ����� ******************/
		splid_username = (TextView) splidmenu.findViewById(R.id.splid_username);
		splid_username.setText(name);
		splid_no = (TextView) splidmenu.findViewById(R.id.splid_no);
		splid_no.setText(no);
		
		splid_exit = (ImageView) splidmenu.findViewById(R.id.splid_exit);
		splid_exit.setOnClickListener(this);
		changekey = (TextView) splidmenu.findViewById(R.id.ChangeKey);
		changekey.setOnClickListener(this);
		aboutus = (TextView) splidmenu.findViewById(R.id.Aboutiword);
		aboutus.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.Aboutiword:				
			Intent aboutiword = new Intent(iword.this,aboutiword.class);
			startActivity(aboutiword);
			break;
		case R.id.ChangeKey:			
			Intent changekey = new Intent(iword.this,changekey.class);
			changekey.putExtra("no", no);  			
			startActivity(changekey);
			break;
		case R.id.splid:
			splidmenu.toggle();
			break;
		case R.id.splid_exit:			
			Intent login = new Intent(iword.this,login.class);			
			startActivity(login);
			finish();
			break;
		case R.id.learnWord:
			
			name_title.setText(name);
			/*****************����ͼƬ��ʽ*********************/
			learnWord.setCompoundDrawablesWithIntrinsicBounds(null,
					word_clicked, null, null);
			listDay.setCompoundDrawablesWithIntrinsicBounds(null,
					list_unclicked, null, null);
			listAll.setCompoundDrawablesWithIntrinsicBounds(null,
					list_unclicked, null, null);

			fragment_remember = new Fragment_remember();
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frameLayoutId, fragment_remember);
			transaction.commit();

			break;
		case R.id.listAll:
			
			name_title.setText("");

			learnWord.setCompoundDrawablesWithIntrinsicBounds(null,
					word_unclicked, null, null);
			listDay.setCompoundDrawablesWithIntrinsicBounds(null,
					list_unclicked, null, null);
			listAll.setCompoundDrawablesWithIntrinsicBounds(null, list_clicked,
					null, null);

			fragment_list = new Fragment_list();
			
			bundle.putString("status","listAll");//states������Ӧ��״̬
	        fragment_list.setArguments(bundle);
			
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frameLayoutId, fragment_list);
			transaction.commit();

			break;
		case R.id.listDay:
			
			name_title.setText("");

			learnWord.setCompoundDrawablesWithIntrinsicBounds(null,
					word_unclicked, null, null);
			listDay.setCompoundDrawablesWithIntrinsicBounds(null, list_clicked,
					null, null);
			listAll.setCompoundDrawablesWithIntrinsicBounds(null,
					list_unclicked, null, null);

			fragment_list = new Fragment_list();
			
			bundle.putString("status","listDay");//states������Ӧ��״̬
	        fragment_list.setArguments(bundle);
			
			fragmentManager = getFragmentManager();
			transaction = fragmentManager.beginTransaction();
			transaction.replace(R.id.frameLayoutId, fragment_list);
			transaction.commit();

			break;
		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (splidmenu.isMenuShowing()) {
				splidmenu.toggle();
			} else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
					mExitTime = System.currentTimeMillis();

				} else {
					finish();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
