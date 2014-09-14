package com.example.foodrescue;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import databases.SQLiteHelper;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

	NotificationManager nm;
	SQLiteHelper db;
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("AlarmReceiver", "In Alarm Receiver");
		db = new SQLiteHelper(context);
		int foodCount = db.moveExpiredFood();
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence from = "Food Rescue";
		CharSequence message = "Food is about to expire!";
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		Notification notif = new Notification(R.drawable.food_deafault,
				"Food Rescue", System.currentTimeMillis());
		notif.setLatestEventInfo(context, from, message, contentIntent);
		if(foodCount > 0){
			nm.notify(1, notif);
		}
	}
	

	
	

}
