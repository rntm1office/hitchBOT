package Speech;

import java.util.ArrayList;

import com.example.hitchbot.Config;
import com.example.hitchbot.Activities.SpeechActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class GoogleRecognizer implements RecognitionListener {

	private SpeechRecognizer mSpeechRecognizer;
	private Intent mSpeechRecognizerIntent; 
	private CleverScriptHelper csh;
	private static final String TAG = "GoogleRecognizer";
	private boolean isListening = false;
	private boolean isSetup = false;
	private int errorCounter = 0;
	private double rmsDbLevel = 0.0;
	private int rmsCounter = 0;
	AudioManager aM;
	
	public GoogleRecognizer ()
	{
		//aM = (AudioManager)Config.context.getSystemService(Context.AUDIO_SERVICE);
		setupRecognizer();
	}
	
	public void setCleverHandler(CleverScriptHelper csh)
	{
		this.csh = csh;
	}
	
	private void setupRecognizer(){
		isSetup = true;
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Config.context);
	    mSpeechRecognizer.setRecognitionListener(this); 
		mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
	                                     Config.context.getPackageName());
	   // mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
	   // mSpeechRecognizerIntent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{});
	}
	
	public void startListening()
	{
		//aM.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		rmsDbLevel = 0.0;
		rmsCounter = 0;
		if(!isSetup)
			setupRecognizer();
		//aM.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		isListening = true;
		mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
	}
	
	@Override
	public void onReadyForSpeech(Bundle params) {
		Log.i(TAG, "onreadyforspeach called");		
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		rmsDbLevel += rmsdB;
		rmsCounter+=1;
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(int error) {
		Log.i(TAG, "Speech Error: " + error);
		switch(error){
		case SpeechRecognizer.ERROR_AUDIO:
			handleError("3");
			break;
		case SpeechRecognizer.ERROR_CLIENT:
			handleError("5");
			break;
		case SpeechRecognizer.ERROR_NETWORK:
			handleError("2");
			break;
		case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
			handleError("1");
			break;
		case SpeechRecognizer.ERROR_NO_MATCH:
			handleError("");
			break;
		case SpeechRecognizer.ERROR_SERVER:
			handleError("4");
			break;
		case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
			handleError("");
			break;
		case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
			killRecognizer();
			handleError("8");
			break;
		default:
			handleError("");
			break;
		}
	}
	
	private void killRecognizer()
	{
		mSpeechRecognizer.destroy();
		isSetup = false;
	}
	
	private void handleError(String error)
	{
		errorCounter++;
		isListening = false;
		//mSpeechRecognizer.cancel();
		//Want to slow down responses if it is responding too fast.
		if(errorCounter > 4){
			//aM.setStreamVolume(AudioManager.STREAM_MUSIC,
			//		aM.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			errorCounter = 0;
			csh.sendCleverScriptResponse(error);
		}
		else
			startListening();
	}

	@Override
	public void onResults(Bundle results) {
		//aM.setStreamVolume(AudioManager.STREAM_MUSIC,
		//		aM.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		isListening = false;
		//aM.setStreamVolume(AudioManager.STREAM_MUSIC,
		//					aM.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		 float [] scores = results.getFloatArray(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
		String message = matches.get(0);
		Log.i(TAG, message + " ");
		float score = -1.0f;
		if(scores != null && scores.length > 0){
			score = scores[0];
		}
		csh.sendCleverScriptResponse(message, rmsDbLevel / rmsCounter,score, 0);
		
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		Log.i(TAG, params.toString() + "");
		// TODO Auto-generated method stub
		
	}

}