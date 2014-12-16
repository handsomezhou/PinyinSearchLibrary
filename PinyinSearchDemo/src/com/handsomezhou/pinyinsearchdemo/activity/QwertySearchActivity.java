package com.handsomezhou.pinyinsearchdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper.OnContactsLoad;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;

public class QwertySearchActivity extends Activity implements OnContactsLoad{
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
	protected void onDestroy() {
		super.onDestroy();
		
		mSearchEt.setText("");
		ContactsHelper.getInstance().parseQwertyInputSearchContacts(null);
	}


	private void initView(){
		mSearchEt=(EditText)findViewById(R.id.search_edit_text);
		mContactsOperationView = (ContactsOperationView)findViewById(R.id.contacts_operation_layout);
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
				mContactsOperationView.updateContactsList();
				
			}
		});
	}

	@Override
	public void onContactsLoadSuccess() {
		mContactsOperationView.contactsLoadSuccess();
		
		//just background printing contacts information
		//ContactsHelper.getInstance().showContactsInfo();
		ContactsIndexHelper.getInstance().praseContacts(ContactsHelper.getInstance().getBaseContacts());
		ContactsIndexHelper.getInstance().showContactsInfo();
	}

	@Override
	public void onContactsLoadFailed() {
		mContactsOperationView.contactsLoadFailed();
	}
}
