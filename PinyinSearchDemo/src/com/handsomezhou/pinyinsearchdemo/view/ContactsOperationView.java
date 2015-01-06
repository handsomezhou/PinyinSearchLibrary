package com.handsomezhou.pinyinsearchdemo.view;

import java.util.ArrayList;
import java.util.List;

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
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.util.ContactsHelper;
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
	}
	
	final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLER_MSG_VIEW_DISPLAY:
				break;
			case HANDLER_MSG_VIEW_DISAPPEAR:
				hideView(mContactsIndexView);
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

	public OnContactsOperationView getOnContactsOperationView() {
		return mOnContactsOperationView;
	}

	public void setOnContactsOperationView(OnContactsOperationView onContactsOperationView) {
		mOnContactsOperationView = onContactsOperationView;
	}
	
	public void contactsLoading() {
		showView(mLoadContactsView);
	}

	public void contactsLoadSuccess() {
		hideView(mLoadContactsView);
		updateContactsList();
	}

	public void contactsLoadFailed() {
		hideView(mLoadContactsView);
		showView(mContactsLv);
	}

	public void clearSelectedContacts(){
		mContactsAdapter.clearSelectedContacts();
	}
	
	public static Contacts getContacts(int position){
		if((position<0)||(position>=ContactsHelper.getInstance().getSearchContacts().size())){
			return null;
		}
		return ContactsHelper.getInstance().getSearchContacts().get(position);
	}
	
	/**
	 * parse one or multiple numbers from the contacts
	 * @param contacts
	 * @return
	 */
	public static List<Contacts> parseContacts(Contacts contacts){
		if((null==contacts)){
			return null;
		}
		
		List<Contacts> contactsList=new ArrayList<Contacts>();
		contactsList.add(contacts);
		List<Contacts> multipleNumbersContacts=contacts.getMultipleNumbersContacts();
		for(Contacts cs:multipleNumbersContacts){
			contactsList.add(cs);
		}
		
		return contactsList;
	}
	
	public void updateContactsList() {
		if (null == mContactsLv) {
			return;
		}

		BaseAdapter contactsAdapter = (BaseAdapter) mContactsLv.getAdapter();
		if (null != contactsAdapter) {
			contactsAdapter.notifyDataSetChanged();
			if (contactsAdapter.getCount() > 0) {
				showView(mContactsLv);
				hideView(mSearchResultPromptTv);

			} else {
				hideView(mContactsLv);
				showView(mSearchResultPromptTv);

			}
			// showView(mContactsIndexView);//just for test
		}
	}
	
	@Override
	public void onContactsSelected(Contacts contacts) {
//		Toast.makeText(mContext,
//				PinyinUtil.getFirstCharacter(contacts.getNamePinyinUnits()),
//				Toast.LENGTH_SHORT).show();
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
	public void onQuickAlphabeticBarDown() {
		clearViewDisappearMsg();
		showView(mContactsIndexView);
		Log.i(TAG, "onQuickAlphabeticBarDown");
	}

	@Override
	public void onQuickAlphabeticBarUp() {
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
	/*end:OnContactsAdapter*/
	
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

		showView(mContactsLv);
		hideView(mContactsIndexView);
		hideView(mLoadContactsView);
		hideView(mSearchResultPromptTv);
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
				if(null!=mOnContactsOperationView){
					mOnContactsOperationView.onListItemClick(contacts,position);
				}
				/*String uri = "tel:" + contacts.getPhoneNumber();
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
				// intent.setData(Uri.parse(uri));
				mContext.startActivity(intent);*/
				/*
				Intent intent=new Intent(mContext, ContactDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt(CONTACTS_INDEX, position);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
				*/
				

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
