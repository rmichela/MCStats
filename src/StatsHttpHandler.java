import java.io.*;
import java.util.*;
import com.sun.net.httpserver.*;

public class StatsHttpHandler implements HttpHandler {

	private StatsManager stats;
	private StatsConfig config;
	
	public StatsHttpHandler(StatsManager stats, StatsConfig config)
	{
		this.stats = stats;
		this.config = config;
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
        if(t.getRequestURI().getPath().endsWith(config.getStatsResource()))
        {
        	handleSuccess(t);
        }
        else
        {
        	handleFailure(t);
        }
	}
	
	private void handleSuccess(HttpExchange t) throws IOException {
		String response = buildXMLResponse();
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}
	
	private void handleFailure(HttpExchange t) throws IOException {
		String response = t.getRequestURI().getPath() + " NOT FOUND";
        t.sendResponseHeaders(404, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}

	private String buildXMLResponse() {
		//build the list of player names online. (java needs linq).
		List<String> playersOnline = new ArrayList<String>();
		for (Player p : etc.getServer().getPlayerList()) {
			playersOnline.add(p.getName());
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		sb.append("<mcstats>\n");
		
		sb.append(String.format("    <playersOnline>%s</playersOnline>\n", playersOnline.size()));
		
		for(PlayerStats s : stats.getAllPlayerStats()) {
			sb.append("    <player>\n");
			sb.append(String.format("        <name>%s</name>\n", s.playerName));
			sb.append(String.format("        <online>%s</online>\n", playersOnline.contains(s.playerName)));
			sb.append(String.format("        <playersince>%s</playersince>\n", s.playerSince.toString()));
			sb.append(String.format("        <playtime>%s</playtime>\n", secondsToTimestamp(s.secondsOnServer)));
			sb.append(String.format("        <metersTraveled>%s</metersTraveled>\n", s.metersTraveled));
			sb.append(String.format("        <blocksPlaced>%s</blocksPlaced>\n", MapCount(s.blocksPlaced)));
			sb.append(String.format("        <blocksDestroyed>%s</blocksDestroyed>\n", MapCount(s.blocksDestroyed)));
			sb.append(String.format("        <itemsDropped>%s</itemsDropped>\n", MapCount(s.itemsDropped)));
			sb.append("    </player>\n");
		}
		
		sb.append("</mcstats>");
		return sb.toString();
	}
	
	private String secondsToTimestamp(long seconds) {
		long hours = seconds / 3600;
		seconds %= 3600;
		long minutes = seconds / 60;
		return String.format("%s hours and %s minutes", hours, minutes);
	}
	
	private long MapCount(HashMap<Integer, Long> map) {
		long acc = 0L;
		
		for(long l : map.values()) {
			acc += l;
		}		
		
		return acc;
	}
}
