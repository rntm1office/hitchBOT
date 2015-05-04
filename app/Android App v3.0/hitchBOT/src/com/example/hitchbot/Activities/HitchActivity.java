package com.example.hitchbot.Activities;

import com.example.hitchbot.Config;
import com.example.hitchbot.R;
import com.example.hitchbot.R.id;
import com.example.hitchbot.R.layout;
import com.example.hitchbot.R.menu;
import com.example.hitchbot.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HitchActivity extends Activity{

	Button startButton;
	EditText editTextName;
	EditText editTextSpecInfo;
	TextView textViewName;
	TextView textViewSpecInfo;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_hitch);
	        Config.context = this;
	        startButton = (Button) findViewById(R.id.buttonStart);
	        editTextName = (EditText) findViewById(R.id.editTextName);
	        editTextSpecInfo = (EditText) findViewById(R.id.editTextSpecInfo);
	        
	    }

	 	public void startSpeech(View view)
	 	{
	 		String name = editTextName.getText().toString();
	 		String specInfo = editTextSpecInfo.getText().toString();
	 		
	 		//Must enter info
	 		if(name.equals("") || specInfo.equals(""))
	 		{
	 			new AlertDialog.Builder(this)
	 		    .setTitle("Warning!")
	 		    .setMessage(R.string.missing_info)
	 		    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	 		        public void onClick(DialogInterface dialog, int which) { 
	 		            dialog.cancel();
	 		        }
	 		     })
	 		    .setIcon(android.R.drawable.ic_dialog_alert)
	 		     .show();
	 		}
	 		else
	 		{
	 			Config.name = name;
	 			Config.specInfo = specInfo;
	 			Intent intent = new Intent(this, SpeechActivity.class);	
	 			startActivity(intent);
	 		}
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