package com.handsomezhou.pinyinsearchdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.handsomezhou.pinyinsearchdemo.R;


/**
 * @description Main activity
 * @author handsomezhou
 * @date 2014.11.09
 */
public class MainActivity extends Activity{
	//private static final String TAG = "MainActivity";
	private Context mContext;
	private Button mT9SearchBtn;
	private Button mQwertySearchBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initView();
		initData();
		initListener();
	}
	
	private void initView(){
		mT9SearchBtn=(Button) findViewById(R.id.t9_search_btn);
		mQwertySearchBtn=(Button) findViewById(R.id.qwerty_search_btn);
	}
	
	private void initData(){
		
	}
	
	private void initListener(){
		mT9SearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startT9Search();
			}
		});
		
		mQwertySearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startQwertySearch();
			}
		});
	}
	
	private void startT9Search(){
		Intent intent=new Intent(mContext, T9SearchActivity.class);
		startActivity(intent);
	}

	private void startQwertySearch(){
		Intent intent=new Intent(mContext, QwertySearchActivity.class);
		startActivity(intent);
	}
}
