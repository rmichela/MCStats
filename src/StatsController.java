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

import java.util.*;

public class StatsController {
	private HashMap<String, PlayerStatistics> stats;
	private StatsConfig config;
	
	public StatsController(StatsConfig config, HashMap<String, PlayerStatistics> stats) {
		this.stats = stats;
		this.config = config;
	}
	
	//Mark the player's connect time in the playclockStart field of stats
	public void logIn(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.sessionMarkTime = new Date();
		ps.playerGroups = player.getGroups();
		ps.lastLogin = new Date();
	}
	
	//Logs in any players who are active when the mod starts
	public void logInOnlinePlayers() {
		for(Player player : etc.getServer().getPlayerList()) {
			logIn(player);
		}
	}
	
	//Add total play time to the player's secondsOnServer
	public void logOut(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.flushSessionPlaytime();
		ps.sessionMarkTime = null;
	}
	
	//Logs out all players - called at shutdown
	public void logOutAllPlayers() {
		synchronized(stats) {
			for(PlayerStatistics ps : stats.values()) {
				ps.flushSessionPlaytime();
				ps.sessionMarkTime = null;
			}
		}
	}
	
	//Note that the player has traveled a meter
	public void travelAMeter(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.metersTraveled++;
	}
	
	//Note that the player has placed a block
	public void placeABlock(Player player, Block block) {
		if (block.getType() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.blocksPlaced.containsKey(block.getType())) {
				ps.blocksPlaced.put(block.getType(), 0L);
			}
			ps.blocksPlaced.put(block.getType(),
					ps.blocksPlaced.get(block.getType()) + 1);
		}
	}
	
	//Note that the player has destroyed a block
	public void destroyABlock(Player player, Block block) {
		if (block.getType() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.blocksDestroyed.containsKey(block.getType())) {
				ps.blocksDestroyed.put(block.getType(), 0L);
			}
			ps.blocksDestroyed.put(block.getType(),
					ps.blocksDestroyed.get(block.getType()) + 1);
		}
	}
	
	//Note that the player disposed of an item
	public void dropAnItem(Player player, Item item) { 
		if (item.getItemId() > 0) {
			if(ignorePlayer(player)) return;
			PlayerStatistics ps = getPlayerStats(player);
			if (!ps.itemsDropped.containsKey(item.getItemId())) {
				ps.itemsDropped.put(item.getItemId(), 0L);
			}
			ps.itemsDropped.put(item.getItemId(),
					ps.itemsDropped.get(item.getItemId()) + 1);
		}
	}
	
	//Note that the player has died
	public void die(Player player) {
		if(ignorePlayer(player)) return;
		PlayerStatistics ps = getPlayerStats(player);
		ps.deaths++;
	}
	
	//Note that the player killed something
	public void kill(Player attacker, LivingEntity victim) {
		if(ignorePlayer(attacker)) return;
		PlayerStatistics ps = getPlayerStats(attacker);
		
		if(victim.isPlayer()) {
			// Increment the correct player kill counter
			String victimName = victim.getPlayer().getName();
			if(!ps.playerKills.containsKey(victimName)) {
				ps.playerKills.put(victimName, 0L);
			}
			ps.playerKills.put(victimName, ps.playerKills.get(victimName) + 1);
		} else if(victim.isMob()) {
			String victimName = "Mob"; //((Mob)victim).getName();
			if(!ps.creatureKills.containsKey(victimName)) {
				ps.creatureKills.put(victimName, 0L);
			}
			ps.creatureKills.put(victimName, ps.creatureKills.get(victimName) + 1);
		} else if(victim.isAnimal()) {
			String victimName = "Animal"; //((Animal)victim).getName();
			if(!ps.creatureKills.containsKey(victimName)) {
				ps.creatureKills.put(victimName, 0L);
			}
			ps.creatureKills.put(victimName, ps.creatureKills.get(victimName) + 1);
		}
	}
	
	// Return a player's total play time
	public String getPlaytime(Player player) {
		PlayerStatistics ps = getPlayerStats(player);
		return ps.getTotalPlaytime();
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
	
	// Ignore players with no group if ignoreGrouplessPlayers is true.
	private boolean ignorePlayer(Player player) {
		if(config.getIgnoreGrouplessPlayers() && player.getGroups().length == 0 ) {
			return true;
		} else {
			return false;
		}
	}
}
