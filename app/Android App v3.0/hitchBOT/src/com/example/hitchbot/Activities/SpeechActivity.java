package com.example.hitchbot.Activities;

import com.example.hitchbot.Config;
import com.example.hitchbot.R;

import Speech.CleverScriptHelper;
import Speech.SpeechController;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SpeechActivity extends Activity {

	SpeechController speechController;
	private boolean speechIsGo = true;
	Button speechButton;
	TextView chatHistory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speech);
		Config.context = this;
		chatHistory = (TextView) findViewById(R.id.textViewSpeechHistory);
		speechButton = (Button) findViewById(R.id.buttonSpeech);
		speechController = new SpeechController();
		speechController.startCycle();
	}

	public void modifySpeechCycle(View view) {
		if (speechIsGo) {
			speechIsGo = false;
			speechButton.setText("Start Speech");
			speechController.stopCycle();
		} else {
			speechIsGo = true;
			speechButton.setText("Stop Speech");
			speechController.startCycle();
		}
	}

	public void updateYourChat(String text){
		String preamble = "<font color=#004c00>YOU: </font>";
		String message = preamble + text + "<br />";
		String tempText = chatHistory.getText().toString();
		if(tempText.length() > 1000){
			tempText = tempText.substring(0, 500);
		}
		tempText = tempText.replace("YOU:", "<br /> <font color=#004c00>YOU: </font>");
		tempText = tempText.replace("hitchBOT:", "<br /> <font color=#236B8E>hitchBOT: </font>");
		message = message.replaceFirst("<br />", "");
		chatHistory.setText(Html.fromHtml(message + tempText));
		}
	
	public void updateHitchBotChat(String text)
	{
		String preamble = "<font color=#236B8E>hitchBOT: </font>";
		String tempText = chatHistory.getText().toString();
		if(tempText.length() > 1000){
			tempText = tempText.substring(0, 500);
		}
		String message = preamble + text + "<br />";
		tempText = tempText.replace("YOU:", "<br /> <font color=#004c00>YOU: </font>");
		tempText = tempText.replace("hitchBOT:", "<br /> <font color=#236B8E>hitchBOT: </font>");
		message = message.replaceFirst("<br />", "");
		chatHistory.setText( Html.fromHtml(message + tempText));
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

}