import java.util.*;

public class StatsController {
	private HashMap<String, PlayerStatistics> stats;
	
	public StatsController(StatsConfig config, HashMap<String, PlayerStatistics> stats) {
		this.stats = stats;
	}
	
	//Mark the player's connect time in the playclockStart field of stats
	public void logIn(Player player) {
		PlayerStatistics ps = getPlayerStats(player);
		ps.sessionMarkTime = new Date();
	}
	
	//Add total play time to the player's secondsOnServer
	public void logOut(Player player) {
		PlayerStatistics ps = getPlayerStats(player);
		ps.flushSessionPlaytime();
		ps.sessionMarkTime = null;
	}
	
	//Note that the player has traveled a meter
	public void travelAMeter(Player player) {
		PlayerStatistics ps = getPlayerStats(player);
		ps.metersTraveled++;
	}
	
	//Note that the player has placed a block
	public void placeABlock(Player player, Block block) {
		PlayerStatistics ps = getPlayerStats(player);
		if(!ps.blocksPlaced.containsKey(block.getType())) {
			ps.blocksPlaced.put(block.getType(), 0L);
		}
		ps.blocksPlaced.put(block.getType(), ps.blocksPlaced.get(block.getType()) + 1);
	}
	
	//Note that the player has destroyed a block
	public void destroyABlock(Player player, Block block) {
		PlayerStatistics ps = getPlayerStats(player);
		if(!ps.blocksDestroyed.containsKey(block.getType())) {
			ps.blocksDestroyed.put(block.getType(), 0L);
		}
		ps.blocksDestroyed.put(block.getType(), ps.blocksDestroyed.get(block.getType()) + 1);
	}
	
	//Note that the player disposed of an item
	public void dropAnItem(Player player, Item item) { 
		PlayerStatistics ps = getPlayerStats(player);
		if(!ps.itemsDropped.containsKey(item.getItemId())) {
			ps.itemsDropped.put(item.getItemId(), 0L);
		}
		ps.itemsDropped.put(item.getItemId(), ps.itemsDropped.get(item.getItemId()) + 1);
	}
	
	private PlayerStatistics getPlayerStats(Player player)
	{
		if(!stats.containsKey(player.getName()))
		{
			synchronized (stats) {
				PlayerStatistics newStats = new PlayerStatistics();
				newStats.playerName = player.getName();
				newStats.playerSince = new Date(); //initialize to now
				stats.put(player.getName(), newStats);
			}
		}
		return stats.get(player.getName());
	}
}
