package com.example.hitchbot.Models;

import java.util.List;

import org.apache.http.NameValuePair;

import com.example.hitchbot.Config;

public class HttpPostDb {

	private int ID;
	private String uri;
	//0 is no, 1 is yes
	private int uploadToServer;
	private String dateCreated;
	private List<NameValuePair> body;
	private List<NameValuePair> header;
	// 0 image, 1 location, 2 spokenphrase, 3 heardphrase, 4 battery, 5 image, 6 audio
	//7 exception
	private int uploadType;
	
	public HttpPostDb(int ID, String uri, int uploadToServer, String dateCreated, int uT)
	{
		this.ID = ID;
		this.uploadToServer = uploadToServer;
		this.setUri(uri);
		this.dateCreated = dateCreated;
		this.setUploadType(uT);
	}
	
	public HttpPostDb(String uri, int uploadToServer, int uT)
	{
		this.uploadToServer = uploadToServer;
		this.setUri(uri);
		this.dateCreated = Config.getUtcDate();
		this.setUploadType(uT);
	}
	
	public HttpPostDb()
	{
		
	}
	
	public int getUploadToServer() {
		return uploadToServer;
	}
	public void setUploadToServer(int uploadToServer) {
		this.uploadToServer = uploadToServer;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<NameValuePair> getHeader() {
		return header;
	}

	public void setHeader(List<NameValuePair> header) {
		this.header = header;
	}

	public List<NameValuePair> getBody() {
		return body;
	}

	public void setBody(List<NameValuePair> body) {
		this.body = body;
	}

	public int getUploadType() {
		return uploadType;
	}

	public void setUploadType(int uploadType) {
		this.uploadType = uploadType;
	}

}
