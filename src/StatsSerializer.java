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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
	
	public static String statsAsJavascript(PlayerStatistics[] rawStats) {
		String json = statsAsJson(rawStats);
		return "var mcStatsRawData = " + json + ";";
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
	
	public static String statsAsHtml() {
		try {
			InputStream reader = StatsSerializer.class
					.getResourceAsStream("/mcstats.html");
			return convertStreamToString(reader);
		} catch (IOException e) {
			return "";
		}
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
	
	/*
	 * To convert the InputStream to String we use the
	 * Reader.read(char[] buffer) method. We iterate until the
	 * Reader return -1 which means there's no more data to
	 * read. We use the StringWriter class to produce the string.
	 * 
	 * Why the frack isn't there a readEntireStream method? Grr. Java you are so left behind.
	 */
    private static String convertStreamToString(InputStream is) throws IOException {

    	if (is != null) {
	    Writer writer = new StringWriter();
	
	    char[] buffer = new char[1024];
	    try {
	        Reader reader = new BufferedReader(
	                new InputStreamReader(is, "UTF-8"));
	        int n;
	        while ((n = reader.read(buffer)) != -1) {
	            writer.write(buffer, 0, n);
	        }
	    } finally {
	        is.close();
	    }
	    	return writer.toString();
		} else {        
		    return "";
		}
	}
}
