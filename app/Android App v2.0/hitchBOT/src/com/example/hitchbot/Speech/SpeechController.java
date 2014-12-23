package com.example.hitchbot.Speech;

import com.example.hitchbot.Config;

public class SpeechController {
	/*
	 * The purpose of this class is to: 
	 * 1. prevent freezing of speech recognition 
	 * 2. update clever variables periodically 
	 * 3. pause speech and start story recording periodically (every 45 minutes for now)
	 */
	private SpeechIn speechIn;
	private SpeechOut speechOut;

	public SpeechController() {
		CleverScriptHelper csh = new CleverScriptHelper(Config.cleverDB, Config.cleverAPIKey);
		speechIn = new SpeechIn();
		speechOut = new SpeechOut();
		speechIn.setSpeechOut(speechOut);
		speechIn.setCleverScript(csh);
		speechOut.setSpeechIn(speechIn);
	}

	public void beginSpeechCycle()
	{
		speechIn.switchSearch(Config.searchName);
	}
	
	public void askToRecordAudio()
	{
		
	}
	
	public void pauseSpeechCycle()
	{
		
	}

}
