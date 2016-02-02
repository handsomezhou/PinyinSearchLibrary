package com.handsomezhou.contactssearch.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.provider.ContactsContract;
import android.util.Log;

import com.handsomezhou.contactssearch.application.ContacstSearchApplication;
import com.handsomezhou.contactssearch.model.Contacts;
import com.handsomezhou.contactssearch.model.Contacts.SearchByType;
import com.handsomezhou.contactssearch.view.QuickAlphabeticBar;
import com.pinyinsearch.model.PinyinBaseUnit;
import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.model.PinyinUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyUtil;
import com.pinyinsearch.util.T9Util;


public class ContactsHelper {
	private static final String TAG = "ContactsHelper";
	private Context mContext;
	private static ContactsHelper mInstance = null;
	private List<Contacts> mBaseContacts = null; // The basic data used for the search
	private List<Contacts> mSearchContacts = null; // The search results from the basic data
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
	private HashMap<String, Contacts> mSelectedContactsHashMap=null; //(id+phoneNumber)as key
	
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
	
	
	public HashMap<String, Contacts> getSelectedContacts() {
		return mSelectedContactsHashMap;
	}

	public void setSelectedContacts(HashMap<String, Contacts> selectedContacts) {
		mSelectedContactsHashMap = selectedContacts;
	}
	
