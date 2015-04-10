package com.handsomezhou.pinyinsearchdemo.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.util.ShareUtil;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper.OnContactsLoad;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView.OnContactsOperationView;

public class QwertySearchActivity extends Activity implements OnContactsLoad,OnContactsOperationView{
	private static final String TAG="QwertySearchActivity";
	private Context mContext;
	private ContactsOperationView mContactsOperationView;
	private EditText mSearchEt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qwerty_search);
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
		
		mSearchEt.setText("");
		ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
		
		List<Contacts> selectedContactsList=new ArrayList<Contacts>();
		selectedContactsList.addAll(ContactsHelper.getInstance().getSelectedContacts().values());
		Log.i(TAG, "onDestroy() selectedContactsList.size()="+selectedContactsList.size());
		for(Contacts cs:selectedContactsList){
			Log.i(TAG, "onDestroy() name=["+cs.getName()+"] phoneNumber=["+cs.getPhoneNumber()+"]");
		}
		
		mContactsOperationView.clearSelectedContacts();
		ContactsHelper.getInstance().clearSelectedContacts();
	}


	private void initView(){
		mSearchEt=(EditText)findViewById(R.id.search_edit_text);
		mContactsOperationView = (ContactsOperationView)findViewById(R.id.contacts_operation_layout);
		mContactsOperationView.setOnContactsOperationView(this);
	}
	
	private void initData(){
		ContactsHelper.getInstance().setOnContactsLoad(this);
		boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
		if (true == startLoad) {
			mContactsOperationView.contactsLoading();
		}
	}
	
	private void initListener(){
		mSearchEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String curCharacter=s.toString().trim();
				
				if(TextUtils.isEmpty(curCharacter)){
					ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
				}else{
					ContactsHelper.getInstance().parseQwertyInputSearchContacts(curCharacter);
				}
				mContactsOperationView.updateContactsList(TextUtils.isEmpty(curCharacter));
				
			}
		});
	}
	
	/*start:OnContactsLoad*/
	@Override
	public void onContactsLoadSuccess() {
		ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
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
		ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
		mContactsOperationView.updateContactsList(true);
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
	
	@Override
	public void onContactsCall(Contacts contacts) {
		//Toast.makeText(mContext, "onContactsCall"+contacts.getPhoneNumber(), Toast.LENGTH_SHORT).show();
		if(null!=contacts){
			 Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contacts.getPhoneNumber()));
			 mContext.startActivity(intent);
		}
	}


	@Override
	public void onContactsSms(Contacts contacts) {
		//Toast.makeText(mContext, "onContactsSms"+contacts.getPhoneNumber(), Toast.LENGTH_SHORT).show();
		ShareUtil.shareTextBySms(mContext, contacts.getPhoneNumber(), null);
	}
	/*end:OnContactsOperationView*/

}
