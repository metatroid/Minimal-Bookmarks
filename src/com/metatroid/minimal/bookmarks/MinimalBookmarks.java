package com.metatroid.minimal.bookmarks;


import mobi.intuitit.android.content.LauncherIntent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MinimalBookmarks extends AppWidgetProvider {
	
	public static final int UPDATE_RATE = 3600000;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		final int N = appWidgetIds.length;
		for(int i=0; i<N; i++){
			int appWidgetId = appWidgetIds[i];
			SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
			String lc = prefs.getString("lcolor", "dark");
			RemoteViews views;
			if(lc.equals("dark")){
				views = new RemoteViews(context.getPackageName(), R.layout.minimal_bookmarks_layout);
			} else {
				views = new RemoteViews(context.getPackageName(), R.layout.minimal_bookmarks_layout_light);
			}
			
			
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		final String action = intent.getAction();
		if(TextUtils.equals(action, LauncherIntent.Action.ACTION_READY)){
			onAppWidgetReady(context, intent);
		} else if (TextUtils.equals(action, LauncherIntent.Action.ACTION_FINISH)) {
			int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			setAlarm(context, appWidgetId, UPDATE_RATE);
		} else if (TextUtils.equals(action, LauncherIntent.Action.ACTION_ITEM_CLICK)) {
			// onItemClickListener
			onClick(context, intent);
		} else if (TextUtils.equals(action, LauncherIntent.Action.ACTION_VIEW_CLICK)) {
			// onClickListener
			onClick(context, intent);
		}
	}
	public static void setAlarm(Context context, int appWidgetId, int updateRate) {
        PendingIntent newPending = makeControlPendingIntent(context,MinimalBookmarkService.UPDATE,appWidgetId);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (updateRate >= 0) {
            alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
        } else {
        	// on a negative updateRate stop the refreshing 
            alarms.cancel(newPending);
        }
    }
	
	public static PendingIntent makeControlPendingIntent(Context context, String command, int appWidgetId) {
        Intent active = new Intent(context,MinimalBookmarkService.class);
        active.setAction(command);
        active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //this Uri data is to make the PendingIntent unique, so it wont be updated by FLAG_UPDATE_CURRENT
        //so if there are multiple widget instances they wont override each other
        Uri data = Uri.withAppendedPath(Uri.parse("minimalbookmarks://widget/id/#"+command+appWidgetId), String.valueOf(appWidgetId));
        active.setData(data);
        return(PendingIntent.getService(context, 0, active, PendingIntent.FLAG_UPDATE_CURRENT));
    }
	
	private void onClick(Context context, Intent intent){
		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		Uri uri = Uri.parse(intent.getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_ITEM_POS));
		int viewId = intent.getIntExtra(LauncherIntent.Extra.EXTRA_VIEW_ID, -1);
		String u = intent.getStringExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION_ARGUMENTS);
		SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
		//Toast.makeText(context, prefs.getString("link"+uri, "null"), Toast.LENGTH_LONG).show();
		String url = prefs.getString("link"+uri, "http://google.com");
		if(URLUtil.isValidUrl(url)){
			Intent i = new Intent(); 
			i.setAction(Intent.ACTION_VIEW); 
			i.addCategory(Intent.CATEGORY_BROWSABLE); 
			i.setData(Uri.parse(prefs.getString("link"+uri, "http://google.com")));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		} else {
			Toast.makeText(context, "Not a valid URL", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	public static void onAppWidgetReady(Context context, Intent intent){
		if(intent == null){
			return;
		}
		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		if(appWidgetId < 0){
			return;
		}
		Intent replaceDummy = new Intent(LauncherIntent.Action.ACTION_SCROLL_WIDGET_START);
		replaceDummy.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		replaceDummy.putExtra(LauncherIntent.Extra.EXTRA_VIEW_ID, R.id.widget_content);
		replaceDummy.putExtra(LauncherIntent.Extra.Scroll.EXTRA_DATA_PROVIDER_ALLOW_REQUERY, true);
		replaceDummy.putExtra(LauncherIntent.Extra.Scroll.EXTRA_LISTVIEW_LAYOUT_ID,
				R.layout.wlistview);
		SharedPreferences prefs = context.getSharedPreferences("prefs", 0);
		String lc = prefs.getString("lcolor", "dark");
		if(lc.equals("dark")){
			replaceDummy.putExtra(LauncherIntent.Extra.Scroll.EXTRA_ITEM_LAYOUT_ID, R.layout.item);
		} else {
			replaceDummy.putExtra(LauncherIntent.Extra.Scroll.EXTRA_ITEM_LAYOUT_ID, R.layout.item_light);
		}
		putProvider(replaceDummy, DataProvider.CONTENT_URI_MESSAGES.buildUpon().appendEncodedPath(Integer.toString(appWidgetId)).toString());
		putMapping(replaceDummy);
		replaceDummy.putExtra(LauncherIntent.Extra.Scroll.EXTRA_ITEM_CHILDREN_CLICKABLE, true);
		
		context.sendBroadcast(replaceDummy);
	}
	
	public static void putProvider(Intent intent, String widgetUri) {
		if (intent == null)
			return;
		String whereClause = null;
		String orderBy = null;
		String[] selectionArgs = null;
		intent.putExtra(LauncherIntent.Extra.Scroll.EXTRA_DATA_URI, widgetUri);
		intent.putExtra(LauncherIntent.Extra.Scroll.EXTRA_PROJECTION, DataProvider.PROJECTION_APPWIDGETS);
		intent.putExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION, whereClause);
		intent.putExtra(LauncherIntent.Extra.Scroll.EXTRA_SELECTION_ARGUMENTS, selectionArgs);
		intent.putExtra(LauncherIntent.Extra.Scroll.EXTRA_SORT_ORDER, orderBy);

	}
	public static void putMapping(Intent intent) {
		if (intent == null)
			return;

		final int NB_ITEMS_TO_FILL = 3;

		int[] cursorIndices = new int[NB_ITEMS_TO_FILL];
		int[] viewTypes = new int[NB_ITEMS_TO_FILL];
		int[] layoutIds = new int[NB_ITEMS_TO_FILL];
		boolean[] clickable = new boolean[NB_ITEMS_TO_FILL];

		int iItem = 0;
		
		cursorIndices[iItem] = DataProvider.DataProviderColumns.title.ordinal();
		viewTypes[iItem] = LauncherIntent.Extra.Scroll.Types.TEXTVIEW;
		layoutIds[iItem] = R.id.title;
		clickable[iItem] = true;
		
		iItem++;
		
		cursorIndices[iItem] = DataProvider.DataProviderColumns.icon.ordinal();
		viewTypes[iItem] = LauncherIntent.Extra.Scroll.Types.IMAGERESOURCE;
		layoutIds[iItem] = R.id.icon;
		clickable[iItem] = true;
		
		cursorIndices[iItem] = DataProvider.DataProviderColumns.link.ordinal();
		viewTypes[iItem] = LauncherIntent.Extra.Scroll.Types.TEXTVIEW;
		layoutIds[iItem] = R.id.url;
		clickable[iItem] = true;

		intent.putExtra(LauncherIntent.Extra.Scroll.Mapping.EXTRA_VIEW_IDS, layoutIds);
		intent.putExtra(LauncherIntent.Extra.Scroll.Mapping.EXTRA_VIEW_TYPES, viewTypes);
		intent.putExtra(LauncherIntent.Extra.Scroll.Mapping.EXTRA_VIEW_CLICKABLE, clickable);
		intent.putExtra(LauncherIntent.Extra.Scroll.Mapping.EXTRA_CURSOR_INDICES, cursorIndices);

	}
	
	
}
