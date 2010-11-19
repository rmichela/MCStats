import java.util.*;

public class StatsManager {
	private HashMap<String, PlayerStats> stats;
	
	public StatsManager(StatsConfig config, HashMap<String, PlayerStats> stats) {
		this.stats = stats;
	}
	
	//Mark the player's connect time in the playclockStart field of stats
	public void logIn(Player player) {
		PlayerStats ps = getPlayerStats(player);
		ps.sessionMarkTime = new Date();
	}
	
	//Add total play time to the player's secondsOnServer
	public void logOut(Player player) {
		PlayerStats ps = getPlayerStats(player);
		ps.flushSessionPlaytime();
		ps.sessionMarkTime = null;
	}
	
	//Note that the player has traveled a meter
	public void travelAMeter(Player player) {
		PlayerStats ps = getPlayerStats(player);
		ps.metersTraveled++;
	}
	
	//Note that the player has placed a block
	public void placeABlock(Player player, Block block) {
		PlayerStats ps = getPlayerStats(player);
		if(!ps.blocksPlaced.containsKey(block.getType())) {
			ps.blocksPlaced.put(block.getType(), 0L);
		}
		ps.blocksPlaced.put(block.getType(), ps.blocksPlaced.get(block.getType()) + 1);
	}
	
	//Note that the player has destroyed a block
	public void destroyABlock(Player player, Block block) {
		PlayerStats ps = getPlayerStats(player);
		if(!ps.blocksDestroyed.containsKey(block.getType())) {
			ps.blocksDestroyed.put(block.getType(), 0L);
		}
		ps.blocksDestroyed.put(block.getType(), ps.blocksDestroyed.get(block.getType()) + 1);
	}
	
	//Note that the player disposed of an item
	public void dropAnItem(Player player, Item item) { 
		PlayerStats ps = getPlayerStats(player);
		if(!ps.itemsDropped.containsKey(item.getItemId())) {
			ps.itemsDropped.put(item.getItemId(), 0L);
		}
		ps.itemsDropped.put(item.getItemId(), ps.itemsDropped.get(item.getItemId()) + 1);
	}
	
	//Get all player stats
	public PlayerStats[] getAllPlayerStats() {
		synchronized (stats) {
			//Copies references to the PlayerStats objects into a new array, preserving thread safety.
			return stats.values().toArray(new PlayerStats[stats.size()]);
		}
	}
	
	private PlayerStats getPlayerStats(Player player)
	{
		if(!stats.containsKey(player.getName()))
		{
			synchronized (stats) {
				PlayerStats newStats = new PlayerStats();
				newStats.playerName = player.getName();
				newStats.playerSince = new Date(); //initialize to now
				stats.put(player.getName(), newStats);
			}
		}
		return stats.get(player.getName());
	}
}