	/**
	 * Provides an function to start load contacts
	 * 
	 * @return start load success return true, otherwise return false
	 */
	public boolean startLoadContacts() {
		if (true == isLoading()) {
			return false;
		}

		if (false == isContactsChanged()) {
			return false;
		}

		mLoadTask = new AsyncTask<Object, Object,List<Contacts>>() {

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
	 * @param keyword
	 *            (valid characters include:'0'~'9','*','#')
	 * @return void
	 *
	 * 
	 */
	public void t9InputSearch(String keyword) {
		List<Contacts> mSearchByNameContacts=new ArrayList<Contacts>();
		List<Contacts> mSearchByPhoneNumberContacts=new ArrayList<Contacts>();

		if (null == keyword) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for (int i=0; i<mBaseContacts.size(); i++) {
				Contacts currentContacts=null;
				for(currentContacts=mBaseContacts.get(i); null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					currentContacts.setSearchByType(SearchByType.SearchByNull);
					currentContacts.clearMatchKeywords();
					currentContacts.setMatchStartIndex(-1);
					currentContacts.setMatchLength(0);
					if(true==currentContacts.isFirstMultipleContacts()){
						mSearchContacts.add(currentContacts);
					}else{
						if(false==currentContacts.isHideMultipleContacts()){
							mSearchContacts.add(currentContacts);
						}
					}
				}
			}
			
			//mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (keyword.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ keyword.length()
								+ "["
								+ keyword + "]");
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

			PinyinSearchUnit namePinyinSearchUnit= mBaseContacts.get(i).getNamePinyinSearchUnit();
			if (true == T9Util.match(namePinyinSearchUnit,keyword)) {// search by name;
				
				Contacts currentContacts=null;
				Contacts firstContacts=null;
				for(currentContacts=mBaseContacts.get(i),firstContacts=currentContacts; null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					currentContacts.setSearchByType(SearchByType.SearchByName);
					currentContacts.setMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
					currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
					currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
					mSearchByNameContacts.add(currentContacts);
				}
				
				continue;
			} else {
				Contacts currentContacts=null;
				for(currentContacts=mBaseContacts.get(i); null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					if(currentContacts.getPhoneNumber().contains(keyword)){// search by phone number
						currentContacts.setSearchByType(SearchByType.SearchByPhoneNumber);
						currentContacts.setMatchKeywords(keyword);
						currentContacts.setMatchStartIndex(currentContacts.getPhoneNumber().indexOf(keyword));
						currentContacts.setMatchLength(keyword.length());
						mSearchByPhoneNumberContacts.add(currentContacts);
					}
				}
				continue;

			}
		}
		
		if(mSearchByNameContacts.size()>0){
			Collections.sort(mSearchByNameContacts, Contacts.mSearchComparator);
		}
		if(mSearchByPhoneNumberContacts.size()>0){
			Collections.sort(mSearchByPhoneNumberContacts, Contacts.mSearchComparator);
		}
		
		mSearchContacts.clear();
		mSearchContacts.addAll(mSearchByNameContacts);
		mSearchContacts.addAll(mSearchByPhoneNumberContacts);
		
		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(keyword);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
			} else {

			}
		}

	}

	/**
	 * @description search base data according to string parameter
	 * @param keyword
	 * @return void
	 */
	public void qwertySearch(String keyword) {
		if (null == keyword) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for(int i=0; i<mBaseContacts.size(); i++){
				Contacts currentContacts=null;
				for(currentContacts=mBaseContacts.get(i); null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					currentContacts.setSearchByType(SearchByType.SearchByNull);
					currentContacts.clearMatchKeywords();
					currentContacts.setMatchStartIndex(-1);
					currentContacts.setMatchLength(0);
					if(true==currentContacts.isFirstMultipleContacts()){
						mSearchContacts.add(currentContacts);
					}else{
						if(false==currentContacts.isHideMultipleContacts()){
							mSearchContacts.add(currentContacts);
						}
					}
				}
			}

			//mSearchContacts.addAll(mBaseContacts);
			mFirstNoSearchResultInput.delete(0,mFirstNoSearchResultInput.length());
			Log.i(TAG, "null==search,mFirstNoSearchResultInput.length()="+ mFirstNoSearchResultInput.length());
			return;
		}

		if (mFirstNoSearchResultInput.length() > 0) {
			if (keyword.contains(mFirstNoSearchResultInput.toString())) {
				Log.i(TAG,
						"no need  to search,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
				return;
			} else {
				Log.i(TAG,
						"delete  mFirstNoSearchResultInput, null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length()
								+ "["
								+ mFirstNoSearchResultInput.toString()
								+ "]"
								+ ";searchlen="
								+ keyword.length()
								+ "["
								+ keyword + "]");
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
			PinyinSearchUnit namePinyinSearchUnit= mBaseContacts.get(i).getNamePinyinSearchUnit();

			if (true == QwertyUtil.match(namePinyinSearchUnit,keyword)) {// search by name;
				Contacts currentContacts=null;
				Contacts firstContacts=null;
				for(currentContacts=mBaseContacts.get(i),firstContacts=currentContacts; null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					currentContacts.setSearchByType(SearchByType.SearchByName);
					currentContacts.setMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
					currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
					currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
					mSearchContacts.add(currentContacts);
				}
				
				continue;
			} else {
				Contacts currentContacts=null;
				for(currentContacts=mBaseContacts.get(i); null!=currentContacts; currentContacts=currentContacts.getNextContacts()){
					if(currentContacts.getPhoneNumber().contains(keyword)){// search by phone number
						currentContacts.setSearchByType(SearchByType.SearchByPhoneNumber);
						currentContacts.setMatchKeywords(keyword);
						currentContacts.setMatchStartIndex(currentContacts.getPhoneNumber().indexOf(keyword));
						currentContacts.setMatchLength(keyword.length());
						mSearchContacts.add(currentContacts);
					}
				}
				continue;
			}
		}

		if (mSearchContacts.size() <= 0) {
			if (mFirstNoSearchResultInput.length() <= 0) {
				mFirstNoSearchResultInput.append(keyword);
				Log.i(TAG,
						"no search result,null!=search,mFirstNoSearchResultInput.length()="
								+ mFirstNoSearchResultInput.length() + "["
								+ mFirstNoSearchResultInput.toString() + "]"
								+ ";searchlen=" + keyword.length() + "["
								+ keyword + "]");
			} else {

			}
		}else{
			Collections.sort(mSearchContacts, Contacts.mSearchComparator);
		}

	}

	public void clearSelectedContacts(){
		if(null==mSelectedContactsHashMap){
			mSelectedContactsHashMap=new HashMap<String, Contacts>();
			return;
		}
		
		mSelectedContactsHashMap.clear();
	}
	
	public boolean addSelectedContacts(Contacts contacts){
		do{
			if(null==contacts){
				break;
			}
			
			if(null==mSelectedContactsHashMap){
				mSelectedContactsHashMap=new HashMap<String, Contacts>();
			}
			
			mSelectedContactsHashMap.put(getSelectedContactsKey(contacts), contacts);
			
			return true;
		}while(false);
		
		return false;
	
	}
	
	public void removeSelectedContacts(Contacts contacts){
		if(null==contacts){
			return;
		}
		
		if(null==mSelectedContactsHashMap){
			return;
		}
		
		mSelectedContactsHashMap.remove(getSelectedContactsKey(contacts));
	}
	
	// just for debug
	public void showContactsInfo() {
		int contactsCount = ContactsHelper.getInstance().getBaseContacts().size();
		for (int i = 0; i < contactsCount; i++) {
			Contacts currentCoutacts=null;
			for(currentCoutacts=ContactsHelper.getInstance().getBaseContacts().get(i);null!=currentCoutacts; currentCoutacts=currentCoutacts.getNextContacts()){
				Log.i(TAG, "======================================================================");
				String name = currentCoutacts.getName();
				//List<PinyinUnit> pinyinUnit = currentCoutacts.getPinyinSearchUnit().getPinyinUnits();//.getNamePinyinUnits();
				PinyinSearchUnit pinyinSearchUnit= currentCoutacts.getNamePinyinSearchUnit();//.getNamePinyinUnits();
				Log.i(TAG,
						"++++++++++++++++++++++++++++++:name=[" + name + "] phoneNumber"+currentCoutacts.getPhoneNumber()
								+currentCoutacts.isHideMultipleContacts()+ "firstCharacter=["
								+ PinyinUtil.getFirstCharacter(pinyinSearchUnit) + "]"
								+ "firstLetter=["
								+ PinyinUtil.getFirstLetter(pinyinSearchUnit) + "]"
								+ "+++++++++++++++++++++++++++++");
				int pinyinUnitCount = pinyinSearchUnit.getPinyinUnits().size();
				for (int j = 0; j < pinyinUnitCount; j++) {
					PinyinUnit pyUnit = pinyinSearchUnit.getPinyinUnits().get(j);
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
	}

	private void initContactsHelper() {
		mContext = ContacstSearchApplication.getContextObject();
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
		
		if(null==mSelectedContactsHashMap){
			mSelectedContactsHashMap=new HashMap<String, Contacts>();
		}else{
			mSelectedContactsHashMap.clear();
		}
	}

	private boolean isLoading() {
		return (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING);
	}

	@SuppressLint("DefaultLocale")
	private List<Contacts> loadContacts(Context context) {
		List<Contacts> kanjiStartContacts = new ArrayList<Contacts>();
		HashMap<String, Contacts> kanjiStartContactsHashMap=new HashMap<String, Contacts>();
		
		List<Contacts>  nonKanjiStartContacts = new ArrayList<Contacts>();
		HashMap<String, Contacts> nonKanjiStartContactsHashMap=new HashMap<String,Contacts>();
		
		List<Contacts> contacts=new ArrayList<Contacts>();
		
		Contacts cs=null;
		Cursor cursor = null;
		String sortkey = null;
		long startLoadTime=System.currentTimeMillis();
		String[] projection=new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
		try {

			cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,null, null, "sort_key");
			
			int idColumnIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
			int dispalyNameColumnIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int numberColumnIndex=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			while (cursor.moveToNext()) {

				String id=cursor.getString(idColumnIndex);
				String displayName = cursor.getString(dispalyNameColumnIndex);
				String phoneNumber = cursor.getString(numberColumnIndex);
			//	Log.i(TAG, "id=["+id+"]name=["+displayName+"]"+"number=["+phoneNumber+"]");
				
				boolean kanjiStartContactsExist=kanjiStartContactsHashMap.containsKey(id);
				boolean nonKanjiStartContactsExist=nonKanjiStartContactsHashMap.containsKey(id);
				
				if(true==kanjiStartContactsExist){
					cs=kanjiStartContactsHashMap.get(id);
					Contacts.addMultipleContact(cs, phoneNumber);
				}else if(true==nonKanjiStartContactsExist){
					cs=nonKanjiStartContactsHashMap.get(id);
					Contacts.addMultipleContact(cs, phoneNumber);
				}else{
					
					cs = new Contacts(id,displayName, phoneNumber);
					
					PinyinUtil.parse(cs.getNamePinyinSearchUnit());
					sortkey = PinyinUtil.getSortKey(cs.getNamePinyinSearchUnit()).toUpperCase();
					cs.setSortKey(praseSortKey(sortkey));
					boolean isKanji=PinyinUtil.isKanji(cs.getName().charAt(0));
					
					if(true==isKanji){
						kanjiStartContactsHashMap.put(id, cs);
					}else{
						nonKanjiStartContactsHashMap.put(id, cs);
					}
					
				}
			}
		} catch (Exception e) {

		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		
		kanjiStartContacts.addAll(kanjiStartContactsHashMap.values());
		Collections.sort(kanjiStartContacts, Contacts.mAscComparator);
		
		nonKanjiStartContacts.addAll(nonKanjiStartContactsHashMap.values());
		Collections.sort(nonKanjiStartContacts, Contacts.mAscComparator);
		
		//contacts.addAll(nonKanjiStartContacts);
		contacts.addAll(kanjiStartContacts);
	
		//merge nonKanjiStartContacts and kanjiStartContacts
		int lastIndex=0;
		boolean shouldBeAdd=false;
		for(int i=0; i<nonKanjiStartContacts.size(); i++){
			String nonKanfirstLetter=PinyinUtil.getFirstLetter(nonKanjiStartContacts.get(i).getNamePinyinSearchUnit());
			//Log.i(TAG, "nonKanfirstLetter=["+nonKanfirstLetter+"]");
			int j=0;
			for(j=0+lastIndex; j<contacts.size(); j++){
				String firstLetter=PinyinUtil.getFirstLetter(contacts.get(j).getNamePinyinSearchUnit());
				lastIndex++;
				if(firstLetter.charAt(0)>nonKanfirstLetter.charAt(0)){
					shouldBeAdd=true;
					break;
				}else{
					shouldBeAdd=false;
				}
			}
			
			if(lastIndex>=contacts.size()){
				lastIndex++;
				shouldBeAdd=true;
				//Log.i(TAG, "lastIndex="+lastIndex);
			}
			
			if(true==shouldBeAdd){
				contacts.add(j, nonKanjiStartContacts.get(i));
				shouldBeAdd=false;
			}
		}
	
		long endLoadTime=System.currentTimeMillis();
		Log.i(TAG, "endLoadTime-startLoadTime=["+(endLoadTime-startLoadTime)+"] contacts.size()="+contacts.size());
		
		/*for (int i = 0; i < contacts.size(); i++) {
			Log.i(TAG, "****************************************");
			Contacts currentContacts = contacts.get(i);
			while (null != currentContacts) {
				Log.i(TAG, "name[" + currentContacts.getName()+"]phoneNumber[" + currentContacts.getPhoneNumber()+"]");
				currentContacts = currentContacts.getNextContacts();
			}
		}*/
		
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
	
	/**
	 * key=id+phoneNumber
	 * */
	private String getSelectedContactsKey(Contacts contacts){
		if(null==contacts){
			return null;
		}
		
		return contacts.getId()+contacts.getPhoneNumber();
	}	
}
