package com.handsomezhou.pinyinsearchdemo.util;

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
import android.text.TextUtils;
import android.util.Log;

import com.handsomezhou.pinyinsearchdemo.main.PinyinSearchApplication;
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
	//private List<Contacts> mBaseContacts = null; // The basic data used for the search
	private List<List<Contacts>> mBaseContacts=null;// The basic data used for the search
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
	private AsyncTask<Object, Object, List<List<Contacts>>> mLoadTask = null;
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

	public List<List<Contacts>> getBaseContacts() {
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
		if (true == isSearching()) {
			return false;
		}

		if (false == isContactsChanged()) {
			return false;
		}

		mLoadTask = new AsyncTask<Object, Object,List<List<Contacts>>>() {

			@Override
			protected List<List<Contacts>> doInBackground(Object... params) {
				return loadContacts(mContext);
			}

			@Override
			protected void onPostExecute(List<List<Contacts>> result) {
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
		List<Contacts> mSearchByNameContacts=new ArrayList<Contacts>();
		List<Contacts> mSearchByPhoneNumberContacts=new ArrayList<Contacts>();

		if (null == search) {// add all base data to search
			if (null != mSearchContacts) {
				mSearchContacts.clear();
			} else {
				mSearchContacts = new ArrayList<Contacts>();
			}

			for(int i=0; i<mBaseContacts.size(); i++){
				for(int j=0; j<mBaseContacts.get(i).size(); j++){
					
					mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByNull);
					mBaseContacts.get(i).get(j).clearMatchKeywords();
					mBaseContacts.get(i).get(j).setMatchStartIndex(-1);
					mBaseContacts.get(i).get(j).setMatchLength(0);
					if(j==0){
						mSearchContacts.add(mBaseContacts.get(i).get(j));
					}else{
						if(false==mBaseContacts.get(i).get(j).isHideMultipleContacts()){
							mSearchContacts.add(mBaseContacts.get(i).get(j));
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

		int contactsListCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by name pinyin
		 * characters(org name->name pinyin characters) ('0'~'9','*','#')
		 * (2)Search by org name ('0'~'9','*','#') 2:Search by phone number
		 * ('0'~'9','*','#')
		 */
		for (int i = 0; i < contactsListCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i).get(0).getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get Chinese KeyWords.Ofcourse it's maybe not Chinese characters.
			String name = mBaseContacts.get(i).get(0).getName();
			if (true == T9MatchPinyinUnits.matchPinyinUnits(pinyinUnits, name,search, chineseKeyWord)) {// search by NamePinyinUnits;
				for(int j=0; j<mBaseContacts.get(i).size(); j++){
					mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByName);
					mBaseContacts.get(i).get(j).setMatchKeywords(chineseKeyWord.toString());
					mBaseContacts.get(i).get(j).setMatchStartIndex(mBaseContacts.get(i).get(0).getName().indexOf(mBaseContacts.get(i).get(0).getMatchKeywords().toString()));
					mBaseContacts.get(i).get(j).setMatchLength(mBaseContacts.get(i).get(0).getMatchKeywords().length());
					mSearchByNameContacts.add(mBaseContacts.get(i).get(j));
				}
				chineseKeyWord.delete(0, chineseKeyWord.length());
				
				continue;
			} else {
				for(int j=0;j<mBaseContacts.get(i).size();j++){
					if(mBaseContacts.get(i).get(j).getPhoneNumber().contains(search)){// search by phone number
						mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByPhoneNumber);
						mBaseContacts.get(i).get(j).setMatchKeywords(search);
						mBaseContacts.get(i).get(j).setMatchStartIndex(mBaseContacts.get(i).get(j).getPhoneNumber().indexOf(search));
						mBaseContacts.get(i).get(j).setMatchLength(search.length());
						mSearchByPhoneNumberContacts.add(mBaseContacts.get(i).get(j));
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

			for(int i=0; i<mBaseContacts.size(); i++){
				for(int j=0; j<mBaseContacts.get(i).size(); j++){
					
					mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByNull);
					mBaseContacts.get(i).get(j).clearMatchKeywords();
					mBaseContacts.get(i).get(j).setMatchStartIndex(-1);
					mBaseContacts.get(i).get(j).setMatchLength(0);
					if(j==0){
						mSearchContacts.add(mBaseContacts.get(i).get(j));
					}else{
						if(false==mBaseContacts.get(i).get(j).isHideMultipleContacts()){
							mSearchContacts.add(mBaseContacts.get(i).get(j));
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

		int contactsListCount = mBaseContacts.size();

		/**
		 * search process: 1:Search by name (1)Search by original name (2)Search
		 * by name pinyin characters(original name->name pinyin characters)
		 * 2:Search by phone number
		 */
		for (int i = 0; i < contactsListCount; i++) {

			List<PinyinUnit> pinyinUnits = mBaseContacts.get(i).get(0).getNamePinyinUnits();
			StringBuffer chineseKeyWord = new StringBuffer();// In order to get Chinese KeyWords.Ofcourse it's maybe not Chinese characters.
			
			String name = mBaseContacts.get(i).get(0).getName();
			if (true == QwertyMatchPinyinUnits.matchPinyinUnits(pinyinUnits,name, search, chineseKeyWord)) {// search by NamePinyinUnits;
				for(int j=0; j<mBaseContacts.get(i).size(); j++){
					mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByName);
					mBaseContacts.get(i).get(j).setMatchKeywords(chineseKeyWord.toString());
					mBaseContacts.get(i).get(j).setMatchStartIndex(mBaseContacts.get(i).get(0).getName().indexOf(mBaseContacts.get(i).get(0).getMatchKeywords().toString()));
					mBaseContacts.get(i).get(j).setMatchLength(mBaseContacts.get(i).get(0).getMatchKeywords().length());
					mSearchContacts.add(mBaseContacts.get(i).get(j));
				}
				chineseKeyWord.delete(0, chineseKeyWord.length());
				
				continue;
			} else {
				for(int j=0;j<mBaseContacts.get(i).size();j++){
					if(mBaseContacts.get(i).get(j).getPhoneNumber().contains(search)){// search by phone number
						mBaseContacts.get(i).get(j).setSearchByType(SearchByType.SearchByPhoneNumber);
						mBaseContacts.get(i).get(j).setMatchKeywords(search);
						mBaseContacts.get(i).get(j).setMatchStartIndex(mBaseContacts.get(i).get(j).getPhoneNumber().indexOf(search));
						mBaseContacts.get(i).get(j).setMatchLength(search.length());
						mSearchContacts.add(mBaseContacts.get(i).get(j));
					}
				}
				continue;
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
		int contactsListCount = ContactsHelper.getInstance().getBaseContacts()
				.size();
		for (int i = 0; i < contactsListCount; i++) {
			for(int m=0; m<ContactsHelper.getInstance().getBaseContacts().get(i).size(); m++){
				Log.i(TAG, "======================================================================");
				String name = ContactsHelper.getInstance().getBaseContacts().get(i).get(m).getName();
				List<PinyinUnit> pinyinUnit = ContactsHelper.getInstance()
						.getBaseContacts().get(i).get(m).getNamePinyinUnits();
				Log.i(TAG,
						"++++++++++++++++++++++++++++++:name=[" + name + "] phoneNumber"+ContactsHelper.getInstance().getBaseContacts().get(i).get(m).getPhoneNumber()
								+ContactsHelper.getInstance().getBaseContacts().get(i).get(m).isHideMultipleContacts()+ "firstCharacter=["
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
	}

	private void initContactsHelper() {
		mContext = PinyinSearchApplication.getContextObject();
		setContactsChanged(true);
		if (null == mBaseContacts) {
			mBaseContacts = new ArrayList<List<Contacts>>();
		} else {
			clearBaseContacts(mBaseContacts);
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
	
	private void clearBaseContacts(List<List<Contacts>> contaLists){
		if(null==contaLists){
			return;
		}
		
		for(int i=0; i<contaLists.size(); i++){
			contaLists.get(i).clear();
		}
		contaLists.clear();
	}

	private boolean isSearching() {
		return (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING);
	}

	@SuppressLint("DefaultLocale")
	private List<List<Contacts>> loadContacts(Context context) {
		List<List<Contacts>> kanjiStartContacts = new ArrayList<List<Contacts>>();
		HashMap<String, List<Contacts>> kanjiStartContactsHashMap=new HashMap<String, List<Contacts>>();
		
		List<List<Contacts>>  nonKanjiStartContacts = new ArrayList<List<Contacts>>();
		HashMap<String, List<Contacts>> nonKanjiStartContactsHashMap=new HashMap<String, List<Contacts>>();
		
		List<List<Contacts>> contactsLists=new ArrayList<List<Contacts>>();
		
		List<Contacts> contactList=null;
		
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
					contactList=kanjiStartContactsHashMap.get(id);
					Contacts cts=addMulitpleContact(contactList, phoneNumber);
					if(null!=cts){
						contactList.add(cts);
					}
				}else if(true==nonKanjiStartContactsExist){
					contactList=nonKanjiStartContactsHashMap.get(id);
					Contacts cts=addMulitpleContact(contactList, phoneNumber);
					if(null!=cts){
						contactList.add(cts);
					}
				}else{
					contactList=new ArrayList<Contacts>();
					Contacts cts = new Contacts(id,displayName, phoneNumber);
					PinyinUtil.chineseStringToPinyinUnit(cts.getName(),
							cts.getNamePinyinUnits());
					sortkey = PinyinUtil.getSortKey(cts.getNamePinyinUnits())
							.toUpperCase();
					cts.setSortKey(praseSortKey(sortkey));
					boolean isKanji=PinyinUtil.isKanji(cts.getName().charAt(0));
					contactList.add(cts);
					
					if(true==isKanji){
						kanjiStartContactsHashMap.put(id, contactList);
					}else{
						nonKanjiStartContactsHashMap.put(id, contactList);
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
		
		//contactsLists.addAll(nonKanjiStartContacts);
		contactsLists.addAll(kanjiStartContacts);
	
		//merge nonKanjiStartContacts and kanjiStartContacts
		int lastIndex=0;
		boolean shouldBeAdd=false;
		for(int i=0; i<nonKanjiStartContacts.size(); i++){
			String nonKanfirstLetter=PinyinUtil.getFirstLetter(nonKanjiStartContacts.get(i).get(0).getNamePinyinUnits());
			//Log.i(TAG, "nonKanfirstLetter=["+nonKanfirstLetter+"]");
			int j=0;
			for(j=0+lastIndex; j<contactsLists.size(); j++){
				String firstLetter=PinyinUtil.getFirstLetter(contactsLists.get(j).get(0).getNamePinyinUnits());
				lastIndex++;
				if(firstLetter.charAt(0)>nonKanfirstLetter.charAt(0)){
					shouldBeAdd=true;
					break;
				}else{
					shouldBeAdd=false;
				}
			}
			
			if(lastIndex>=contactsLists.size()){
				lastIndex++;
				shouldBeAdd=true;
				//Log.i(TAG, "lastIndex="+lastIndex);
			}
			
			if(true==shouldBeAdd){
				contactsLists.add(j, nonKanjiStartContacts.get(i));
				shouldBeAdd=false;
			}
		}
	
		long endLoadTime=System.currentTimeMillis();
		Log.i(TAG, "endLoadTime-startLoadTime=["+(endLoadTime-startLoadTime)+"] contactsLists.size()="+contactsLists.size());
		
		/*for(int i=0; i<contactsLists.size(); i++){
			for(int j=0; j<contactsLists.get(i).size();j++){
				Log.i(TAG, "****************************************");
				Log.i(TAG, "name["+contactsLists.get(i).get(j).getName()+"]phoneNumber["+contactsLists.get(i).get(j).getPhoneNumber());
			}
		}*/
		return contactsLists;
	}
	
	private Contacts addMulitpleContact(final List<Contacts> contactList, String phoneNumber){
		do{
			if((TextUtils.isEmpty(phoneNumber))||(null==contactList)||(contactList.size()<=0)){
				break;
			}
			
			if(contactList.get(0).getPhoneNumber().equals(phoneNumber)){
				break;
			}
			
			int i=0; 
			for(i=0; i<contactList.size();i++){
				if(contactList.get(i).getPhoneNumber().equals(phoneNumber)){
					break;
				}
			}
			
			Contacts contacts=null;
			if(i>=contactList.size()){
				Contacts cs=contactList.get(contactList.size()-1);
				contacts=new Contacts(cs.getId(), cs.getName(),phoneNumber);
				contacts.setSortKey(cs.getSortKey());
				contacts.setNamePinyinUnits(cs.getNamePinyinUnits());// not deep copy
				contacts.setFirstMultipleContacts(false);
				contacts.setHideMultipleContacts(true);
				contacts.setBelongMultipleContactsPhone(true);
				cs.setBelongMultipleContactsPhone(true);
				cs.setNextContacts(contacts);
			}
			return contacts;
		}while(false);
		
		return null;
	}
	
	private void parseContacts(List<List<Contacts>> contactsLists) {
		if (null == contactsLists || contactsLists.size() < 1) {
			if (null != mOnContactsLoad) {
				mOnContactsLoad.onContactsLoadFailed();
			}
			return;
		}

		for (List<Contacts> contactList : contactsLists) {
			if (!mBaseContacts.contains(contactList)) {
				mBaseContacts.add(contactList);
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
