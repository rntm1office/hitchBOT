package com.example.stayalive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		AlarmManager aM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, KeepAliveService.class);
		PendingIntent pI = PendingIntent.getService(context, 0, i, 0);
		aM.cancel(pI);
		//Goes off every 5 minutes
		aM.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
					SystemClock.elapsedRealtime() +  1000 * 10, 
					5 * 1000 * 60, pI);
		
	}
	
}
