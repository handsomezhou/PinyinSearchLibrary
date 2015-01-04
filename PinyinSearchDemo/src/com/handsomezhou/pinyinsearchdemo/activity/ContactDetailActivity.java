package com.handsomezhou.pinyinsearchdemo.activity;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;

public class ContactDetailActivity extends Activity {

	private TextView mContactsDetailTv;
	private Contacts mContacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_contacts_detail);
		Bundle bundle = this.getIntent().getExtras();
		if(null!=bundle){
			int position=bundle.getInt(ContactsOperationView.CONTACTS_INDEX);
			mContacts=ContactsOperationView.getContacts(position);
		}
		initData();
		initView();
		initListener();
	}
	
	private void initData(){
		
	}
	
	private void initView(){
		mContactsDetailTv=(TextView)findViewById(R.id.contacts_detail_text_view);
		StringBuffer contactsInfo=new StringBuffer();
		if(null!=mContacts){
			contactsInfo.append(mContacts.getName()+"\n");
			for(String number:mContacts.getPhoneNumberList()){
				contactsInfo.append(number+"\n");
			}
		}
		
		mContactsDetailTv.setText(contactsInfo.toString());
	}
	
	private void initListener(){
		
	}

}
