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
import android.widget.Toast;

import com.handsomezhou.contactssearch.R;
import com.handsomezhou.contactssearch.helper.ContactsHelper;
import com.handsomezhou.contactssearch.helper.ContactsHelper.OnContactsLoad;
import com.handsomezhou.contactssearch.helper.ContactsIndexHelper;
import com.handsomezhou.contactssearch.model.Contacts;
import com.handsomezhou.contactssearch.util.ShareUtil;
import com.handsomezhou.contactssearch.view.ContactsOperationView;
import com.handsomezhou.contactssearch.view.ContactsOperationView.OnContactsOperationView;
import com.handsomezhou.contactssearch.view.SearchBox;
import com.handsomezhou.contactssearch.view.SearchBox.OnSearchBox;

public class QwertySearchFragment extends BaseFragment implements OnContactsLoad,OnContactsOperationView,OnSearchBox{
	private static final String TAG="QwertySearchFragment";
	private ContactsOperationView mContactsOperationView;
	//private EditText mSearchEt;
	private SearchBox mSearchBox;
	private boolean mFirstRefreshView=true;;


	@Override
	public void onResume() {
		if(false==isFirstRefreshView()){
			mContactsOperationView.updateContactsList();
		}else{
			setFirstRefreshView(false);
		}
		super.onResume();
	}

	
	@Override
	public void onDestroy() {
		mSearchBox.getSearchEt().setText("");
		ContactsHelper.getInstance().qwertySearch(null);
		
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
		View view=inflater.inflate(R.layout.fragment_qwerty_search, container, false);
		mSearchBox=(SearchBox) view.findViewById(R.id.search_box);
		mSearchBox.setOnSearchBox(this);
		mContactsOperationView = (ContactsOperationView)view.findViewById(R.id.contacts_operation_layout);
		mContactsOperationView.setOnContactsOperationView(this);
		boolean startLoad = ContactsHelper.getInstance().startLoadContacts();
		if (true == startLoad) {
			mContactsOperationView.contactsLoading();
		}
		return view;
	}

	@Override
	protected void initListener() {
		
	}

	/*Start: OnContactsLoad*/
	@Override
	public void onContactsLoadSuccess() {
		ContactsHelper.getInstance().qwertySearch(null);
		mContactsOperationView.contactsLoadSuccess();
		
		ContactsIndexHelper.getInstance().praseContacts(ContactsHelper.getInstance().getBaseContacts());
		
	}

	@Override
	public void onContactsLoadFailed() {
		mContactsOperationView.contactsLoadFailed();
		
	}
	/*End: OnContactsLoad*/
	
	/*start: OnSearchBox*/
	@Override
	public void onSearchTextChanged(String curCharacter) {
		updateSearch(curCharacter);
		
	}
	/*end: OnSearchBox*/
	
	/*Start: OnContactsOperationView*/
	@Override
	public void onListItemClick(Contacts contacts,int position){
		ContactsHelper.getInstance().qwertySearch(null);
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
		ShareUtil.shareTextBySms(getContext(), contacts.getPhoneNumber(), null);
	}
	/*Start: OnContactsOperationView*/
	
	
	public boolean isFirstRefreshView() {
		return mFirstRefreshView;
	}


	public void setFirstRefreshView(boolean firstRefreshView) {
		mFirstRefreshView = firstRefreshView;
	}

	
	private void updateSearch(String search) {
		String curCharacter;
		if (null == search) {
			curCharacter = search;
		} else {
			curCharacter = search.trim();
		}
		
		if(TextUtils.isEmpty(curCharacter)){
			ContactsHelper.getInstance().qwertySearch(null);
		}else{
			ContactsHelper.getInstance().qwertySearch(curCharacter);
		}
		mContactsOperationView.updateContactsList(TextUtils.isEmpty(curCharacter));
		
	}


}
