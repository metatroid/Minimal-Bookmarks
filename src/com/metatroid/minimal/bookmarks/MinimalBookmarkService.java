package com.metatroid.minimal.bookmarks;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MinimalBookmarkService extends Service{
	public static final String UPDATE = "update";
	public static final String CONFIG = "config";
	public static final String REFRESH = "refresh";
	public static final String ADD = "add";
	//
	final ArrayList<HashMap<String,String>> ITEMS = new ArrayList<HashMap<String,String>>();
	SimpleAdapter sList;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		
		
		String command = intent.getAction();
		int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
		if ((flags & START_FLAG_REDELIVERY)!=0) { 
			System.out.println("Minimal Bookmark Service restarted by Android System - don't process commands");
		} else {
			if(command.equals("refresh")){
				//Toast.makeText(getApplicationContext(), "updating...", Toast.LENGTH_SHORT).show();
				MinimalBookmarks.onAppWidgetReady(getApplicationContext(), intent);
			}
			if(command.equals("config")){
				//Toast.makeText(getApplicationContext(), "conf", Toast.LENGTH_SHORT).show();
				Intent i = new Intent();
				i.setAction("android.intent.action.VIEW"); 
				//i.addCategory("android.intent.category.BROWSABLE");
				i.setClassName("com.android.browser", "com.android.browser.CombinedBookmarkHistoryActivity");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
			if(command.equals("add")){
				//Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(), AddBookmark.class);
				i.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				Uri data = Uri.withAppendedPath(Uri.parse("minimalbookmarks://widget/id/#"+command+appWidgetId), String.valueOf(appWidgetId));
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setData(data);
				getApplicationContext().startActivity(i);
			}
		}
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences("prefs", 0);
		String lc = prefs.getString("lcolor", "dark");
		RemoteViews remoteView;
		if(lc.equals("dark")){
			remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.minimal_bookmarks_layout);
		} else {
			remoteView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.minimal_bookmarks_layout_light);
		}

		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
		
		remoteView.setOnClickPendingIntent(R.id.widget_container,MinimalBookmarks.makeControlPendingIntent(getApplicationContext(),REFRESH,appWidgetId));
		remoteView.setOnClickPendingIntent(R.id.add,MinimalBookmarks.makeControlPendingIntent(getApplicationContext(),ADD,appWidgetId));
		remoteView.setOnClickPendingIntent(R.id.conf,MinimalBookmarks.makeControlPendingIntent(getApplicationContext(),CONFIG,appWidgetId));
		
		appWidgetManager.updateAppWidget(appWidgetId, remoteView);
		super.onStart(intent, startId);
		return START_REDELIVER_INTENT;
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
}