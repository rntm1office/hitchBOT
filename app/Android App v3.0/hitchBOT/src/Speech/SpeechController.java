package Speech;

import com.example.hitchbot.Config;
import com.example.hitchbot.R;

import android.widget.Toast;

public class SpeechController {
	//The flow of speech is recognizer -> cleverscript -> text to speech .. repeat
	SpeechOut speechOut;
	CleverScriptHelper csh;
	GoogleRecognizer gRecognizer;
	
	public SpeechController()
	{
		gRecognizer = new GoogleRecognizer();
		speechOut = new SpeechOut();
        csh = new CleverScriptHelper(Config.context.getString(R.string.clever_db), 
        		Config.context.getString(R.string.clever_apikey));
        setup();
        Config.csh = csh;
        startCycle();
	}
	
	private void setup()
	{
		gRecognizer.setCleverHandler(csh);
		speechOut.setRecognizer(gRecognizer);
		csh.setSpeechOut(speechOut);
	}

	public void startCycle()
	{
		speechOut.startRecognition();
		//if(Config.networkAvailable())
			gRecognizer.startListening();
		//else
		//	pRecognizer.startListening(Config.searchName);
	}
	
	public void stopCycle()
	{
		speechOut.stopRecognition();
		Toast.makeText(Config.context, "Speech will stop after next speech output", 
				Toast.LENGTH_LONG).show();
	}
}
