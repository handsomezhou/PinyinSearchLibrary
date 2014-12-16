package com.handsomezhou.pinyinsearchdemo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.provider.ContactsContract;
import android.util.Log;

import com.handsomezhou.pinyinsearchdemo.main.T9SearchApplication;
import com.handsomezhou.pinyinsearchdemo.model.Contacts;
import com.handsomezhou.pinyinsearchdemo.model.Contacts.SearchByType;
import com.handsomezhou.pinyinsearchdemo.view.QuickAlphabeticBar;
import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyMatchPinyinUnits;
import com.pinyinsearch.util.T9MatchPinyinUnits;

public class ContactsHelper {
	private static final String TAG = "ContactsHelper";
	private Context mContext;
	private static ContactsHelper mInstance = null;
	private List<Contacts> mBaseContacts = null; // The basic data used for the
													// search
	private List<Contacts> mSearchContacts = null; // The search results from
													// the basic data
	/*
	 * save the first input string which search no result.
	 * mFirstNoSearchResultInput.size<=0, means that the first input string
	 * which search no result not appear. mFirstNoSearchResultInput.size>0,
	 * means that the first input string which search no result has appeared,
	 * it's mFirstNoSearchResultInput.toString(). We can reduce the number of
	 * search basic data by the first input string which search no result.
	 */
	private StringBuffer mFirstNoSearchResultInput = null;
	private AsyncTask<Object, Object, List<Contacts>> mLoadTask = null;
	private OnContactsLoad mOnContactsLoad = null;
	/*
	 * private OnContactsChanged mOnContactsChanged=null; private
	 * ContentObserver mContentObserver;
	 */
	private boolean mContactsChanged = true;

	/* private Handler mContactsHandler=new Handler(); */

	public interface OnContactsLoad {
		void onContactsLoadSuccess();

		void onContactsLoadFailed();
	}

	public interface OnContactsChanged {
		void onContactsChanged();
	}

	private ContactsHelper() {
		initContactsHelper();
		// registerContentObserver();
	}

	public static ContactsHelper getInstance() {
		if (null == mInstance) {
			mInstance = new ContactsHelper();
		}

		return mInstance;
	}

	public void destroy() {
		if (null != mInstance) {
			// unregisterContentObserver();
			mInstance = null;// the system will free other memory.
		}
	}

	public List<Contacts> getBaseContacts() {
		return mBaseContacts;
	}

	// public void setBaseContacts(List<Contacts> baseContacts) {
	// mBaseContacts = baseContacts;
	// }

	public List<Contacts> getSearchContacts() {
		return mSearchContacts;
	}

	public int getSearchContactsIndex(Contacts contacts) {
		int index = -1;
		if (null == contacts) {
			return -1;
		}
		int searchContactsCount = mSearchContacts.size();
		for (int i = 0; i < searchContactsCount; i++) {
			if (contacts.getName().charAt(0) == mSearchContacts.get(i)
					.getName().charAt(0)) {
				index = i;
				break;
			}
		}

		return index;
	}

	// public void setSearchContacts(List<Contacts> searchContacts) {
	// mSearchContacts = searchContacts;
	// }

	public OnContactsLoad getOnContactsLoad() {
		return mOnContactsLoad;
	}

	public void setOnContactsLoad(OnContactsLoad onContactsLoad) {
		mOnContactsLoad = onContactsLoad;
	}

	private boolean isContactsChanged() {
		return mContactsChanged;
	}

	private void setContactsChanged(boolean contactsChanged) {
		mContactsChanged = contactsChanged;
	}

	/**
	 * Provides an function to start load contacts
	 * 
	 * @return start load success return true, otherwise return false
	 */
	public boolean startLoadContacts() {
		if (true == isSearching()) {
			return false;
		}

		if (false == isContactsChanged()) {
			return false;
		}

		initContactsHelper();

		mLoadTask = new AsyncTask<Object, Object, List<Contacts>>() {

			@Override
			protected List<Contacts> doInBackground(Object... params) {
				return loadContacts(mContext);
			}

			@Override
			protected void onPostExecute(List<Contacts> result) {
				parseContacts(result);
				super.onPostExecute(result);
				setContactsChanged(false);
				mLoadTask = null;
			}
		}.execute();

		return true;
	}

