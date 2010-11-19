import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StatsPersister extends TimerTask {

	private StatsConfig config;
	private Logger log;
	private HashMap<String, PlayerStats> stats;
	Timer t = new Timer();
	
	@SuppressWarnings("unchecked")
	public StatsPersister(StatsConfig config, Logger log) {
		this.config = config;
		this.log = log;
		
		//load the stats from disk
		log.log(Level.INFO, "MCStats restoring player statistics.");
		if(new File(config.getStatsCacheFile()).exists())
		{
			try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(config.getStatsCacheFile()));
				stats = (HashMap<String, PlayerStats>) in.readObject();
			} catch (Exception e) {
				log.log(Level.SEVERE, "MCStats failed to restore player statistics.", e);
				stats = new HashMap<String, PlayerStats>();
			}
		} else {
			log.log(Level.INFO, "MCStats did not find previously stored player statistics - creating a new statistics cache.");
			stats = new HashMap<String, PlayerStats>();
			saveStats();
		}
		
		//start the save timer
		
	}
	
	public HashMap<String, PlayerStats> getStats() {
		return stats;
	}
	
	public void saveStats() {
		
		//Update all the secondsOnServer fields
		for(PlayerStats s : stats.values())
		{
			s.flushSessionPlaytime();
		}
		
		try {
			log.log(Level.INFO, "MCStats persisting player statistics.");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(config.getStatsCacheFile()));
			out.writeObject(stats);
			out.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "MCStats failed to persist player statistics to disk.", e);
		}
	}
	
	public void startPersisting() {
		long period = config.getStatsSaveSeconds() * 1000;
		t.scheduleAtFixedRate(this, period, period);
	}
	
	public void stopPersisting() {
		t.cancel();
		t = new Timer();
	}
	
	@Override
	//called by the timer every few seconds
	public void run() {
		saveStats();
	}
}
