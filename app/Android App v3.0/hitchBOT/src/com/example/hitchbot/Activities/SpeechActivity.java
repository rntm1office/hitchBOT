package com.example.hitchbot.Activities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.example.hitchbot.Config;
import com.example.hitchbot.LocationInfo;
import com.example.hitchbot.R;
import com.example.hitchbot.TabletInfo;

import Camera.PictureTaker;
import Data.DataGET;
import Data.DataPOST;
import Data.FileUpload;
import Models.DatabaseConfig;
import Models.FileUploadDb;
import Models.HttpPostDb;
import Speech.SpeechController;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SpeechActivity extends Activity {

	SpeechController speechController;
	private boolean speechIsGo = true;
	public PictureTaker tP;
	private Handler pictureHandler;
	private Handler dataCollectionHandler;
	private Handler internetHandler;
	private Handler fileUploadHandler;
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
		speechController.startCycle();
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
					List<FileUploadDb> fileQueue = Config.dQ
							.serverFileUploadQueue();
					FileUploadDb[] dbFileArray = new FileUploadDb[fileQueue
							.size()];
					Log.i(TAG, String.valueOf(fileQueue.size()));
					uploadFile(fileQueue.toArray(dbFileArray));
					new DataGET().execute(Config.cleverGET);
				}
				internetHandler.postDelayed(this, Config.FIFTEEN_MINUTES);
			}
		}, Config.TWENTY_SECONDS);
		
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
	
	public void uploadFile(final FileUploadDb[] fileUpload) {
		fileUploadHandler = new Handler();
		fileUploadHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					Log.i("FileUpload", fileUpload.length + "length of queueueueueueuue");
					for (int i = 0; i < Math.min(2, fileUpload.length); i++) {
						if (fileUpload[i] == null) {
							Log.i(TAG, "ITS NULL");
						}
						FileInputStream fstrm = new FileInputStream(
								fileUpload[i].getUri());
						String uploadUrl;
						switch (fileUpload[i].getFileType()) {
						case 0:
							uploadUrl = Config.audioPOST;
							break;
						case 1:
							uploadUrl = String.format(Config.imagePOST,
									Config.ID,
									fileUpload[i].getDateCreated());
							break;
						default:
							uploadUrl = Config.audioPOST;
							break;
						}
						FileUpload hfu = new FileUpload(uploadUrl,
								fileUpload[i]);

						hfu.send_Now(fstrm);

					}
				} catch (FileNotFoundException e) {
					//Extreme error handling (only done because of time crunch)
					Config.dQ.launchFileMissles();
				}
			}

		});

	}

}
