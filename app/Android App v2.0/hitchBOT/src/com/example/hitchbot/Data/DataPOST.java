package com.example.hitchbot.Data;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.hitchbot.Config;
import com.example.hitchbot.Models.*;

import android.os.AsyncTask;
import android.util.Log;

public class DataPOST extends AsyncTask<HttpPostDb, Void, Boolean> {
	
	private String TAG = "DataPOST";

	public DataPOST()
	{
		
	}
	
	@Override
	protected Boolean doInBackground(HttpPostDb... params) {
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
				
				if(responseCheck.equals("true"))
				{
					Config.dQ.markAsUploadedToServer(params[i]);
				}

			}
			catch(ClientProtocolException e)
			{
				Log.i(TAG, e.toString() );
			}
			catch(IOException e)
			{
				Log.i(TAG, e.toString() );
			}
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result)
		{
			//Config.dQ.markAsUploadedToServer(httpPost);
		}
		else
		{
			//do nothing (stays in queue)
		}
	}

}
