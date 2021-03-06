package Data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.example.hitchbot.Config;

import Models.HttpPostDb;
import android.os.AsyncTask;
import android.util.Log;

public class DataPOST extends AsyncTask<HttpPostDb, Void, Void> {
	
	private String TAG = "DataPOST";

	public DataPOST()
	{
		
	}
	
	@Override
	protected Void doInBackground(HttpPostDb... params) {
		for(int i = 0; i < params.length ; i ++){
			HttpClient hC = new DefaultHttpClient();
			HttpPost hP = new HttpPost(params[i].getUri());
			Log.i(TAG, params[i].getUri() );

			/*if (params[i].getHeader() != null)
			{
				for(int j = 0; j < params[i].getHeader().size(); j ++)
				{
			hP.addHeader(params[i].getHeader().get(j).getName(),params[i].getHeader().get(j).getValue());
			Log.i(TAG, "header isn't null" );
				}
			}*/
			hP.setHeader("Accept", "application/json");
			hP.setHeader("Content-type", "application/json");
			
			try
			{
				if(params[i].getSerializedBody() != null || params[i].getSerializedBody() != "")
				{
				Log.i(TAG, params[i].getSerializedBody() + "");
				StringEntity se = new StringEntity(params[i].getSerializedBody(), HTTP.UTF_8);

				hP.setEntity(se);
				}
				
				HttpResponse hR = hC.execute(hP);
				String responseCheck = EntityUtils.toString(hR.getEntity());
				Log.i(TAG, responseCheck );
		        int status = hR.getStatusLine().getStatusCode();
				if(HttpStatus.SC_OK == status)
				{
					Log.i(TAG, "Upload Successful");
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
		return null;
	}

}
