package com.handsomezhou.pinyinsearchdemo.view;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsAdapter;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsOperationView extends FrameLayout {

	private Context mContext;
	private ListView mContactsLv;
	private View mLoadContactsView;
	private TextView mSearchResultPromptTv;
	private ContactsAdapter mContactsAdapter;
	
	private View mContactsOperationView;
	
	public ContactsOperationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
		initData();
		initListener();
	}

	public void contactsLoading(){
		showView(mLoadContactsView);
	}
	
	public void contactsLoadSuccess(){
		hideView(mLoadContactsView);
		updateContactsList();
	}
	
	public void contactsLoadFailed(){
		hideView(mLoadContactsView);
		showView(mContactsLv);
	}
	
	private void initView(){
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContactsOperationView = inflater.inflate(R.layout.contacts_operation,
				this);
		
		mContactsLv = (ListView) mContactsOperationView.findViewById(R.id.contacts_list_view);
		mLoadContactsView = mContactsOperationView.findViewById(R.id.load_contacts);
		mSearchResultPromptTv = (TextView) mContactsOperationView.findViewById(R.id.search_result_prompt_text_view);
		
		showView(mContactsLv);
		hideView(mLoadContactsView);
		hideView(mSearchResultPromptTv);
	}
	
	private void initData(){
		mContactsAdapter = new ContactsAdapter(mContext,
				R.layout.contacts_list_item, ContactsHelper.getInstance()
						.getSearchContacts());
		mContactsLv.setAdapter(mContactsAdapter);
	}
	
	private void initListener(){
		mContactsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Contacts contacts=ContactsHelper.getInstance().getSearchContacts().get(position);
				 String uri = "tel:" + contacts.getPhoneNumber() ;
				 Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse(uri));
				// intent.setData(Uri.parse(uri));
				 mContext.startActivity(intent);
				
			}
		});
	}
	
	private void hideView(View view) {
		if (null == view) {
			return;
		}
		if (View.GONE != view.getVisibility()) {
			view.setVisibility(View.GONE);
		}

		return;
	}

	private int getViewVisibility(View view) {
		if (null == view) {
			return View.GONE;
		}

		return view.getVisibility();
	}

	private void showView(View view) {
		if (null == view) {
			return;
		}

		if (View.VISIBLE != view.getVisibility()) {
			view.setVisibility(View.VISIBLE);
		}
	}

	
	public void updateContactsList(){
		if(null==mContactsLv){
			return;
		}
		
		BaseAdapter contactsAdapter=(BaseAdapter) mContactsLv.getAdapter();
		if(null!=contactsAdapter){
			contactsAdapter.notifyDataSetChanged();
			if(contactsAdapter.getCount()>0){
				showView(mContactsLv);
				hideView(mSearchResultPromptTv);
				
			}else{
				hideView(mContactsLv);
				showView(mSearchResultPromptTv);
				
			}
		}
	}
}
