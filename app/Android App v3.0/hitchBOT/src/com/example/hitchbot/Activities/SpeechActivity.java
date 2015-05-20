package com.example.hitchbot.Activities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.example.hitchbot.Config;
import com.example.hitchbot.LocationInfo;
import com.example.hitchbot.R;
import com.example.hitchbot.TabletInfo;

import Camera.PictureTaker;
import Data.DataPOST;
import Models.DatabaseConfig;
import Models.HttpPostDb;
import Speech.CleverScriptHelper;
import Speech.SpeechController;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SpeechActivity extends Activity {

	SpeechController speechController;
	private boolean speechIsGo = true;
	public PictureTaker tP;
	private Handler pictureHandler;
	private Handler dataCollectionHandler;
	private Handler internetHandler;
	private static String TAG = SpeechActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech);
		Config.context = this;
		Config.dQ = DatabaseConfig.getHelper(this);
		setUnCaughtExceptionHandler();
		tP = new PictureTaker();
		setupHandler();
		// queue location info
		LocationInfo lu = new LocationInfo(Config.context);
		lu.setupProvider();
		speechController = new SpeechController();
	}



	private void setUnCaughtExceptionHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				String stackTrace = sw.toString();
				Log.i(TAG, stackTrace);
				String uri = String.format(Config.exceptionPOST,
						Config.ID, Uri.encode(stackTrace),
						Config.getUtcDate());
				HttpPostDb eL = new HttpPostDb(uri, 0, 7);
				Config.dQ.addItemToQueue(eL);
				System.exit(2);
			}
		});
	}
	
	private void setupHandler() {
		internetHandler = new Handler();
		internetHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (Config.networkAvailable()) {
					List<HttpPostDb> postQueue = Config.dQ
							.serverPostUploadQueue();
					HttpPostDb[] dbPostArray = new HttpPostDb[postQueue.size()];
					new DataPOST().execute(postQueue.toArray(dbPostArray));
					internetHandler.postDelayed(this, Config.FIVE_MINUTES);
				}
			}
		}, Config.ONE_MINUTE);
		
		dataCollectionHandler = new Handler();
		dataCollectionHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// get environment and tablet info
				TabletInfo tI = new TabletInfo();
				tI.queueBatteryUpdates();

				dataCollectionHandler.postDelayed(this, Config.FIFTEEN_MINUTES);

			}

		}, Config.THIRTY_SECONDS);
		
		pictureHandler = new Handler();
		pictureHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				takePicture();
				pictureHandler.postDelayed(this, Config.FIFTEEN_MINUTES);
			}
		}, Config.THIRTY_SECONDS);

	}

	
	public void takePicture() {
		tP.captureHandler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hitch, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
