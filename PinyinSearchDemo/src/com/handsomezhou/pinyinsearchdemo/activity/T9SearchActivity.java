package com.handsomezhou.pinyinsearchdemo.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper.OnContactsLoad;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;
import com.handsomezhou.pinyinsearchdemo.view.T9TelephoneDialpadView;
import com.handsomezhou.pinyinsearchdemo.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;
import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinUnit;

public class T9SearchActivity extends Activity implements OnT9TelephoneDialpadView,OnContactsLoad{
	private static final String TAG="T9SearchActivity";

	private Context mContext;
	
	private T9TelephoneDialpadView mT9TelephoneDialpadView;
	private ContactsOperationView mContactsOperationView;
	
	private Button mDialpadOperationBtn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_t9_search);
		mContext=this;
		initView();
		initData();
		initListener();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mT9TelephoneDialpadView.clearT9Input();
		ContactsHelper.getInstance().parseT9InputSearchContacts(null);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//moveTaskToBack(true);
	}
	
	private void initView() {
	
		mT9TelephoneDialpadView = (T9TelephoneDialpadView) findViewById(R.id.t9_telephone_dialpad_layout);
		mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);

		mContactsOperationView = (ContactsOperationView)findViewById(R.id.contacts_operation_layout);
		
		mDialpadOperationBtn = (Button) findViewById(R.id.dialpad_operation_btn);
		mDialpadOperationBtn.setText(R.string.hide_keyboard);

		

	}

	private void initData() {
		ContactsHelper.getInstance().setOnContactsLoad(this);
		boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
		if (true == startLoad) {
			mContactsOperationView.contactsLoading();
		}
	}

	private void initListener() {
		mDialpadOperationBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clickDialpad();
			}
		});
		
	}

	private void clickDialpad() {
		if (mT9TelephoneDialpadView.getT9TelephoneDialpadViewVisibility() == View.VISIBLE) {
			mT9TelephoneDialpadView.hideT9TelephoneDialpadView();
			mDialpadOperationBtn.setText(R.string.display_keyboard);
		} else {
			mT9TelephoneDialpadView.showT9TelephoneDialpadView();
			mDialpadOperationBtn.setText(R.string.hide_keyboard);
		}
	}

	@Override
	public void onAddDialCharacter(String addCharacter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeleteDialCharacter(String deleteCharacter) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDialInputTextChanged(String curCharacter) {
		
		if(TextUtils.isEmpty(curCharacter)){
			ContactsHelper.getInstance().parseT9InputSearchContacts(null);
		}else{
			ContactsHelper.getInstance().parseT9InputSearchContacts(curCharacter);
		}
		mContactsOperationView.updateContactsList();
	}

	@Override
	public void onHideT9TelephoneDialpadView() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onContactsLoadSuccess() {
		mContactsOperationView.contactsLoadSuccess();
		
		//just background printing contacts information
		//ContactsHelper.getInstance().showContactsInfo();
		ContactsIndexHelper.getInstance().praseContacts(ContactsHelper.getInstance().getBaseContacts());
		//ContactsIndexHelper.getInstance().showContactsInfo();
		
	}

	@Override
	public void onContactsLoadFailed() {
		mContactsOperationView.contactsLoadFailed();
	}

	
	
	
}
