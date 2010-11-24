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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StatsModel extends TimerTask {

	private StatsConfig config;
	private Logger log;
	private HashMap<String, PlayerStatistics> stats;
	Timer t = new Timer();
	
	@SuppressWarnings("unchecked")
	public StatsModel(StatsConfig config, Logger log) {
		this.config = config;
		this.log = log;
		
		//load the stats from disk
		log.log(Level.INFO, "MCStats restoring player statistics.");
		if(new File(config.getStatsCacheFile()).exists())
		{
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(config.getStatsCacheFile()));
				stats = (HashMap<String, PlayerStatistics>) in.readObject();
				cleanStats();
			} catch (Exception e) {
				log.log(Level.SEVERE, "MCStats failed to restore player statistics.", e);
				stats = new HashMap<String, PlayerStatistics>();
			}
		} else {
			log.log(Level.INFO, "MCStats did not find previously stored player statistics - creating a new statistics cache.");
			stats = new HashMap<String, PlayerStatistics>();
			saveStats();
		}
	}
	
	/*
	 * cleanStats is called after load and fixes any glitches that may be in the stats model
	 */
	private void cleanStats() {
		//1. Remove any block types with id less than 1
		for(PlayerStatistics ps : stats.values()) {
			ps.blocksPlaced.remove(0);
			ps.blocksPlaced.remove(-1);
			ps.blocksDestroyed.remove(0);
			ps.blocksDestroyed.remove(-1);
			ps.itemsDropped.remove(0);
			ps.itemsDropped.remove(-1);
		}
	}
	
	public HashMap<String, PlayerStatistics> getStats() {
		return stats;
	}
	
	public void startPersisting() {
		long period = config.getSecondsBetweenSaves() * 1000;
		t.scheduleAtFixedRate(this, period, period);
	}
	
	public void stopPersisting() {
		t.cancel();
		t = new Timer();
	}
	
	public void saveStats() {
		
		//Update all the secondsOnServer fields
		for(PlayerStatistics s : stats.values())
		{
			s.flushSessionPlaytime();
		}
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(config.getStatsCacheFile()));
			out.writeObject(stats);
			out.close();
		} catch (Exception ex) {
			log.log(Level.SEVERE, "MCStats failed to persist player statistics to disk.", ex);
		}
	}
	
	public void saveUserFiles() {
		try {
			//create the base resource directory if needed
			File dir = new File(config.getResourceSaveDirectory());
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			saveUserFile(".xml", StatsSerializer.statsAsXml(getRawStats()), true);
			saveUserFile(".json", StatsSerializer.statsAsJson(getRawStats()), true);
			saveUserFile(".js", StatsSerializer.statsAsJavascript(getRawStats()), true);
			saveUserFile(".html", StatsSerializer.statsAsHtml(config), true);
			
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error writing stats user files.", ex);
		}
	}
	
	public void purgePlayer(String playerName) {
		if(stats.containsKey(playerName)) {
			stats.remove(playerName);
			log.log(Level.INFO, "Purging player " + playerName);
		}
	}
	
	private void saveUserFile(String extension, String content, boolean overwrite) throws IOException
	{
		String path = config.getResourceSaveDirectory() + "/" + config.getStatsBaseResource() + extension;
		//create/overwrite the file iff it doesn't exist or overwrite is true
		if(!new File(path).exists() || overwrite) {
			PrintWriter pw = new PrintWriter(path);
			pw.write(content);
			pw.close();
		}
	}
	
	//Get raw player stats
	public List<PlayerStatistics> getRawStats() {
		synchronized (stats) {
			//Copies references to the PlayerStats objects into a new array, preserving thread safety.
			return new ArrayList<PlayerStatistics>(stats.values());
		}
	}
	
	@Override
	//called by the timer every few seconds
	public void run() {
		saveStats();
		saveUserFiles();
	}
}
