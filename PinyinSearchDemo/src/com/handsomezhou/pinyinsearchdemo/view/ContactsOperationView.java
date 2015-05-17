package com.handsomezhou.pinyinsearchdemo.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsAdapter;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsAdapter.OnContactsAdapter;
import com.handsomezhou.pinyinsearchdemo.helper.ContactsHelper;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ViewUtil;
import com.handsomezhou.pinyinsearchdemo.view.ContactsIndexView.OnContactsIndexView;
import com.handsomezhou.pinyinsearchdemo.view.QuickAlphabeticBar.OnQuickAlphabeticBar;

public class ContactsOperationView extends FrameLayout implements
		OnContactsIndexView, OnQuickAlphabeticBar ,OnContactsAdapter{
	public static final String CONTACTS_INDEX="CONTACTS_INDEX";
	private static final String TAG = "ContactsOperationView";
	private static final int VIEW_SHOW_TIME_MILLIS = 4000;// ms
	private static final int HANDLER_MSG_VIEW_DISPLAY = 0x01;
	private static final int HANDLER_MSG_VIEW_DISAPPEAR = 0x02;
	private Context mContext;
	private ListView mContactsLv;
	private QuickAlphabeticBar mQuickAlphabeticBar;
	private ContactsIndexView mContactsIndexView;
	private View mLoadContactsView;
	private TextView mSelectCharTv;
	private TextView mSearchResultPromptTv;
	private ContactsAdapter mContactsAdapter;
	private View mContactsOperationView;
	private OnContactsOperationView mOnContactsOperationView;

	public interface OnContactsOperationView{
		void onListItemClick(Contacts contacts,int position);
		void onAddContactsSelected(Contacts contacts);
		void onRemoveContactsSelected(Contacts contacts);
		void onContactsCall(Contacts contacts);
		void onContactsSms(Contacts contacts);
	}
	
	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_MSG_VIEW_DISPLAY:
				break;
			case HANDLER_MSG_VIEW_DISAPPEAR:
				ViewUtil.hideView(mContactsIndexView);
				break;
			default:
				break;
			}

		}

	};

	public ContactsOperationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
		initData();
		initListener();
	}

	
	
	@Override
	public void onContactsSelected(Contacts contacts) {
		int contactsIndex = ContactsHelper.getInstance()
				.getSearchContactsIndex(contacts);
		if (contactsIndex < 0) {
			return;
		}

		mContactsLv.setSelection(contactsIndex);
		
		clearViewDisappearMsg();
		sendViewDisappearMsg();
		// mQuickAlphabeticLv.setSelection(position);
	}

	@Override
	public void onQuickAlphabeticBarDown(char selectCharacters) {
		clearViewDisappearMsg();
		ViewUtil.showView(mContactsIndexView);
		//Log.i(TAG, "onQuickAlphabeticBarDown");
	}

	@Override
	public void onQuickAlphabeticBarUp(char selectCharacters) {
		// hideView(mContactsIndexView);
		sendViewDisappearMsg();
		Log.i(TAG, "onQuickAlphabeticBarUp");
	}

	/*start:OnContactsAdapter*/

	@Override
	public void onAddContactsSelected(Contacts contacts) {
		if(null!=contacts){
			mOnContactsOperationView.onAddContactsSelected(contacts);
		}
	}

	@Override
	public void onRemoveContactsSelected(Contacts contacts) {
		if(null!=contacts){
			mOnContactsOperationView.onRemoveContactsSelected(contacts);
		}
	}
	@Override
	public void onContactsCall(Contacts contacts) {
		if(null!=contacts){
			mOnContactsOperationView.onContactsCall(contacts);
		}
	}

	@Override
	public void onContactsSms(Contacts contacts) {
		if(null!=contacts){
			mOnContactsOperationView.onContactsSms(contacts);
		}
	}

	@Override
	public void onContactsRefreshView() {
		// TODO Auto-generated method stub
		updateContactsList();
	}
	/*end:OnContactsAdapter*/
	
	public OnContactsOperationView getOnContactsOperationView() {
		return mOnContactsOperationView;
	}

	public void setOnContactsOperationView(OnContactsOperationView onContactsOperationView) {
		mOnContactsOperationView = onContactsOperationView;
	}
	
	public void contactsLoading() {
		ViewUtil.showView(mLoadContactsView);
	}

	public void contactsLoadSuccess() {
		ViewUtil.hideView(mLoadContactsView);
		updateContactsList(true);
	}

	public void contactsLoadFailed() {
		ViewUtil.hideView(mLoadContactsView);
		ViewUtil.showView(mContactsLv);
	}

	public void clearSelectedContacts(){
		mContactsAdapter.clearSelectedContacts();
	}
	
	public void updateContactsList(boolean searchEmpty) {
		if (null == mContactsLv) {
			return;
		}
		
		
		if(true==searchEmpty){
			ViewUtil.showView(mQuickAlphabeticBar);
		}else{
			ViewUtil.hideView(mQuickAlphabeticBar);
		}
		
		updateContactsList();
	}
	
	public void updateContactsList() {
		if (null == mContactsLv) {
			return;
		}
		
		ViewUtil.hideView(mContactsIndexView);
		
		BaseAdapter contactsAdapter = (BaseAdapter) mContactsLv.getAdapter();
		if (null != contactsAdapter) {
			contactsAdapter.notifyDataSetChanged();
			if (contactsAdapter.getCount() > 0) {
				ViewUtil.showView(mContactsLv);
				ViewUtil.hideView(mSearchResultPromptTv);

			} else {
				ViewUtil.hideView(mContactsLv);
				ViewUtil.showView(mSearchResultPromptTv);

			}
		}
	}
	
	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContactsOperationView = inflater.inflate(R.layout.contacts_operation,
				this);

		mContactsLv = (ListView) mContactsOperationView
				.findViewById(R.id.contacts_list_view);
		mQuickAlphabeticBar = (QuickAlphabeticBar) mContactsOperationView
				.findViewById(R.id.quick_alphabetic_bar);
		mQuickAlphabeticBar.setOnQuickAlphabeticBar(this);
		mContactsIndexView = (ContactsIndexView) mContactsOperationView
				.findViewById(R.id.contacts_index_view);
		mContactsIndexView.setOnContactsIndexView(this);
		mLoadContactsView = mContactsOperationView
				.findViewById(R.id.load_contacts);
		mSelectCharTv = (TextView) mContactsOperationView
				.findViewById(R.id.select_char_text_view);
		mSearchResultPromptTv = (TextView) mContactsOperationView
				.findViewById(R.id.search_result_prompt_text_view);

		ViewUtil.showView(mContactsLv);
		ViewUtil.hideView(mContactsIndexView);
		ViewUtil.hideView(mLoadContactsView);
		ViewUtil.hideView(mSearchResultPromptTv);
	}

	private void initData() {
		mContactsAdapter = new ContactsAdapter(mContext,
				R.layout.contacts_list_item, ContactsHelper.getInstance()
						.getSearchContacts());
		mContactsAdapter.setOnContactsAdapter(this);
		mContactsLv.setAdapter(mContactsAdapter);
	}

	private void initListener() {
		mContactsLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Contacts contacts = ContactsHelper.getInstance()
						.getSearchContacts().get(position);
				if(false==contacts.isFirstMultipleContacts()){
					return;
				}
				
				Contacts.hideOrUnfoldMultipleContactsView(contacts);
				
				if(null!=mOnContactsOperationView){
					mOnContactsOperationView.onListItemClick(contacts,position);
				}
				
			}
		});

		mContactsLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// Log.i(TAG,
				// "firstVisibleItem=["+firstVisibleItem+"]visibleItemCount=["+visibleItemCount+"totalItemCount=["+totalItemCount+"]");
				Adapter adapter = mContactsLv.getAdapter();
				int currentIndex = 0;
				if ((null != adapter) && adapter.getCount() > 0) {
					currentIndex = ((firstVisibleItem + visibleItemCount) < totalItemCount) ? (firstVisibleItem)
							: (totalItemCount - 1);
					Contacts contacts = (Contacts) adapter
							.getItem(currentIndex);
					char currentSelectChar = contacts.getSortKey().charAt(0);
					mQuickAlphabeticBar.setCurrentSelectChar(currentSelectChar);
					mContactsIndexView.setCurrentSelectChar(currentSelectChar);
				}

			}
		});

		mQuickAlphabeticBar.setSectionIndexer(mContactsAdapter);
		mQuickAlphabeticBar.setQuickAlphabeticLv(mContactsLv);
		mQuickAlphabeticBar.setSelectCharTv(mSelectCharTv);
	}

	private void sendViewDisappearMsg() {
		clearViewDisappearMsg();
		handler.sendEmptyMessageDelayed(HANDLER_MSG_VIEW_DISAPPEAR,
				VIEW_SHOW_TIME_MILLIS);
		return;
	}

	private void clearViewDisappearMsg() {

		if (handler.hasMessages(HANDLER_MSG_VIEW_DISAPPEAR)) {
			handler.removeMessages(HANDLER_MSG_VIEW_DISAPPEAR);
		}

		return;
	}
	
}
