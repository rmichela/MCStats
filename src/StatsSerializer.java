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
		return html;
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
    
    private static String html = 
"<!DOCTYPE HTML>\n" +
"<html>\n" +
"	<head>\n" +
"		<style type='text/css'>\n" +
"			h1 {\n" +
"				font: bold 24px verdana, arial, helvetica, sans-serif;\n" +
"				color: #363636;\n" +
"				text-align:center;\n" +
"				width: 80%;\n" +
"				margin-left: 10%;\n" +
"				margin-right: 10%;\n" +
"				min-width: 600px;\n" +
"			}\n" +
"\n" +			
"			table {\n" +
"				border-collapse: collapse;\n" +
"				border: 1px solid #666666;\n" +
"				font: normal 11px verdana, arial, helvetica, sans-serif;\n" +
"				color: #363636;\n" +
"				background: #f6f6f6;\n" +
"				text-align:left;\n" +
"				width: 80%;\n" +
"				margin-left: 10%;\n" +
"				margin-right: 10%;\n" +
"				min-width: 600px;\n" +
"			}\n" +
"\n" +
"			thead {\n" +
"				background: #cfe7fa; /* old browsers */\n" +
"				background: -moz-linear-gradient(top, #cfe7fa 0%, #6393c1 100%); /* firefox */\n" +
"				background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#cfe7fa), color-stop(100%,#6393c1)); /* webkit */\n" +
"				filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#cfe7fa', endColorstr='#6393c1',GradientType=0 ); /* ie */\n" +
"				text-align:left;\n" +
"				height:30px;\n" +
"			}\n" +
"\n" +			
"			thead th {\n" +
"				padding:5px;\n" +
"			}\n" +
"\n" +			
"			tr.odd {\n" +
"				background: #f1f1f1;\n" +
"			}\n" +
"\n" +			
"			tbody th, tbody td {\n" +
"				padding:5px;\n" +
"			}\n" +
"\n" +			
"			.pName {\n" +
"				font-weight:bold;\n" +
"			}\n" +
"\n" +			
"			.pOnline {\n" +
"				padding: 3px;\n" +
"			}\n" +
"\n" +			
"			#stats td {\n" +
"				white-space:nowrap;\n" +
"			}\n" +
"		</style>\n" +
"	</head>\n" +
"	<body>\n" +
"		<h1 class='center'>Minecraft Server Statistics</h1>\n" +
"		<table id='online' class='center'>\n" +
"			<thead>\n" +
"				<tr>\n" +
"					<th>Players Online</th>\n" +
"				</tr>\n" +
"			</thead>\n" +
"			<tbody>\n" +
"				<tr>\n" +
"					<td id='playersOnlineList'>\n" +
"					</td>\n" +
"				</tr>\n" +
"			</tbody\n" +
"		</table>\n" +
"		<br/>	\n" +
"		<table id='stats' class='center'>\n" +
"			<thead>\n" +
"				<tr>\n" +
"					<th>Name</th>\n" +
"					<th>Placed</th>\n" +
"					<th>Destroyed</th>\n" +
"					<th>Dropped</th>\n" +
"					<th>Meters Traveled</th>\n" +
"					<th>Player Since</th>\n" +
"					<th>Playtime</th>\n" +
"				</tr>\n" +
"			</thead>\n" +
"			<tbody id='statsTable'></tbody>\n" +
"		</table>\n" +
"\n" +
"		<script src='mcstats.js'></script>\n" +
"		<script>\n" +
"			//build the Players Online list\n" +
"			var playersOnline = document.getElementById('playersOnlineList');\n" +
"			mcStatsRawData.playersOnline.sort(strSortNoCase);\n" +
"			for(i in mcStatsRawData.playersOnline)\n" +
"			{\n" +
"				var li = document.createElement('span');\n" +
"				li.setAttribute('class', 'pOnline');\n" +
"				li.innerHTML = mcStatsRawData.playersOnline[i];\n" +
"				li.innerHTML += ' ';\n" +
"				playersOnline.appendChild(li);\n" +
"			}			\n" +
"\n" +
"			//Build the player stats table\n" +
"			mcStatsRawData.playerStats.sort(statsSort);\n" +
"			for(j in mcStatsRawData.playerStats)\n" +
"			{\n" +
"				var ps = mcStatsRawData.playerStats[j];\n" +
"				var tr = document.createElement('tr');\n" +
"\n" +
"				var playerName = tr.insertCell(0);\n" +
"				playerName.setAttribute('class', 'pName');\n" +
"				playerName.innerHTML = ps.playerName;\n" +
"\n" +
"				var placed = tr.insertCell(1);\n" +
"				placed.innerHTML = ps.blocksPlaced;\n" +
"\n" +
"				var destroyed = tr.insertCell(2);\n" +
"				destroyed.innerHTML = ps.blocksDestroyed;\n" +
"\n" +
"				var dropped = tr.insertCell(3);\n" +
"				dropped.innerHTML = ps.itemsDropped;\n" +
"\n" +
"				var traveled = tr.insertCell(4);\n" +
"				traveled.innerHTML = ps.metersTraveled;\n" +
"\n" +
"				var playersince = tr.insertCell(5);\n" +
"				playersince.innerHTML = ps.playerSince.getMonth() + '/' + ps.playerSince.getDate() + '/' + ps.playerSince.getFullYear();\n" +
"\n" +
"				var playtime = tr.insertCell(6);\n" +
"				playtime.innerHTML = ps.totalPlaytime;\n" +
"\n" +
"				document.getElementById('statsTable').appendChild(tr);\n" +
"			}\n" +
"\n" +
"			function strSortNoCase(a, b) {\n" +
"				a = a.toLowerCase(); b = b.toLowerCase();\n" +
"				if (a>b) return 1;\n" +
"				if (a <b) return -1;\n" +
"				return 0; \n" +
"			}\n" +
"\n" +
"			function statsSort(a, b) {\n" +
"				return strSortNoCase(a.playerName, b.playerName);\n" +
"			}\n" +
"		</script>\n" +
"	</body>\n" +
"</html>";
}
