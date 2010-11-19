import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


public class StatsSerializer {
	
	public static String statsAsJson(PlayerStatistics[] rawStats) {
		String response;
		try {
			response = JSONEncoder.getJSONEncoder(StatsSerializerMessage.class).encode(buildStatsMessage(rawStats));
		} catch (Exception ex) {
			response = ex.getMessage();
		}
		
		return response;
	}
	
	public static String statsAsXml(PlayerStatistics[] rawStats) {
		String response;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			JAXBContext jc = JAXBContext.newInstance(StatsSerializerMessage.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(buildStatsMessage(rawStats), out);;
			
			response = out.toString();
		} catch (Exception ex) {
			response = ex.getMessage();
		}
		
		return response;
	}
	
	private static StatsSerializerMessage buildStatsMessage(PlayerStatistics[] rawStats) {
		//build the list of player names online. (java needs linq).
		List<String> playersOnline = new ArrayList<String>();
		for (Player p : etc.getServer().getPlayerList()) {
			playersOnline.add(p.getName());
		}
		
		StatsSerializerMessage message = new StatsSerializerMessage();
		message.setPlayersOnline(playersOnline.toArray(new String[playersOnline.size()]));
		message.setPlayerStats(rawStats);
		return message;
	}
}
