package com.handsomezhou.pinyinsearchdemo.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handsomezhou.pinyinsearchdemo.R;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsIndexAdapter;
import com.handsomezhou.pinyinsearchdemo.adapter.ContactsIndexAdapter.OnContactsIndexAdapter;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.model.ContactsIndex;
import com.handsomezhou.pinyinsearchdemo.util.ContactsIndexHelper;
import com.pinyinsearch.util.PinyinUtil;

/**
 * 
 * @author handsomezhou
 * @date 2014-12-16
 */
public class ContactsIndexView extends LinearLayout implements OnContactsIndexAdapter{
	private static final String TAG = "ContactsIndexView";

	private Context mContext;
	private View mContactsIndexView;
	private TextView mIndexTv;
	private ListView mIndexLv; // listview
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
		mIndexTv = (TextView) mContactsIndexView
				.findViewById(R.id.index_text_view);
		mIndexLv = (ListView) mContactsIndexView
				.findViewById(R.id.index_list_view);

		return;
	}

	private void initData() {
		mContactsIndex = new ContactsIndex();
		// setCurrentSelectChar('#');
		mContactsIndexAdapter = new ContactsIndexAdapter(mContext,
				R.layout.contacts_index_list_item, mContactsIndex.getContacts());
		mContactsIndexAdapter.setOnContactsIndexAdapter(this);
		mIndexLv.setAdapter(mContactsIndexAdapter);
		mIndexTv.setText("好");

		return;
	}

	private void initListener() {
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

		int contactsIndexsCount = ContactsIndexHelper.getInstance()
				.getContactsIndexs().size();
		for (int i = 0; i < contactsIndexsCount; i++) {
			if (currentSelectString.equals(ContactsIndexHelper.getInstance()
					.getContactsIndexs().get(i).getIndexKey())) {
				mContactsIndex.getContacts().clear();
				mContactsIndex.getContacts().addAll(
						ContactsIndexHelper.getInstance().getContactsIndexs()
								.get(i).getContacts());

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
		if (null == mIndexLv) {
			return;
		}

		BaseAdapter contactsAdapter = (BaseAdapter) mIndexLv.getAdapter();
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
