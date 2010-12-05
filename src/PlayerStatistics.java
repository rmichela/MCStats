//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="player")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PlayerStatistics implements Serializable {
	private static final long serialVersionUID = 1L;
	public String playerName;
	public String[] playerGroups;
	public Date playerSince;
	public Date lastLogin;
	public long secondsOnServer;
	public long metersTraveled;
	public HashMap<Integer, Long> blocksPlaced;
	public HashMap<Integer, Long> blocksDestroyed;
	public HashMap<Integer, Long> itemsDropped;
	public long deaths;
	
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
	@XmlElement
	public String getPlayerName() { 
		return playerName; 
	}
	
	@XmlElement
	public String[] getPlayerGroups() {
		return playerGroups;
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
	public String getPlayerSince() {
		return playerSince != null ? Long.toString(playerSince.getTime()/1000L) : "";
	}
	
	@XmlElement
	public String getLastLogin() {
		return lastLogin != null ? Long.toString(lastLogin.getTime()/1000L) : "";
	}

	@XmlElement
	public String getTotalPlaytime() {
		return secondsToTimestamp(secondsOnServer);
	}
	
	@XmlElement
	public String getTotalPlaytimeSeconds() {
		return Long.toString(secondsOnServer);
	}
	
	@XmlElement
	public String getSessionPlaytime() {
		if(getIsOnline()) {
			long seccondsInSession = (new Date().getTime() - lastLogin.getTime()) / 1000;
			return secondsToTimestamp(seccondsInSession);
		} else {
			return "";
		}
	}
	
	@XmlElement
	public String getSessionPlaytimeSeconds() {
		if(getIsOnline()) {
			long secondsInSession = (new Date().getTime() - lastLogin.getTime()) / 1000;
			return Long.toString(secondsInSession);
		} else {
			return "-1";
		}
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
	
	@XmlElement
	public long getDeaths() {
		return deaths;
	}
	
	// Utility methods
	private String secondsToTimestamp(long seconds) {
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		return String.format("%s.%02d hours", hours, (int)Math.floor(minutes/60.0*100.0));
	}
	
	private long mapCount(HashMap<Integer, Long> map) {
		long acc = 0L;
		
		for(long l : map.values()) {
			acc += l;
		}		
		
		return acc;
	}
}
