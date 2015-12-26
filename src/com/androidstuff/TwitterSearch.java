package com.androidstuff;

import java.util.Arrays;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
				alertByToast("Both search query and tag fields needs to be filled");
			}
		}

		private void alertByToast(String alertMessage) {
			final Toast toast = Toast.makeText(getApplicationContext(), alertMessage, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
		}
	};

	private OnClickListener deleteTagButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final ViewGroup parent = (ViewGroup) v.getParent();
			removeRowFromUI(parent);
			removeTagFromDictionary(parent);
		}

		private void removeTagFromDictionary(final ViewGroup parent) {
			final Button tagButton = (Button) parent.findViewById(R.id.newTagButton);
			final String tagToBeDeleted = tagButton.getText().toString();
			removeFromDictionary(tagToBeDeleted);
		}

		private void removeRowFromUI(final ViewGroup parent) {
			final ViewGroup savedTagsTableLayout = (ViewGroup) findViewById(R.id.savedTagsTableLayout);
			savedTagsTableLayout.removeView(parent);
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

		refreshTagList();
	}

	private void saveTagAndRefreshUI() {
		String searchQuery = getSearchQueryEditText().getText().toString(),
				tag = getTagSearchQueryEditText().getText().toString();
		boolean isTagAlreadyPresent = isTagAlreadyPresent(tag);
		addToDictionary(searchQuery, tag);

		if(isTagAlreadyPresent) {
			return;
		}
		refreshTagList(tag);
	}

	private void addToDictionary(String searchQuery, String tag) {
		Editor preferencesEditor = savedSearches.edit();
		preferencesEditor.putString(tag, searchQuery);
		preferencesEditor.apply();
	}

	private void removeFromDictionary(final String tagToBeDeleted) {
		final Editor editor = savedSearches.edit();
		editor.remove(tagToBeDeleted);
		editor.apply();
	}

	private void refreshTagList() {
		String[] tags = fetchSortedTags();
		for (int index = 0; index < tags.length; index++) {
			addTagRowInGUI(tags[index], index);
		}
	}

	private void refreshTagList(String newTag) {
		String[] tags = fetchSortedTags();
		addTagRowInGUI(newTag, Arrays.binarySearch(tags, newTag));
	}

	private String[] fetchSortedTags() {
		final Set<String> keySet = savedSearches.getAll().keySet();
		Log.i("savedSearches size : ", Integer.toString(keySet.size()));
		String[] tags = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(tags, String.CASE_INSENSITIVE_ORDER);
		return tags;
	}

	private void addTagRowInGUI(String tag, int index) {
		final ViewGroup savedTagsTableLayout = (ViewGroup) findViewById(R.id.savedTagsTableLayout);
		final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View newRow = layoutInflater.inflate(R.layout.new_tag_view_row, savedTagsTableLayout, false);

		getTagButtonIn(newRow).setText(tag); //NOTE: It's relative search within the inflated layout context
		getDeleteButtonIn(newRow).setOnClickListener(deleteTagButtonListener);
		savedTagsTableLayout.addView(newRow, index);
	}

	private Button getDeleteButtonIn(final View newRow) {
		return (Button) newRow.findViewById(R.id.newDeleteButton);
	}

	private Button getTagButtonIn(final View newRow) {
		return (Button) newRow.findViewById(R.id.newTagButton);
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
