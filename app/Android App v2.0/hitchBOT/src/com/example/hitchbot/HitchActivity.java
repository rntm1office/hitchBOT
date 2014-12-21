package com.example.hitchbot;

import java.util.List;

import com.example.hitchbot.Models.HttpPostDb;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HitchActivity extends ActionBarActivity {

	public TakePicture tP;
	private Handler pictureHandler;
	private Handler dataCollectionHandler;
	private Handler internetHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitch);
        Config.context = this;
        Config.dQ = DatabaseQueue.getHelper(this);
        tP = new TakePicture();
        
        dataCollectionHandler = new Handler();
        dataCollectionHandler.postDelayed(new Runnable()
        {

			@Override
			public void run() {
				//get environment and tablet info
				TabletInformation tI = new TabletInformation();
				tI.queueBatteryUpdates();
				
				//queue location info
				LocationUpdates lu = new LocationUpdates(Config.context);
				lu.setupProvider();
				
			}
        	
        }, Config.FIFTEEN_MINUTES);
        
        internetHandler = new Handler();
        internetHandler.postDelayed(new Runnable()
        {
        	@Override
			public void run() 
        	{
        		if(Config.networkAvailable())
        		{
        			List<HttpPostDb> queue = Config.dQ.serverUploadQueue();
        			new DataCommunication().execute((HttpPostDb[])queue.toArray());
        			internetHandler.postDelayed(this, Config.FIFTEEN_MINUTES);
        		}
        	}
        }, Config.FIFTEEN_MINUTES);
        
        pictureHandler = new Handler();
        pictureHandler.postDelayed(new Runnable()
        {

			@Override
			public void run() {
				takePicture();
				pictureHandler.postDelayed(this, Config.FIFTEEN_MINUTES);			
			}
        	
        }, Config.FIFTEEN_MINUTES);
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
    
    public void takePicture()
    {
    	tP.captureHandler();
    }
    
}
