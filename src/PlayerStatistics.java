import java.io.Serializable;
import java.util.*;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class PlayerStatistics implements Serializable {
	private static final long serialVersionUID = 1L;
	public String playerName;
	public Date playerSince;
	public long secondsOnServer;
	public long metersTraveled;
	public HashMap<Integer, Long> blocksPlaced;
	public HashMap<Integer, Long> blocksDestroyed;
	public HashMap<Integer, Long> itemsDropped;
	
	public Date sessionMarkTime;
	
	public PlayerStatistics() {
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
	
	// Bean getters for JSON/XML serialization
	public String getPlayerName() { 
		return playerName; 
	}
	
	@XmlElement
	public boolean getIsOnline() {
		List<String> playersOnline = new ArrayList<String>();
		for (Player p : etc.getServer().getPlayerList()) {
			playersOnline.add(p.getName());
		}
		
		return playersOnline.contains(playerName);
	}

	@XmlElement
	public Date getPlayerSince() {
		return playerSince;
	}

	@XmlElement
	public String getTotalPlaytime() {
		return secondsToTimestamp(secondsOnServer);
	}

	@XmlElement
	public long getMetersTraveled() {
		return metersTraveled;
	}
	
	@XmlElement
	public long getBlocksPlaced() {
		return mapCount(blocksPlaced);
	}

	public HashMap<Integer, Long> getBlocksPlacedDetails() {
		return blocksPlaced;
	}
	
	@XmlElement
	public long getBlocksDestroyed() {
		return mapCount(blocksDestroyed);
	}

	public HashMap<Integer, Long> getBlocksDestroyedDetails() {
		return blocksDestroyed;
	}
	
	@XmlElement
	public long getItemsDropped() {
		return mapCount(itemsDropped);
	}

	public HashMap<Integer, Long> getItemsDroppedDetails() {
		return itemsDropped;
	}
	
	// Utility methods
	private String secondsToTimestamp(long seconds) {
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		return String.format("%s.%s hours", hours, (int)Math.floor(minutes/60.0*100.0));
	}
	
	private long mapCount(HashMap<Integer, Long> map) {
		long acc = 0L;
		
		for(long l : map.values()) {
			acc += l;
		}		
		
		return acc;
	}
	
}
