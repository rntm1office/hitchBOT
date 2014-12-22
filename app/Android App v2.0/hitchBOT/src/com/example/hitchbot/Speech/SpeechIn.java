package com.example.hitchbot.Speech;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.example.hitchbot.Config;
import com.example.hitchbot.HitchActivity;
import com.example.hitchbot.R;

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

	private static final String KWS_SEARCH = "wakeup";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    private boolean isListening = false;
	
	public SpeechIn()
	{
		initRecognizer();
	}
	
	private void initRecognizer()
	{
		
		// Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, 0);
       ((TextView) ((HitchActivity) Config.context).findViewById(R.id.textViewCaption))
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
                    ((TextView) ((HitchActivity)Config.context).findViewById(R.id.textViewCaption))
                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
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
	      //  recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
	        // Create grammar-based searches.
	       // File menuGrammar = new File(modelsDir, "grammar/menu.gram");
	       // recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
	       // File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
	       // recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
	        // Create language model search.
	       // File languageModel = new File(modelsDir, "lm/weather.dmp");
	       // recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
	    }
	   
	   private void switchSearch(String searchName) {
	        recognizer.stop();
	        isListening = true;
	        recognizer.startListening(searchName);
	       // String caption = Config.context.getResources().getString(captions.get(searchName));
	    }
	
	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPartialResult(Hypothesis arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResult(Hypothesis hypothesis) {
		((TextView) ((HitchActivity)Config.context).findViewById(R.id.textViewCaption)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Toast.makeText(Config.context.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
        isListening = false;
		
	}

	public boolean isListening() {
		return isListening;
	}

}
