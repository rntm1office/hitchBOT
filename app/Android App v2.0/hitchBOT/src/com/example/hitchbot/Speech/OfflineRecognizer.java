package com.example.hitchbot.Speech;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hitchbot.Config;
import com.example.hitchbot.HitchActivity;
import com.example.hitchbot.R;
import com.example.hitchbot.Models.HttpPostDb;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class OfflineRecognizer implements RecognitionListener {

	private SpeechRecognizer recognizer;
	private HashMap<String, Integer> captions;
	private SpeechController speechController;
	private Handler freezeHandler;
	public boolean isListening = false;
	private static final String TAG = "OfflineRecognizer";

	public OfflineRecognizer() {
		initRecognizer();
	}

	public void setSpeechController(SpeechController speechController) {
		this.speechController = speechController;

	}

	private void initRecognizer() {

		// Prepare the data for UI
		captions = new HashMap<String, Integer>();
		captions.put(Config.searchName, 0);
		((TextView) ((HitchActivity) Config.context)
				.findViewById(R.id.textViewCaption))
				.setText("Preparing the recognizer");

		new AsyncTask<Void, Void, Exception>() {
			@Override
			protected Exception doInBackground(Void... params) {
				try {
					Assets assets = new Assets(Config.context);
					File assetDir = assets.syncAssets();
					setupRecognizer(assetDir);
				} catch (IOException e) {
					return e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Exception result) {
				if (result != null) {
					Log.i(TAG, "Failed to init recognizer " + result);
				} else {
					speechController.getSpeechIn().switchSearch(
							Config.searchName);
				}
			}
		}.execute();
	}

	private void setupRecognizer(File assetsDir) {
		File modelsDir = new File(assetsDir, "models");
	 	recognizer = defaultSetup()
				.setAcousticModel(new File(modelsDir, "hmm/de-ge"))
				.setDictionary(new File(modelsDir, "dict/9624.dic"))
				.setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
				.getRecognizer();	
	 	
		recognizer.addListener(this);

		// Create keyword-activation search.
		// recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
		// Create grammar-based searches.
		// File menuGrammar = new File(modelsDir, "grammar/menu.gram");
		// recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
		// File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
		// recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
		// Create language model search.
		File languageModel = new File(modelsDir, "lm/9624.dmp");
		recognizer.addNgramSearch(Config.searchName, languageModel);
	}

	public void startListening(String searchName) {
		this.isListening = true;
		freezeHandler = new Handler();
		freezeHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Config.context.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (isListening)
							getResult();
					}

				});
			}

		}, 15 * 1000);
		recognizer.startListening(searchName);
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndOfSpeech() {
		if (isListening)
			recognizer.stop();

	}

	@Override
	public void onPartialResult(Hypothesis arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(Hypothesis hypothesis) {
		this.isListening = false;
		((TextView) ((HitchActivity) Config.context)
				.findViewById(R.id.textViewCaption)).setText("");
		if (hypothesis != null) {
			String text = hypothesis.getHypstr();
			Log.i(TAG, text);
			Toast.makeText(Config.context.getApplicationContext(), text,
					Toast.LENGTH_SHORT).show();
			String uri = String.format(Config.heardPOST, Config.HITCHBOT_ID,
					Uri.encode(text), Config.getUtcDate());
			HttpPostDb httpPost = new HttpPostDb(uri, 0, 3);
			Config.dQ.addItemToQueue(httpPost);
			speechController.getSpeechIn().setIsListening(true);
			speechController.getSpeechOut().Speak(
					speechController.getCleverScriptHelper()
							.getResponseFromCleverScript(text));
		} else {
			Log.i(TAG, "null recognized result");

			speechController.getSpeechIn().setIsListening(true);
			String uri = String.format(Config.heardPOST, Config.HITCHBOT_ID,
					Uri.encode(""), Config.getUtcDate());
			HttpPostDb httpPost = new HttpPostDb(uri, 0, 3);
			Config.dQ.addItemToQueue(httpPost);
			speechController.getSpeechOut().Speak(
					speechController.getCleverScriptHelper()
							.getResponseFromCleverScript(""));
		}
	}

	public void getResult() {
		recognizer.cancel();
		isListening = false;
		this.isListening = false;
		Config.context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				speechController.getSpeechOut().Speak(
						speechController.getCleverScriptHelper()
								.getResponseFromCleverScript(""));
			}
		});
	}

	public void pauseRecognizer() {
		recognizer.cancel();
	}

}
