package com.metatroid.minimal.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.Browser;

public class DataProvider extends ContentProvider {
	private static final String AUTHORITY = "com.metatroid.minimal.bookmarks.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri CONTENT_URI_MESSAGES = CONTENT_URI.buildUpon().appendEncodedPath("data").build();
	public enum DataProviderColumns {
		_id, title, link, icon
	}
	public static final String[] PROJECTION_APPWIDGETS = new String[] { DataProviderColumns._id.toString(),
		DataProviderColumns.title.toString(), DataProviderColumns.link.toString(), DataProviderColumns.icon.toString()};
	private static Context ctx = null;
	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int URI_DATA = 0;
	
	static {
		URI_MATCHER.addURI(AUTHORITY, "data/*", URI_DATA);
	}
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean onCreate() {
		ctx = getContext();

		return false;
	}
	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		switch (URI_MATCHER.match(arg0)) {

		case URI_DATA:

			MatrixCursor mc = loadNewData(this, arg1);

			
			return mc;

		default:
			throw new IllegalStateException("Unrecognized URI:" + arg0);
		}
	}
	public static void notifyDatabaseModification(int widgetId) {

		Uri widgetUri = CONTENT_URI_MESSAGES.buildUpon().appendEncodedPath(Integer.toString(widgetId)).build();

		

		ctx.getContentResolver().notifyChange(widgetUri, null);

	}
	public static MatrixCursor loadNewData(ContentProvider mcp, String[] projection) {
		ArrayList<HashMap<String,String>> books = new ArrayList<HashMap<String,String>>();
		MatrixCursor ret = new MatrixCursor(projection);

		try {
			
			String[] p = new String[] {
		       		Browser.BookmarkColumns.TITLE
		       		, Browser.BookmarkColumns.URL
		        };
			Cursor c = ctx.getContentResolver().query(android.provider.Browser.BOOKMARKS_URI, p, android.provider.Browser.BookmarkColumns.BOOKMARK , null, null);
			c.moveToFirst();
			int titleIdx = c.getColumnIndex(Browser.BookmarkColumns.TITLE);
	        int urlIdx = c.getColumnIndex(Browser.BookmarkColumns.URL);
	        while (c.isAfterLast() == false) {
	        	//view.append("n" + c.getString(titleIdx));
	        	//view.append("n" + c.getString(urlIdx));
	        	HashMap<String, String> mHash = new HashMap<String,String>();
	        	mHash.put("title", c.getString(titleIdx));
	        	mHash.put("url", c.getString(urlIdx));
	        	books.add(mHash);
	        	c.moveToNext();
	        }
	        SharedPreferences prefs = ctx.getSharedPreferences("prefs", 0);
			SharedPreferences.Editor edit = prefs.edit();
			for (int j = 0; j < books.size(); j++) {
				edit.putString("link"+j, books.get(j).get("url"));
				edit.commit();

				Object[] values = new Object[projection.length];

				for (int i = 0, count = projection.length; i < count; i++) {
					String column = projection[i];
					if (DataProviderColumns._id.toString().equals(column)) {
						values[i] = i;
					} else if (DataProviderColumns.title.toString().equals(column)) {
						values[i] = books.get(j).get("title");
					} else if (DataProviderColumns.link.toString().equals(column)) {
						values[i] = books.get(j).get("url");
					} else if (DataProviderColumns.icon.toString().equals(column)) {
						values[i] = R.drawable.unknown;
					}

				}
				ret.addRow(values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
