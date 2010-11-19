import java.io.Serializable;
import java.util.*;

public class PlayerStats implements Serializable {
	private static final long serialVersionUID = 1L;
	public String playerName;
	public Date playerSince;
	public long secondsOnServer;
	public long metersTraveled;
	public HashMap<Integer, Long> blocksPlaced;
	public HashMap<Integer, Long> blocksDestroyed;
	public HashMap<Integer, Long> itemsDropped;
	
	public Date sessionMarkTime;
	
	public PlayerStats() {
		blocksPlaced = new HashMap<Integer, Long>();
		blocksDestroyed = new HashMap<Integer, Long>();
		itemsDropped = new HashMap<Integer, Long>();
	}
	
	public void flushSessionPlaytime() {
		if(sessionMarkTime != null)
		{
			Date now = new Date();
			long dif = (now.getTime() - sessionMarkTime.getTime()) / 1000;
			secondsOnServer += dif;
			sessionMarkTime = now;
		}
	}
}
