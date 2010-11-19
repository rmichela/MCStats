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
			log.log(Level.INFO, "MCStats persisting player statistics.");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(config.getStatsCacheFile()));
			out.writeObject(stats);
			out.close();
		} catch (Exception ex) {
			log.log(Level.SEVERE, "MCStats failed to persist player statistics to disk.", ex);
		}
	}
	
	public void saveUserFiles() {
		String xmlPath = config.getResourceSaveDirectory() + "/" + config.getStatsBaseResource() + ".xml";
		String jsonPath = config.getResourceSaveDirectory() + "/" + config.getStatsBaseResource() + ".json";
		
		try {
			//create the base resource directory if needed
			File dir = new File(config.getResourceSaveDirectory());
			if(!dir.exists())
			{
				dir.mkdirs();
			}
			
			//write the xml file
			PrintWriter pwxml = new PrintWriter(xmlPath);
			pwxml.write(StatsSerializer.statsAsXml(getRawStats()));
			pwxml.close();
			
			//write the json file
			PrintWriter pwjson = new PrintWriter(jsonPath);
			pwjson.write(StatsSerializer.statsAsJson(getRawStats()));
			pwjson.close();
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Error writing stats user files.", ex);
		}
	}
	
	//Get raw player stats
	public PlayerStatistics[] getRawStats() {
		synchronized (stats) {
			//Copies references to the PlayerStats objects into a new array, preserving thread safety.
			return stats.values().toArray(new PlayerStatistics[stats.size()]);
		}
	}
	
	@Override
	//called by the timer every few seconds
	public void run() {
		saveStats();
		saveUserFiles();
	}
}
