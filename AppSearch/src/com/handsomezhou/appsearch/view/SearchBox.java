package com.handsomezhou.appsearch.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handsomezhou.appsearch.R;
import com.handsomezhou.appsearch.util.ViewUtil;

public class SearchBox extends LinearLayout {
	private Context mContext;
	/* start: search box */
	private View mSearchBox;

	private EditText mSearchEt;

	private ImageView mDeleteIv;
	/* end: search box */
	private OnSearchBox mOnSearchBox;

	public interface OnSearchBox {
		void onSearchTextChanged(String curCharacter);
	}

	public SearchBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
		initData();
		initListener();
	}

	public OnSearchBox getOnSearchBox() {
		return mOnSearchBox;
	}

	public void setOnSearchBox(OnSearchBox onSearchBox) {
		mOnSearchBox = onSearchBox;
	}

	public EditText getSearchEt() {
		return mSearchEt;
	}

	public void setSearchEt(EditText searchEt) {
		mSearchEt = searchEt;
	}

	public String getSearchEtInput() {
		return mSearchEt.getText().toString();
	}

	private void initView() {
		LayoutInflater.from(mContext).inflate(R.layout.search_box, this);
		mSearchBox = findViewById(R.id.search_box);

		mSearchEt = (EditText) findViewById(R.id.search_edit_text);
		mDeleteIv = (ImageView) findViewById(R.id.delete_image_view);

		return;
	}

	private void initData() {

		return;
	}

	private void initListener() {
		mSearchEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (null != mOnSearchBox) {
					String inputStr=s.toString();
					mOnSearchBox.onSearchTextChanged(inputStr);
					if(TextUtils.isEmpty(inputStr)){
						ViewUtil.hideView(mDeleteIv);
					}else{
						ViewUtil.showView(mDeleteIv);
					}
				}

			}
		});

		mDeleteIv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				delete();
			}
		});

		return;
	}

	private void delete() {
		mSearchEt.setText("");
		ViewUtil.hideView(mDeleteIv);
	}

}
