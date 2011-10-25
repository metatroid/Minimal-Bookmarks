package com.metatroid.minimal.bookmarks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddBookmark extends Activity{
	Intent i;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_bookmark);
		i = getIntent();
		Button done = (Button) findViewById(R.id.addDone);
		done.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText n = (EditText)findViewById(R.id.bookName);
				EditText l = (EditText)findViewById(R.id.bookLink);
				String name = n.getText().toString().trim();
				String link = l.getText().toString().trim();
				if(name.length() > 0 && link.length() > 0){
					final ContentValues bookmarkValues = new ContentValues();
					bookmarkValues.put(Browser.BookmarkColumns.TITLE, name);
					bookmarkValues.put(Browser.BookmarkColumns.URL, link);
					bookmarkValues.put(Browser.BookmarkColumns.BOOKMARK, 1);
					final Uri newBookmark = getContentResolver().insert(Browser.BOOKMARKS_URI, bookmarkValues);
				}
				MinimalBookmarks.onAppWidgetReady(getApplicationContext(), i);
				finish();
			}
			
		});
	}
}