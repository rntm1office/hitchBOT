package com.example.hitchbot.Speech;

import java.util.HashMap;
import java.util.Locale;

import com.example.hitchbot.Config;
import com.example.hitchbot.Models.HttpPostDb;

import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

public class SpeechOut {

	private TextToSpeech mTts;
	private boolean isSpeaking;
	private SpeechIn speechIn;
	private static String searchName = "searchName";

	public SpeechOut() {
		mTts = new TextToSpeech(Config.context,
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						if (status != TextToSpeech.ERROR) {
							mTts.setLanguage(Locale.GERMAN);
							mTts.setPitch((float) 1.0);
							setTtsListener();
						}
					}
				});
	}
	
	public void setSpeechIn(SpeechIn speechIn)
	{
		this.speechIn = speechIn;
	}

	@SuppressWarnings("deprecation")
	public void Speak(String message) {

		HashMap<String, String> myHashAlarm = new HashMap<String, String>();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
				String.valueOf(AudioManager.STREAM_ALARM));
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
				"SOME MESSAGE");

		mTts.speak(message, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
		queueSpoke(message);
	}

	private void setTtsListener() {
		int listenerResult = mTts
				.setOnUtteranceProgressListener(new UtteranceProgressListener() {
					@Override
					public void onDone(String utteranceId) {
						isSpeaking = false;
						if(speechIn != null)
						{
							speechIn.switchSearch(searchName);
						}
						
					}

					@Override
					public void onError(String utteranceId) {
						// TODO
					}

					@Override
					public void onStart(String utteranceId) {
						isSpeaking = true;
					}

				});
		if (listenerResult != TextToSpeech.SUCCESS) {
		}
	}

	private void queueSpoke(String spoke) {
		String uri = String.format(Config.spokePOST, Config.HITCHBOT_ID, spoke,
				Config.getUtcDate());
		HttpPostDb httpPost = new HttpPostDb(uri, 0, 3);
		Config.dQ.addItemToQueue(httpPost);
	}

	public boolean isSpeaking() {
		return isSpeaking;
	}

}
