package com.handsomezhou.contactssearch.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.handsomezhou.contactssearch.R;
import com.handsomezhou.contactssearch.helper.ContactsHelper;
import com.handsomezhou.contactssearch.helper.ContactsHelper.OnContactsLoad;
import com.handsomezhou.contactssearch.helper.ContactsIndexHelper;
import com.handsomezhou.contactssearch.model.Contacts;
import com.handsomezhou.contactssearch.util.ShareUtil;
import com.handsomezhou.contactssearch.util.ViewUtil;
import com.handsomezhou.contactssearch.view.ContactsOperationView;
import com.handsomezhou.contactssearch.view.ContactsOperationView.OnContactsOperationView;
import com.handsomezhou.contactssearch.view.T9TelephoneDialpadView;
import com.handsomezhou.contactssearch.view.T9TelephoneDialpadView.OnT9TelephoneDialpadView;

public class T9SearchFragment extends BaseFragment implements OnT9TelephoneDialpadView,OnContactsLoad,OnContactsOperationView{
	private static final String TAG="T9SearchFragment";
	private T9TelephoneDialpadView mT9TelephoneDialpadView;
	private ContactsOperationView mContactsOperationView;
	
	
	private View mKeyboardSwitchLayout;
	private ImageView mKeyboardSwitchIv;
	
	private boolean mFirstRefreshView=true;
	
	@Override
	public void onResume() {
		if(false==isFirstRefreshView()){
			mContactsOperationView.refreshContactsLv();
		}else{
			setFirstRefreshView(false);
		}
		
		super.onResume();
	}

	@Override
	public void onDestroy() {
		mT9TelephoneDialpadView.clearT9Input();
		ContactsHelper.getInstance().t9InputSearch(null);
		
		List<Contacts> selectedContactsList=new ArrayList<Contacts>();
		selectedContactsList.addAll(ContactsHelper.getInstance().getSelectedContacts().values());
		Log.i(TAG, "onDestroy() selectedContactsList.size()="+selectedContactsList.size());
		for(Contacts cs:selectedContactsList){
			Log.i(TAG, "onDestroy() name=["+cs.getName()+"] phoneNumber=["+cs.getPhoneNumber()+"]");
		}
		
		mContactsOperationView.clearSelectedContacts();
		ContactsHelper.getInstance().clearSelectedContacts();
		super.onDestroy();
	}
	
	@Override
	protected void initData() {
		setContext(getActivity());
		ContactsHelper.getInstance().setOnContactsLoad(this);
		setFirstRefreshView(true);
	}

	@Override
	protected View initView(LayoutInflater inflater, ViewGroup container) {
		View view=inflater.inflate(R.layout.fragment_t9_search, container, false);
		mT9TelephoneDialpadView = (T9TelephoneDialpadView) view.findViewById(R.id.t9_telephone_dialpad_layout);
		mT9TelephoneDialpadView.setOnT9TelephoneDialpadView(this);

		mContactsOperationView = (ContactsOperationView)view.findViewById(R.id.contacts_operation_layout);
		mContactsOperationView.setOnContactsOperationView(this);
		boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
		if (true == startLoad) {
			mContactsOperationView.contactsLoading();
		}
		
		mKeyboardSwitchLayout = view.findViewById(R.id.keyboard_switch_layout);
		mKeyboardSwitchIv = (ImageView) view.findViewById(R.id.keyboard_switch_image_view);
		showKeyboard();
		return view;
	}

	@Override
	protected void initListener() {
		mKeyboardSwitchLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switchKeyboard();
			}
		});

		mKeyboardSwitchIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switchKeyboard();
			}
		});
		
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
			ContactsHelper.getInstance().t9InputSearch(null);
		}else{
			ContactsHelper.getInstance().t9InputSearch(curCharacter);
		}
		mContactsOperationView.updateContactsList(TextUtils.isEmpty(curCharacter));
	}

	@Override
	public void onHideT9TelephoneDialpadView() {
		hideKeyboard();
	}
	/*end:OnT9TelephoneDialpadView*/
	
	/*start:OnContactsLoad*/
	@Override
	public void onContactsLoadSuccess() {
		ContactsHelper.getInstance().t9InputSearch(null);
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
		ContactsHelper.getInstance().t9InputSearch(null);
		mContactsOperationView.updateContactsList(true);
	}

	@Override
	public void onAddContactsSelected(Contacts contacts) {
		if(null!=contacts){
			Log.i(TAG, "onAddContactsSelected name=["+contacts.getName()+"] phoneNumber=["+contacts.getPhoneNumber()+"]");
			Toast.makeText(getContext(),"Add ["+contacts.getName()+":"+contacts.getPhoneNumber()+"]", Toast.LENGTH_SHORT).show();
			ContactsHelper.getInstance().addSelectedContacts(contacts);
		}
	}


	@Override
	public void onRemoveContactsSelected(Contacts contacts) {
		if(null!=contacts){
			Log.i(TAG, "onRemoveContactsSelected name=["+contacts.getName()+"] phoneNumber=["+contacts.getPhoneNumber()+"]");
			Toast.makeText(getContext(),"Remove ["+contacts.getName()+":"+contacts.getPhoneNumber()+"]", Toast.LENGTH_SHORT).show();
			ContactsHelper.getInstance().removeSelectedContacts(contacts);
		}
	}
	
	@Override
	public void onContactsCall(Contacts contacts) {
		//Toast.makeText(mContext, "onContactsCall"+contacts.getPhoneNumber(), Toast.LENGTH_SHORT).show();
		if(null!=contacts){
			 Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+contacts.getPhoneNumber()));
			 startActivity(intent);
		}
	}


	@Override
	public void onContactsSms(Contacts contacts) {
		//Toast.makeText(mContext, "onContactsSms"+contacts.getPhoneNumber(), Toast.LENGTH_SHORT).show();
		if(null!=contacts){
			ShareUtil.shareTextBySms(getContext(), contacts.getPhoneNumber(), null);
		}
	}
	/*end:OnContactsOperationView*/
	
	public boolean isFirstRefreshView() {
		return mFirstRefreshView;
	}

	public void setFirstRefreshView(boolean firstRefreshView) {
		mFirstRefreshView = firstRefreshView;
	}

	
	private void switchKeyboard() {
		if (ViewUtil.getViewVisibility(mT9TelephoneDialpadView) == View.GONE) {
			showKeyboard();
		} else {
			hideKeyboard();
		}
	}

	private void hideKeyboard() {
		ViewUtil.hideView(mT9TelephoneDialpadView);
		mKeyboardSwitchIv
				.setBackgroundResource(R.drawable.keyboard_show_selector);
	}

	private void showKeyboard() {
		ViewUtil.showView(mT9TelephoneDialpadView);
		mKeyboardSwitchIv
				.setBackgroundResource(R.drawable.keyboard_hide_selector);
	}
	
	

}
