package com.handsomezhou.pinyinsearchdemo.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsDetailAdapter;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsDetailAdapter.OnContactsAdapter;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.view.ContactsOperationView;

public class ContactDetailActivity extends Activity  implements OnContactsAdapter{
	private static final String TAG="ContactDetailActivity";
	private Context mContext;
	private Contacts mContacts;
	private TextView mContactsDetailTv;
	private List<Contacts> mContactsList;
	private ListView mContactsLv;
	private ContactsDetailAdapter mContactsDetailAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_detail);
		mContext=this;
		Bundle bundle = this.getIntent().getExtras();
		if(null!=bundle){
			int position=bundle.getInt(ContactsOperationView.CONTACTS_INDEX);
			mContacts=ContactsOperationView.getContacts(position);
			
		}
		initData();
		initView();
		initListener();
	}
	
	/*start:OnContactsAdapter*/
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
	/*end:OnContactsAdapter*/
	
	private void initData(){
		mContactsList=new ArrayList<Contacts>();
		mContactsList.addAll(ContactsOperationView.parseContacts(mContacts));
		mContactsDetailAdapter=new ContactsDetailAdapter(mContext, R.layout.contacts_detail_list_item, mContactsList);
		mContactsDetailAdapter.setOnContactsAdapter(this);
	}
	
	private void initView(){
		mContactsDetailTv=(TextView)findViewById(R.id.contacts_detail_text_view);
		mContactsDetailTv.setText(mContext.getResources().getString(R.string.phone_number_of_contacts, mContacts.getName()));
		mContactsLv=(ListView)findViewById(R.id.contacts_detail_list_view);
		mContactsLv.setAdapter(mContactsDetailAdapter);
	}
	
	private void initListener(){
		
	}
}
