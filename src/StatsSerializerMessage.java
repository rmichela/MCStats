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

public class StatsSerializerMessage {
	private OnlinePlayer[] playersOnline;
	private PlayerStatistics[] playerStats;
	
	public StatsSerializerMessage()
	{
		playersOnline = new OnlinePlayer[0];
		playerStats = new PlayerStatistics[0];
	}
	
	public OnlinePlayer[] getPlayersOnline() {
		OnlinePlayer noOne = new OnlinePlayer();
		noOne.playerName = "No One";
		
		return playersOnline.length == 0 ? new OnlinePlayer[]{noOne} : playersOnline;
	}
	
	public void setPlayersOnline(OnlinePlayer[] playerList) {
		this.playersOnline = playerList;
	}
	
	public PlayerStatistics[] getPlayerStats() {
		return playerStats;
	}
	
	public void setPlayerStats(PlayerStatistics[] playerStats) {
		this.playerStats = playerStats;
	}
}
