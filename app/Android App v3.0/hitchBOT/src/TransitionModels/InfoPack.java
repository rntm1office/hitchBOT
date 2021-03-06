package TransitionModels;

import org.json.JSONObject;

import com.example.hitchbot.Config;

public abstract class InfoPack {
	
	public int HitchBotId; 
	public long TimeUnix;
	public String Time;
	public String TabletSerial;
	
	public InfoPack(){
		this.HitchBotId = Config.ID;
		this.Time = Config.getUtcDate();
		this.TimeUnix = System.currentTimeMillis();
		this.TabletSerial = Config.tabletID;
	}
	
	public abstract JSONObject toJson();
	
}
