package com.example.hitchbot;

import java.io.IOException;

import com.example.hitchbot.Models.FileUploadDb;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class StoryRecorder {
	private MediaRecorder mRecorder = null;
	private static final String TAG = "StoryRecorder";
	public String mFileName = null;
	private Handler recorderHandler;

	public StoryRecorder() {
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/" + Config.getUtcDate() + "lifeStory.3gp";
		recorderHandler = new Handler();
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
		Config.context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mRecorder.start();

			}
		});
	}

	private void stopRecording() {
		Config.context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}

		});

		
	}

	public void recordSixty() {
		startRecording();

		recorderHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopRecording();
				FileUploadDb insert = new FileUploadDb(mFileName, 0, 0);
				Config.dQ.addItemToQueue(insert);
				Config.cH.cs.assignVariable("audio_on", "false");
				Config.cH.sendCleverScriptResponse("done");
			}

		}, Config.ONE_MINUTE);

	}

}
