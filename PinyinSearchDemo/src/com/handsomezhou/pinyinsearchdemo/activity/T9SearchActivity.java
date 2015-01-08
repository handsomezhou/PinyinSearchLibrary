package com.handsomezhou.pinyinsearchdemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper.OnContactsLoad;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView.OnContactsOperationView;
import com.handsomezhou.pinyinsearchdemo.view.T9TelephoneDialpadView;
import com.handsomezhou.pinyinsearchdemo.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;

public class T9SearchActivity extends Activity implements OnT9TelephoneDialpadView,OnContactsLoad,OnContactsOperationView{
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
	protected void onRestart() {
		super.onRestart();
		
		mContactsOperationView.updateContactsList();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		mT9TelephoneDialpadView.clearT9Input();
		ContactsHelper.getInstance().parseT9InputSearchContacts(null);
		
		List<Contacts> selectedContactsList=new ArrayList<Contacts>();
		selectedContactsList.addAll(ContactsHelper.getInstance().getSelectedContacts().values());
		Log.i(TAG, "onDestroy() selectedContactsList.size()="+selectedContactsList.size());
		for(Contacts cs:selectedContactsList){
			Log.i(TAG, "onDestroy() name=["+cs.getName()+"] phoneNumber=["+cs.getPhoneNumber()+"]");
		}
		
		mContactsOperationView.clearSelectedContacts();
		ContactsHelper.getInstance().clearSelectedContacts();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//moveTaskToBack(true);
	}
	
	/*start:OnT9TelephoneDialpadView*/
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
	/*end:OnT9TelephoneDialpadView*/
	
	/*start:OnContactsLoad*/
	@Override
	public void onContactsLoadSuccess() {
		ContactsHelper.getInstance().parseT9InputSearchContacts(null);
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
	/*end:OnContactsLoad*/
	
	
	/*start:OnContactsOperationView*/
	@Override
	public void onListItemClick(Contacts contacts,int position){
		if(null!=contacts){
			Intent intent=new Intent(mContext, ContactDetailActivity.class);
			Bundle bundle=new Bundle();
			bundle.putInt(ContactsOperationView.CONTACTS_INDEX, position);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
	}

	@Override
	public void onAddContactsSelected(Contacts contacts) {
		if(null!=contacts){
			Log.i(TAG, "onAddContactsSelected name=["+contacts.getName()+"] phoneNumber=["+contacts.getPhoneNumber()+"]");
			Toast.makeText(mContext,"Add ["+contacts.getName()+":"+contacts.getPhoneNumber()+"]", Toast.LENGTH_SHORT).show();
			ContactsHelper.getInstance().addSelectedContacts(contacts);
		}
	}


	@Override
	public void onRemoveContactsSelected(Contacts contacts) {
		if(null!=contacts){
			Log.i(TAG, "onRemoveContactsSelected name=["+contacts.getName()+"] phoneNumber=["+contacts.getPhoneNumber()+"]");
			Toast.makeText(mContext,"Remove ["+contacts.getName()+":"+contacts.getPhoneNumber()+"]", Toast.LENGTH_SHORT).show();
			ContactsHelper.getInstance().removeSelectedContacts(contacts);
		}
	}
	/*end:OnContactsOperationView*/
	
	private void initView() {
		
		mT9TelephoneDialpadView = (T9TelephoneDialpadView) findViewById(R.id.t9_telephone_dialpad_layout);
		mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);

		mContactsOperationView = (ContactsOperationView)findViewById(R.id.contacts_operation_layout);
		mContactsOperationView.setOnContactsOperationView(this);
		
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
}
