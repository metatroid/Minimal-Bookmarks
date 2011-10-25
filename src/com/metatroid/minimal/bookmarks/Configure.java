package com.metatroid.minimal.bookmarks;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;


public class Configure extends Activity{
	private Context self = this;
	private int appWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);
		setContentView(R.layout.configure);
		
		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		RadioButton black = (RadioButton) findViewById(R.id.radio_Dark);
		RadioButton white = (RadioButton) findViewById(R.id.radio_Light);
		
		white.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
				SharedPreferences.Editor edit = prefs.edit();
				if(isChecked){
					edit.putString("lcolor", "light");
					edit.commit();
				} else {
					edit.putString("lcolor", "dark");
					edit.commit();
				}
			}
		});
		black.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
				SharedPreferences.Editor edit = prefs.edit();
				if(isChecked){
					edit.putString("lcolor", "dark");
					edit.commit();
				} else {
					edit.putString("lcolor", "light");
					edit.commit();
				}
			}
		});
		
		//end buttons
		Button done = (Button) findViewById(R.id.configure_done_button);
		done.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
				SharedPreferences.Editor edit = prefs.edit();
				//save text prefs
				//PendingIntent updatepending = MinimalBookmarks.makeControlPendingIntent(self, MinimalBookmarkService.UPDATE, appWidgetId);
				//try {
					//updatepending.send();
				//} catch (CanceledException e){
					//e.printStackTrace();
				//}
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});
		Button cancel = (Button) findViewById(R.id.configure_cancel_button);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}
	public void colorPick(){
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.WHITE, new OnAmbilWarnaListener() {
	        @Override
	        public void onOk(AmbilWarnaDialog dialog, int color) {
	                // color is the color selected by the user.
	        	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    		SharedPreferences.Editor edit = prefs.edit();
	    		edit.putInt("textColor", color);
	    		
	    		edit.commit();
	        }
	                
	        @Override
	        public void onCancel(AmbilWarnaDialog dialog) {
	                // cancel was selected by the user
	        	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    		SharedPreferences.Editor edit = prefs.edit();
	    		edit.putInt("textColor", Color.WHITE);
	    		
	    		edit.commit();
	        }
	});

	dialog.show();
	}
}