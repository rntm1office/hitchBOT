package com.example.stayalive;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class KeepAliveService extends Service {

	// poll every 3 secs
	private static final int INTERVAL = 5000;
	private static final String HITCHBOT = "com.example.hitchbot";
	private static String TAG = "KeepAliveService";
	protected PowerManager.WakeLock mWakeLock;


	private static boolean stopTask;

	public void handleIntent(Intent intent) {
		final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
		super.onCreate();
		stopTask = false;

		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				if (stopTask) {
					this.cancel();
				}

				String foregroundTaskPackageName = getNameOfForegroundApp();
				Log.i(TAG, foregroundTaskPackageName);
				if (!foregroundTaskPackageName.equals(HITCHBOT)) {
					Intent LaunchIntent = getPackageManager()
							.getLaunchIntentForPackage(HITCHBOT);
					startActivity(LaunchIntent);
				}
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, INTERVAL);
	}

	  @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        handleIntent(intent);
	        return START_NOT_STICKY;
	    }
	
	@Override
	public void onDestroy() {
		stopTask = true;
        this.mWakeLock.release();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getNameOfForegroundApp() {
		ActivityManager am = (ActivityManager) KeepAliveService.this
				.getSystemService(ACTIVITY_SERVICE);
		// The first in the list of RunningTasks is always the foreground task.
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		String foregroundTaskPackageName = foregroundTaskInfo.topActivity
				.getPackageName();
		return foregroundTaskPackageName;
		/*Log.i(TAG, foregroundTaskPackageName);
		PackageManager pm = KeepAliveService.this.getPackageManager();
		PackageInfo foregroundAppPackageInfo = null;
		try {
			foregroundAppPackageInfo = pm.getPackageInfo(
					foregroundTaskPackageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo
				.loadLabel(pm).toString();
		Log.i(TAG, foregroundTaskAppName);
		return foregroundTaskAppName;*/
	}

}
