package com.handsomezhou.pinyinsearchdemo.view;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsIndexAdapter;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsIndexAdapter.OnContactsIndexAdapter;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.model.ContactsIndex;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;

/**
 * 
 * @author handsomezhou
 * @date 2014-12-16
 */
public class ContactsIndexView extends LinearLayout implements OnContactsIndexAdapter{
	private static final String TAG = "ContactsIndexView";

	private Context mContext;
	private View mContactsIndexView;
	private TextView mIndexKeyTv;
	private ListView mIndexValueLv; // listview
	private ContactsIndex mContactsIndex;// data
	private ContactsIndexAdapter mContactsIndexAdapter;// adapter
	private OnContactsIndexView mOnContactsIndexView;

	private char mCurrentSelectChar;

	public interface OnContactsIndexView{
		public void onContactsSelected(final Contacts contacts);
	}
	public ContactsIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
		initData();
		initListener();
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContactsIndexView = inflater.inflate(R.layout.contacts_index_view,
				this);

		// mContactsIndexView.setBackgroundColor(getResources().getColor(R.color.cyan3));
		mIndexKeyTv = (TextView) mContactsIndexView
				.findViewById(R.id.index_key_text_view);
		mIndexValueLv = (ListView) mContactsIndexView
				.findViewById(R.id.index_list_view);

		return;
	}

	private void initData() {
		mContactsIndex = new ContactsIndex();
		// setCurrentSelectChar('#');
		mContactsIndexAdapter = new ContactsIndexAdapter(mContext,
				R.layout.contacts_index_list_item, mContactsIndex.getContacts());
		mContactsIndexAdapter.setOnContactsIndexAdapter(this);
		mIndexValueLv.setAdapter(mContactsIndexAdapter);
		mIndexKeyTv.setText("");

		return;
	}

	private void initListener() {
		/*mIndexValueLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});*/
	
	
		return;
	}

	public OnContactsIndexView getOnContactsIndexView() {
		return mOnContactsIndexView;
	}

	public void setOnContactsIndexView(OnContactsIndexView onContactsIndexView) {
		mOnContactsIndexView = onContactsIndexView;
	}
	
	public char getCurrentSelectChar() {
		return mCurrentSelectChar;
	}

	public void setCurrentSelectChar(char currentSelectChar) {
		if (mCurrentSelectChar == currentSelectChar) {
			return;
		}

		mCurrentSelectChar = currentSelectChar;
		String currentSelectString = String.valueOf(mCurrentSelectChar);
		if (TextUtils.isEmpty(currentSelectString)) {
			return;
		}
		
		//set current index key
		mIndexKeyTv.setText(String.valueOf(mCurrentSelectChar));
		
		//set current index value
		int contactsIndexsCount = ContactsIndexHelper.getInstance()
				.getContactsIndexs().size();
		for (int i = 0; i < contactsIndexsCount; i++) {
			if (currentSelectString.equals(ContactsIndexHelper.getInstance()
					.getContactsIndexs().get(i).getIndexKey())) {
				mContactsIndex.getContacts().clear();
				
				 List<Contacts> contacts=ContactsIndexHelper.getInstance().getContactsIndexs().get(i).getContacts();
				 
				 if((null==contacts)||(contacts.size()<=0)){
					 break;
				 }
				 
				mContactsIndex.getContacts().add(contacts.get(0));
				for(int j=1; j<contacts.size(); j++){
//				if(contacts.get(j-1).getName().charAt(0)!=contacts.get(j).getName().charAt(0)){
					if(!String.valueOf(contacts.get(j-1).getName().charAt(0)).equalsIgnoreCase(String.valueOf(contacts.get(j).getName().charAt(0)))){
						mContactsIndex.getContacts().add(contacts.get(j));
					}
				}

				break;
			}
		}

		for (int j = 0; j < mContactsIndex.getContacts().size(); j++) {
			mContactsIndex.getContacts().get(j).showContacts();
		}

		updateContactsList();
		return;
	}

	public void updateContactsList() {
		if (null == mIndexValueLv) {
			return;
		}

		BaseAdapter contactsAdapter = (BaseAdapter) mIndexValueLv.getAdapter();
		if (null != contactsAdapter) {
			contactsAdapter.notifyDataSetChanged();
			if (contactsAdapter.getCount() > 0) {

			} else {

			}

		}
	}

	@Override
	public void onIndexKeyClick(Contacts contacts) {
		if(null!=mOnContactsIndexView){
			/*Toast.makeText(mContext,PinyinUtil.getFirstCharacter(contacts.getNamePinyinUnits()),
					Toast.LENGTH_SHORT).show();*/
			mOnContactsIndexView.onContactsSelected(contacts);
		}
	}

}
