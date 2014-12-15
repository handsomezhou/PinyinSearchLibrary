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
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsOperationView extends FrameLayout {

	private Context mContext;
	private ListView mContactsLv;
	private QuickAlphabeticBar mQuickAlphabeticBar;
	private View mLoadContactsView;
	private TextView mSelectCharTv;
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
		mQuickAlphabeticBar=(QuickAlphabeticBar)mContactsOperationView.findViewById(R.id.quick_alphabetic_bar);
		mLoadContactsView = mContactsOperationView.findViewById(R.id.load_contacts);
		mSelectCharTv=(TextView)mContactsOperationView.findViewById(R.id.select_char_text_view);
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
		
		mContactsLv.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Adapter adapter=mContactsLv.getAdapter();
				if((null!=adapter)&&adapter.getCount()>0){
					Contacts contacts=(Contacts)adapter.getItem(firstVisibleItem);
					char firstChar=contacts.getSortKey().charAt(0);
					mQuickAlphabeticBar.setCurrentSelectChar(firstChar);
				}
				
			}
		} );
		
		mQuickAlphabeticBar.setSectionIndexer(mContactsAdapter);
		mQuickAlphabeticBar.setQuickAlphabeticLv(mContactsLv);
		mQuickAlphabeticBar.setSelectCharTv(mSelectCharTv);
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
