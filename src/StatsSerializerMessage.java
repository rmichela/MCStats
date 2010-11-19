import javax.xml.bind.annotation.*;

@XmlRootElement(name="MCStats")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StatsSerializerMessage {
	private String[] playersOnline;
	private PlayerStatistics[] playerStats;
	
	public StatsSerializerMessage()
	{
		playersOnline = new String[0];
		playerStats = new PlayerStatistics[0];
	}
	
	@XmlElement
	public String[] getPlayersOnline() {
		return playersOnline.length == 0 ? new String[]{"No One"} : playersOnline;
	}
	public void setPlayersOnline(String[] playerList) {
		this.playersOnline = playerList;
	}
	
	@XmlElement
	public PlayerStatistics[] getPlayerStats() {
		return playerStats;
	}
	public void setPlayerStats(PlayerStatistics[] playerStats) {
		this.playerStats = playerStats;
	}
}
