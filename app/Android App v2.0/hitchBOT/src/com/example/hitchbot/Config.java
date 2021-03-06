package com.example.hitchbot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	public static String HITCHBOT_ID = "10";
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
	private static final long TICKS_AT_EPOCH = 621355968000000000L;
	private static final long TICKS_PER_MILLISECOND = 10000;

	public static String searchName = "searchName";
	public static String cleverAPIKey = "pyuypc768beb3a174232f656d70173925fa40";//"t5xgnf9b0b27ed7690bea298d2210410286bd"; //"jjug8608a0d3fa3697162435a5a786e4c3995";
	public static String cleverDB = "hbPROD3.db";
	public static List<NameValuePair> cleverPair = new ArrayList<NameValuePair>();
	public static boolean accessok = true;

	public static String imagePOST = "http://hitchbotapi.azurewebsites.net/api/Image?HitchBotID=%s&timeTaken=%s";
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
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.GERMANY);
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

	public static long convertTicksToMillis(String ticks) {
		long tick;
		try {
			tick = Long.parseLong(ticks);
		} catch (Exception e) {
			tick = System.currentTimeMillis();
		}
		Date date = new Date((tick - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND);
		// System.out.println(date);

		TimeZone utc = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(utc);
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}
}
