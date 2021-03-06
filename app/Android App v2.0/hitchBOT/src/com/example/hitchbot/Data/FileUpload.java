package com.example.hitchbot.Data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.example.hitchbot.Config;
import com.example.hitchbot.Models.FileUploadDb;

import android.util.Log;

public class FileUpload implements Runnable {

	private URL connectURL;
	private String responseString;
	private String Title;
	private String Description;
	private String filePath;
	private static String TAG = "FileUpload";
	private String fileExtension;
	private FileUploadDb fileUpload;

	byte[] dataToServer;
	FileInputStream fileInputStream = null;

	//fileType =1 corresponds to image, fileType = 0 corresponds to audio
	public FileUpload(String urlString, FileUploadDb fileUpload) {
		String vTitle = "myfiletitle"; 
		String vDesc = "lifestoryORimage";
		String filePath = fileUpload.getUri();
		this.fileUpload = fileUpload;
		int fileType = fileUpload.getFileType();
		try {
			connectURL = new URL(urlString);
			Title = vTitle;
			Description = vDesc;
			this.filePath = filePath;
			switch(fileType)
			{
			case 0:
				fileExtension = ".mp4";
				break;
			case 1:
				fileExtension = ".jpg";
				break;
				default:
					fileExtension = ".mp4";
					break;
			}
		} catch (Exception ex) {
			Log.i(TAG, "URL Malformatted");
		}
	}

	public void send_Now(FileInputStream fStream) {
		fileInputStream = fStream;
		Sending();
	}

	public void Sending() {
		Thread t = new Thread(this);
		t.start();

	}

	@Override
	public void run() {
		String iFileName = Config.getUtcDate() + fileExtension;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			Log.e(TAG, "Starting Http File Sending to URL");

			// Open a HTTP connection to the URL
			HttpURLConnection conn = (HttpURLConnection) connectURL
					.openConnection();
			Log.i(TAG, connectURL.toString());

			// Allow Inputs
			conn.setDoInput(true);

			// Allow Outputs
			conn.setDoOutput(true);

			// Don't use a cached copy.
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");

			conn.setRequestProperty("Connection", "Keep-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"title\""
					+ lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(Title);
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"description\""
					+ lineEnd);
			dos.writeBytes(lineEnd);
			dos.writeBytes(Description);
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + lineEnd);

			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ iFileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			Log.e(TAG, "Headers are written");

			// create a buffer of maximum size
			int bytesAvailable = fileInputStream.available();

			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			// read file and write it into form...
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			fileInputStream.close();

			dos.flush();

			Log.e(TAG,
					"File Sent, Response: "
							+ String.valueOf(conn.getResponseCode()));

			InputStream is = conn.getInputStream();

			// retrieve the response from server
			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			Log.i(TAG, s);
			dos.close();
			if (s.contains("true") || conn.getResponseCode() == 200 || s.contains("File uploaded.")) {
				long size = new File(filePath).length();
				Log.i(TAG, "to be deleted" + " Size = " + size);
				boolean deleted = new File(filePath).delete();
				Config.dQ.markAsUploadedToServer(fileUpload);
			}
		} catch (MalformedURLException ex) {
			Log.e(TAG, "URL error: " + ex.getMessage(), ex);
			//TODO
		}

		catch (IOException ioe) {
			Log.e(TAG, "IO error: " + ioe.getMessage(), ioe);
			Config.dQ.launchFileMissles();
		}
	}
}
