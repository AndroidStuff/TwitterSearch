package com.androidstuff;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class TwitterSearch extends Activity {

	private SharedPreferences savedSearches;
	private OnClickListener saveTagButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(hasSearchQuery() && hasTag()) {
				saveTagAndRefreshUI();
				clearSearchQueryInputField();
				clearTagInputField();
			}else {
				//TODO: Throw Alert
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		savedSearches = getSharedPreferences("searches", MODE_PRIVATE);

		//		findViewById(R.id.savedTagsTableLayout);

		Button saveTagButton = (Button) findViewById(R.id.saveTagButton);
		saveTagButton.setOnClickListener(saveTagButtonListener );
	}

	private void saveTagAndRefreshUI() {
		String searchQuery = getSearchQueryEditText().getText().toString(),
				tag = getTagSearchQueryEditText().getText().toString();
		boolean isTagAlreadyPresent = isTagAlreadyPresent(tag);
		Editor preferencesEditor = savedSearches.edit();
		preferencesEditor.putString(tag, searchQuery);
		preferencesEditor.apply();

		if(isTagAlreadyPresent) {
			refreshTagList(tag);
		}
	}

	private void refreshTagList(String tag) {
		String[] tags = savedSearches.getAll().keySet().toArray(new String[0]);
		Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);
		for(int index = 0; index < tags.length; index++) {
			addTagRowInGUI(tags[index], index);
		}
	}

	private void addTagRowInGUI(String tag, int index) {
		final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View newRow = layoutInflater.inflate(R.layout.new_tag_view_row, null);

		((Button) newRow.findViewById(R.id.newTagButton)).setText(tag); //NOTE: It's relative search within the inflated layout context
		( (ViewGroup) findViewById(R.id.savedTagsTableLayout) ).addView(newRow);
	}

	private boolean isTagAlreadyPresent(String tag) {
		return savedSearches.getString(tag, null) != null;
	}

	private boolean hasTag() {
		return getTagSearchQueryEditText().getText().length() > 0;
	}

	private boolean hasSearchQuery() {
		return getSearchQueryEditText().getText().length() > 0;
	}

	private EditText getSearchQueryEditText() {
		return (EditText)findViewById(R.id.searchQueryEditText);
	}

	private EditText getTagSearchQueryEditText() {
		return (EditText)findViewById(R.id.tagSearchQueryEditText);
	}

	private void clearSearchQueryInputField() {
		getSearchQueryEditText().setText("");
	}

	private void clearTagInputField() {
		getTagSearchQueryEditText().setText("");
	}


}