	/**
	 * @description search base data according to string parameter
	 * @param search
	 *            (valid characters include:'0'~'9','*','#')
	 * @return void
	 *
	 * 
	 */
	public void parseT9InputSearchContacts(String search) {
		if (null == search) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for (Contacts contacts : mBaseContacts) {
				contacts.setSearchByType(SearchByType.SearchByNull);
				contacts.clearMatchKeywords();
			}

			mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="
					+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (search.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ search.length()
								+ "["
								+ search + "]");
				mFirstNoSearchResultInput.delete(0,
						mFirstNoSearchResultInput.length());
			}
		}

		if (null != mSearchContacts) {
			mSearchContacts.clear();
		} else {
			mSearchContacts = new ArrayList<Contacts>();
		}

		int contactsCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by name pinyin
		 * characters(org name->name pinyin characters) ('0'~'9','*','#')
		 * (2)Search by org name ('0'~'9','*','#') 2:Search by phone number
		 * ('0'~'9','*','#')
		 */
		for (int i = 0; i < contactsCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i)
					.getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get
																// Chinese
																// KeyWords.Of
																// course it's
																// maybe not
																// Chinese
																// characters.
			String name = mBaseContacts.get(i).getName();
			if (true == T9MatchPinyinUnits.matchPinyinUnits(pinyinUnits, name,
					search, chineseKeyWord)) {// search by NamePinyinUnits;
				mBaseContacts.get(i).setSearchByType(SearchByType.SearchByName);
				mBaseContacts.get(i)
						.setMatchKeywords(chineseKeyWord.toString());
				chineseKeyWord.delete(0, chineseKeyWord.length());
				mSearchContacts.add(mBaseContacts.get(i));
				continue;
			} else {
				if (mBaseContacts.get(i).getPhoneNumber().contains(search)) { // search
																				// by
																				// phone
																				// number
					mBaseContacts.get(i).setSearchByType(
							SearchByType.SearchByPhoneNumber);
					mBaseContacts.get(i).setMatchKeywords(search);
					mSearchContacts.add(mBaseContacts.get(i));
					continue;
				}

			}
		}

		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(search);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
			} else {

			}
		}

	}

	/**
	 * @description search base data according to string parameter
	 * @param search
	 * @return void
	 */
	public void parseQwertyInputSearchContacts(String search) {
		if (null == search) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for (Contacts contacts : mBaseContacts) {
				contacts.setSearchByType(SearchByType.SearchByNull);
				contacts.clearMatchKeywords();
			}

			mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="
					+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (search.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ search.length()
								+ "["
								+ search + "]");
				mFirstNoSearchResultInput.delete(0,
						mFirstNoSearchResultInput.length());
			}
		}

		if (null != mSearchContacts) {
			mSearchContacts.clear();
		} else {
			mSearchContacts = new ArrayList<Contacts>();
		}

		int contactsCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by original name (2)Search
		 * by name pinyin characters(original name->name pinyin characters)
		 * 2:Search by phone number
		 */
		for (int i = 0; i < contactsCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i)
					.getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get
																// Chinese
																// KeyWords.Of
																// course it's
																// maybe not
																// Chinese
																// characters.
			String name = mBaseContacts.get(i).getName();
			if (true == QwertyMatchPinyinUnits.matchPinyinUnits(pinyinUnits,
					name, search, chineseKeyWord)) {// search by
													// NamePinyinUnits;
				mBaseContacts.get(i).setSearchByType(SearchByType.SearchByName);
				mBaseContacts.get(i)
						.setMatchKeywords(chineseKeyWord.toString());
				chineseKeyWord.delete(0, chineseKeyWord.length());
				mSearchContacts.add(mBaseContacts.get(i));
				continue;
			} else {
				if (mBaseContacts.get(i).getPhoneNumber().contains(search)) { // search
																				// by
																				// phone
																				// number
					mBaseContacts.get(i).setSearchByType(
							SearchByType.SearchByPhoneNumber);
					mBaseContacts.get(i).setMatchKeywords(search);
					mSearchContacts.add(mBaseContacts.get(i));
					continue;
				}

			}
		}

		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(search);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + search.length() + "["
								+ search + "]");
			} else {

			}
		}

	}

	// just for debug
	public void showContactsInfo() {
		int contactsCount = ContactsHelper.getInstance().getBaseContacts()
				.size();
		for (int i = 0; i < contactsCount; i++) {
			String name = ContactsHelper.getInstance().getBaseContacts().get(i)
					.getName();
			List<PinyinUnit> pinyinUnit = ContactsHelper.getInstance()
					.getBaseContacts().get(i).getNamePinyinUnits();
			Log.i(TAG,
					"++++++++++++++++++++++++++++++:name=[" + name + "]"
							+ "firstCharacter=["
							+ PinyinUtil.getFirstCharacter(pinyinUnit) + "]"
							+ "firstLetter=["
							+ PinyinUtil.getFirstLetter(pinyinUnit) + "]"
							+ "+++++++++++++++++++++++++++++");
			int pinyinUnitCount = pinyinUnit.size();
			for (int j = 0; j < pinyinUnitCount; j++) {
				PinyinUnit pyUnit = pinyinUnit.get(j);
				Log.i(TAG, "j=" + j + ",isPinyin[" + pyUnit.isPinyin()
						+ "],startPosition=[" + pyUnit.getStartPosition() + "]");
				List<PinyinBaseUnit> stringIndex = pyUnit
						.getPinyinBaseUnitIndex();
				int stringIndexLength = stringIndex.size();
				for (int k = 0; k < stringIndexLength; k++) {
					Log.i(TAG, "k=" + k + "["
							+ stringIndex.get(k).getOriginalString() + "]"
							+ "[" + stringIndex.get(k).getPinyin() + "]+["
							+ stringIndex.get(k).getNumber() + "]");
				}
			}
		}
	}

	private void initContactsHelper() {
		mContext = T9SearchApplication.getContextObject();
		setContactsChanged(true);
		if (null == mBaseContacts) {
			mBaseContacts = new ArrayList<Contacts>();
		} else {
			mBaseContacts.clear();
		}

		if (null == mSearchContacts) {
			mSearchContacts = new ArrayList<Contacts>();
		} else {
			mSearchContacts.clear();
		}

		if (null == mFirstNoSearchResultInput) {
			mFirstNoSearchResultInput = new StringBuffer();
		} else {
			mFirstNoSearchResultInput.delete(0,
					mFirstNoSearchResultInput.length());
		}
	}

	/*
	 * private void registerContentObserver(){ if(null==mContentObserver){
	 * mContentObserver=new ContentObserver(mContactsHandler) {
	 * 
	 * @Override public void onChange(boolean selfChange) {
	 * setContactsChanged(true); if(null!=mOnContactsChanged){
	 * Log.i("ActivityTest"
	 * ,"mOnContactsChanged mContactsChanged="+mContactsChanged);
	 * mOnContactsChanged.onContactsChanged(); } super.onChange(selfChange); }
	 * 
	 * }; }
	 * 
	 * if(null!=mContext){
	 * mContext.getContentResolver().registerContentObserver(
	 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true,
	 * mContentObserver); } }
	 */
	/*
	 * private void unregisterContentObserver(){ if(null!=mContentObserver){
	 * if(null!=mContext){
	 * mContext.getContentResolver().unregisterContentObserver
	 * (mContentObserver); } } }
	 */

	private boolean isSearching() {
		return (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING);
	}

	@SuppressLint("DefaultLocale")
	private List<Contacts> loadContacts(Context context) {

		List<Contacts> contacts = new ArrayList<Contacts>();
		Contacts cs = null;
		Cursor cursor = null;
		try {

			cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, "sort_key");
			String sortkey = null;
			while (cursor.moveToNext()) {

				String displayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				// String nameSortKey=cursor
				// .getString(cursor
				// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.);
				// sortkey = cursor.getString(cursor
				// .getColumnIndex("sort_key"));
				//
				// Log.i(TAG, "sortkey=["+sortkey+"]");

				cs = new Contacts(displayName, phoneNumber);

				PinyinUtil.chineseStringToPinyinUnit(cs.getName(),
						cs.getNamePinyinUnits());
				sortkey = PinyinUtil.getSortKey(cs.getNamePinyinUnits())
						.toUpperCase();
				cs.setSortKey(praseSortKey(sortkey));
				// Log.i(TAG, "sortkey=["+cs.getSortKey()+"]");

				contacts.add(cs);
			}

			// Collections.sort(contacts, Contacts.mAscComparator);
		} catch (Exception e) {

		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}

		return contacts;
	}

	private void parseContacts(List<Contacts> contacts) {
		if (null == contacts || contacts.size() < 1) {
			if (null != mOnContactsLoad) {
				mOnContactsLoad.onContactsLoadFailed();
			}
			return;
		}

		for (Contacts contact : contacts) {
			if (!mBaseContacts.contains(contact)) {
				mBaseContacts.add(contact);
			}
		}

		if (null != mOnContactsLoad) {
			parseT9InputSearchContacts(null);
			mOnContactsLoad.onContactsLoadSuccess();
		}

		return;
	}

	private String praseSortKey(String sortKey) {
		if (null == sortKey || sortKey.length() <= 0) {
			return null;
		}

		if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
				|| (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
			return sortKey;
		}

		return String.valueOf(QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER)
				+ sortKey;
	}
}
