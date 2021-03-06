package com.example.hitchbot.Speech;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.cleverscript.android.*;
import com.example.hitchbot.Config;

public class CleverScriptHelper {

	public CleverscriptAPI cs;
	private static String TAG = "CleverScriptHelper";
	private SpeechController speechController;
	
	public CleverScriptHelper(String db, String APIkey)
	{
		cs = new CleverscriptAPI(Config.context);
		cs.setLocation(db);
		cs.setApiKey(APIkey);
		cs.setDebugLevel(4);
	    cs.loadDatabase();
	    loadVariables();
	}

	public void setSpeechController(SpeechController speechController)
	{
		this.speechController = speechController;
	}
	
	public void loadVariables()
	{

		for(org.apache.http.NameValuePair pair : Config.cleverPair)
		{
			cs.assignVariable(pair.getName(), pair.getValue());
		}	}
	
	public void sendCleverScriptResponse(String message)
	{
		//loadVariables();

		speechController.getSpeechOut().Speak(cs.sendMessage(message));
	}
	
	public String getResponseFromCleverScript(String message)
	{
		//loadVariables();

		return cs.sendMessage(message);
	}
	
	public String getRecentInput()
	{
		return cs.retrieveVariable("input");
	}
	
	public void getInformationForVariables(String jsonString)
	{
		if(jsonString != null)
		{
		try
		{
			JSONObject obj = new JSONObject(jsonString);
			JSONArray jO = obj.getJSONArray("data");
			Config.accessok = false;
			for(int j = 0 ; j < jO.length() ; j ++)
			{
				JSONObject jobj = jO.getJSONObject(j);
				org.apache.http.NameValuePair nvP = new org.apache.http.message.BasicNameValuePair(jobj.getString("key"), (String) jobj.getString("value"));
				Config.cleverPair.add(nvP);
				//cs.assignVariable((String) jobj.getString("key"), (String) jobj.getString("value"));
				Log.i(TAG, (String) jobj.getString("key") + " ");
				Log.i(TAG, (String) jobj.getString("value") + " ");
				
			}
			Config.accessok = true;
		}
		catch(Exception e)
		{
			//stuff
		}
		}
}
	
	public String getBotState()
	{
		return cs.retrieveBotState();
	}
	
	public void loadBotState(String state)
	{
		cs.assignBotState(state);
	}

	public String getAccuracy()
	{
		return cs.retrieveVariable("accuracy");
	}
	
	public String getAudio_on()
	{
		return cs.retrieveVariable("audio_on");
	}
	
	public String getClever_data()
	{
		return cs.retrieveVariable("cleverdata_on");
	}

	
}
