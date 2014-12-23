package com.example.hitchbot.Speech;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.example.hitchbot.Config;
import com.example.hitchbot.HitchActivity;
import com.example.hitchbot.R;
import com.example.hitchbot.Models.HttpPostDb;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class SpeechIn implements RecognitionListener {


	private SpeechRecognizer recognizer;
	private HashMap<String, Integer> captions;
	private boolean isListening = false;
	private long startTime;
	private SpeechOut speechOut;
	private CleverScriptHelper csh;

	public SpeechIn() {
		initRecognizer();
	}

	public void setSpeechOut(SpeechOut speechOut) {
		this.speechOut = speechOut;
	}

	public void setCleverScript(CleverScriptHelper csh) {
		this.csh = csh;
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
					((TextView) ((HitchActivity) Config.context)
							.findViewById(R.id.textViewCaption))
							.setText("Failed to init recognizer " + result);
				} else {
					switchSearch(Config.searchName);
				}
			}
		}.execute();
	}

	public void getResult() {
		recognizer.stop();
	}

	private void setupRecognizer(File assetsDir) {
		File modelsDir = new File(assetsDir, "models");
		recognizer = defaultSetup()
				.setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
				.setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
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
		// File languageModel = new File(modelsDir, "lm/weather.dmp");
		// recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
	}

	public void switchSearch(String searchName) {
		recognizer.stop();
		isListening = true;
		startTime = System.currentTimeMillis() / 1000;
		recognizer.startListening(searchName);
		// String caption =
		// Config.context.getResources().getString(captions.get(searchName));
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndOfSpeech() {
		getResult();

	}

	@Override
	public void onPartialResult(Hypothesis arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResult(Hypothesis hypothesis) {
		((TextView) ((HitchActivity) Config.context)
				.findViewById(R.id.textViewCaption)).setText("");
		isListening = false;
		if (hypothesis != null) {
			String text = hypothesis.getHypstr();
			Toast.makeText(Config.context.getApplicationContext(), text,
					Toast.LENGTH_SHORT).show();
			String uri = String.format(Config.heardPOST, Config.HITCHBOT_ID, text, Config.getUtcDate());
			HttpPostDb httpPost = new HttpPostDb(uri, 0, 3);
			Config.dQ.addItemToQueue(httpPost);
			speechOut.Speak(csh.getResponseFromCleverScript(text));
		}

	}

	public boolean getRunningTime() {
		if (isListening) {
			if ((System.currentTimeMillis() / 1000) - startTime <= 10) {
				return true;
			} else {
				getResult();
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isListening() {
		return isListening;
	}

}
