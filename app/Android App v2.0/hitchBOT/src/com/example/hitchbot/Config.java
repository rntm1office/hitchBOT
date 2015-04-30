package com.example.hitchbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.NameValuePair;

import com.example.hitchbot.Speech.CleverScriptHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class Config {

	public static HitchActivity context = null;
	public static String HITCHBOT_ID = "6";
	public static int HOUR = 1000 * 60 * 60;
	public static int FIFTEEN_MINUTES = 1000 * 60 * 15;
	public static int HALF_HOUR = 1000 * 60 * 30;
	public static int TEN_MINUTES = 1000 * 60 * 30;
	public static int THREE_HOURS = 1000 * 60 * 60 * 3;
	public static int THIRTY_SECONDS = 1000 * 30;
	public static int ONE_MINUTE = 1000 * 60;
	public static int FORTYFIVE_MINUTES = 1000 * 60 * 45;
	public static DatabaseQueue dQ;
	public static CleverScriptHelper cH = null;

	public static String searchName = "searchName";
	public static String cleverAPIKey = "5iz418c4102c2bc496902e135045ba9fe87f7";//"jptdn4c8be32ab94a1050ee3551af67adef87";// 
	public static String cleverDB = "hbEnglish.db"; //"phonemetest.db";
	public static List<NameValuePair> cleverPair = new ArrayList<NameValuePair>();
	
	
	public static String imagePOST = "http://hitchbotapi.azurewebsites.net/api/Image";
	public static String cleverGET = "http://hitchbotapi.azurewebsites.net/api/hitchBOT";
	public static String audioPOST = "http://hitchbotapi.azurewebsites.net/api/hitchBOT";
	public static String batteryPOST = "http://hitchbotapi.azurewebsites.net/api/Tablet?HitchBotID=%s&timeTaken=%s&isPluggedIn=%s&BatteryVoltage=%s&BatteryPercent=%s&BatteryTemp=%s";
	public static String locationPOST_FINE = "http://hitchbotapi.azurewebsites.net/api/Location?HitchBotID=%s&Latitude=%s&Longitude=%s&Altitude=%s&Accuracy=%s&Velocity=%s&TakenTime=%s";
	public static String locationPOST_COURSE = "http://hitchbotapi.azurewebsites.net/api/Location?HitchBotID=%s&Latitude=%s&Longitude=%s&TakenTime=%s";
	public static String spokePOST = "http://hitchbotapi.azurewebsites.net/api/Conversation?HitchBotId=%s&SpeechSaid=%s&TimeTaken=%s";
	public static String heardPOST = "http://hitchbotapi.azurewebsites.net/api/Conversation?HitchBotId=%s&SpeechHeard=%s&TimeTaken=%s";
	public static String startPOST = "http://hitchbotapi.azurewebsites.net/api/Conversation?HitchBotID=%s&StartTime=%s";
	public static String exceptionPOST = "http://hitchbotapi.azurewebsites.net/api/Exception?HitchBotID=%s&Message=%s&TimeOccured=%s";
	
	public static String getUtcDate() {
		final Date date = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMANY);
		final TimeZone utc = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(utc);
		return sdf.format(date);
	}

	public static boolean networkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) Config.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
