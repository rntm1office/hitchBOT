package com.example.hitchbot;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

public class DataCommunication extends AsyncTask<Data, Void, Boolean> {
	
	private String TAG = "DataComunnication";

	public DataCommunication()
	{
		
	}
	
	@Override
	protected Boolean doInBackground(Data... params) {
		for(int i = 0; i < params.length ; i ++){
			HttpClient hC = new DefaultHttpClient();
			HttpPost hP = new HttpPost(params[i].getUri());
			Log.i(TAG, params[i].getUri() );

			if (params[i].getHeader() != null)
			{
				for(int j = 0; j < params[i].getHeader().size(); j ++)
				{
			hP.addHeader(params[i].getHeader().get(j).getName(),params[i].getHeader().get(j).getValue());
			Log.i(TAG, "header isn't null" );
				}
			}
			
			try
			{
				if(params[i].getBody() != null)
				{
				hP.setEntity(new UrlEncodedFormEntity(params[i].getBody(), HTTP.UTF_8));
				}
				
				HttpResponse hR = hC.execute(hP);
				String responseCheck = EntityUtils.toString(hR.getEntity());
				Log.i(TAG, responseCheck );
				
				if(!responseCheck.equals("true"))
				{
					//TODO add to queue
				}

			}
			catch(ClientProtocolException e)
			{
				//TODO add to queue
				Log.i(TAG, e.toString() );
			}
			catch(IOException e)
			{
				//TODO add to queue
				Log.i(TAG, e.toString() );
			}
		}
			return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result)
		{
			//ok
		}
		else
		{
			//add back to queue
		}
	}

}
