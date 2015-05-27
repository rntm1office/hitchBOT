package TransitionModels;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.hitchbot.Config;
import com.google.gson.Gson;

public class TabletPack extends InfoPack {
	public int HitchBotId;
	public int TimeUnix;
	public String Time;
	public double BatteryTemp;
	public double BatteryVoltage;
	public boolean IsCharging;
	public double BatteryPercentage;

	public TabletPack(double batTemp, double batVolt, boolean isCharging,
			double batPercent) {
		this.BatteryTemp = batTemp;
		this.BatteryVoltage = batVolt;
		this.IsCharging = isCharging;
		this.BatteryPercentage = batPercent;
	}

	protected TabletPack() {

	}

	@Override
	public JSONObject toJson() {
		Gson gs = new Gson();
		String json = gs.toJson(this);
		JSONObject jO;
		try {
			jO = new JSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jO;/*
		JSONObject jO = new JSONObject();
		try {
			jO.put("HitchBotId", Config.ID);
			jO.put("TimeUnix", System.currentTimeMillis() / 1000);
			jO.put("Time", Config.getUtcDate());
			
			jO.put("BatteryTemp", this.BatteryTemp);
			jO.put("BatteryVoltage", this.BatteryVoltage);
			jO.put("IsCharging", this.IsCharging);
			jO.put("BatteryPercentage ", this.BatteryPercentage);
			return jO;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;*/
	}
}
