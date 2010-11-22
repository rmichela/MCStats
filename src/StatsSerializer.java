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
		StringBuilder response = new StringBuilder();
		StatsSerializerMessage message = buildStatsMessage(rawStats);
		
		try {
			response.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			response.append("<mcStats>");
			response.append("<playersOnline>");
			
			for(OnlinePlayer p : message.getPlayersOnline()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBContext jc = JAXBContext.newInstance(StatsSerializerMessage.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
				marshaller.marshal(p, out);
				response.append(out.toString());
			}
			
			response.append("</playersOnline>");
			response.append("<playerStats>");
			
			for(PlayerStatistics p : message.getPlayerStats()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBContext jc = JAXBContext.newInstance(StatsSerializerMessage.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
				marshaller.marshal(p, out);
				response.append(out.toString());
			}
			
			response.append("</playerStats>");
			response.append("</mcStats>");
		} catch (Exception ex) {
			response.append(ex.getMessage());
		}
		
		return response.toString();
	}
	
	public static String statsAsHtml() {
		return html;
	}
	
	private static StatsSerializerMessage buildStatsMessage(PlayerStatistics[] rawStats) {
		//build the list of player names online. (java needs linq).
		List<OnlinePlayer> playersOnline = new ArrayList<OnlinePlayer>();
		for (Player p : etc.getServer().getPlayerList()) {
			OnlinePlayer op = new OnlinePlayer();
			op.playerName = p.getName();
			op.groups = p.getGroups();
			playersOnline.add(op);
		}
		
		StatsSerializerMessage message = new StatsSerializerMessage();
		message.setPlayersOnline(playersOnline.toArray(new OnlinePlayer[playersOnline.size()]));
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
"		<h1>Minecraft Server Statistics</h1>\n" +
"		<table id='online'>\n" +
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
"		<table id='stats'>\n" +
"			<thead>\n" +
"				<tr>\n" +
"					<th>Name</th>\n" +
"					<th>Groups</th>\n" +
"					<th>Placed</th>\n" +
"					<th>Destroyed</th>\n" +
"					<th>Dropped</th>\n" +
"					<th>Meters Traveled</th>\n" +
"					<th>Player Since</th>\n" +
"					<th>Last Login</th>\n" +
"					<th>Total Playtime</th>\n" +
"					<th>Session Playtime</th>\n" +
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
"				var span = document.createElement('span');\n" +
"				span.setAttribute('class', 'pOnline' + groupConcat(mcStatsRawData.playersOnline[i].groups));\n" +
"				span.innerHTML = mcStatsRawData.playersOnline[i].playerName;\n" +
"				span.innerHTML += ' ';\n" +
"				playersOnline.appendChild(span);\n" +
"			}			\n" +
"\n" +
"			//Build the player stats table\n" +
"			mcStatsRawData.playerStats.sort(statsSort);\n" +
"			for(j in mcStatsRawData.playerStats)\n" +
"			{\n" +
"				var col = 0;\n"+
"\n" +
"				var ps = mcStatsRawData.playerStats[j];\n" +
"				var tr = document.createElement('tr');\n" +
"\n" +
"				var playerName = tr.insertCell(col++);\n" +
"				playerName.setAttribute('class', 'pName' + groupConcat(ps.playerGroups));\n" +
"				playerName.innerHTML = ps.playerName;\n" +
"\n" +
"				var playerGroups = tr.insertCell(col++);\n" +
"				playerGroups.innerHTML = groupConcat(ps.playerGroups);\n" +
"\n" +
"				var placed = tr.insertCell(col++);\n" +
"				placed.innerHTML = ps.blocksPlaced;\n" +
"\n" +
"				var destroyed = tr.insertCell(col++);\n" +
"				destroyed.innerHTML = ps.blocksDestroyed;\n" +
"\n" +
"				var dropped = tr.insertCell(col++);\n" +
"				dropped.innerHTML = ps.itemsDropped;\n" +
"\n" +
"				var traveled = tr.insertCell(col++);\n" +
"				traveled.innerHTML = ps.metersTraveled;\n" +
"\n" +
"				var playersince = tr.insertCell(col++);\n" +
"				playersince.innerHTML = formatDate(ps.playerSince);\n" +
"\n" +
"				var lastLogin = tr.insertCell(col++);\n" +
"				lastLogin.innerHTML = formatDate(ps.lastLogin);\n" +
"\n" +
"				var totalPlaytime = tr.insertCell(col++);\n" +
"				totalPlaytime.innerHTML = ps.totalPlaytime;\n" +
"\n" +
"				var sessionPlaytime = tr.insertCell(col++);\n" +
"				sessionPlaytime.innerHTML = ps.sessionPlaytime;\n" +
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
"\n" +
"			function groupConcat(groupArray) {\n" +
"				return groupArray == null ? '' : ' ' + groupArray.join(' ');\n" +
"			}\n" +
"\n" +
"			function formatDate(date) {\n" +
"				return date == null ? '--' : date.getMonth() + '/' + date.getDate() + '/' + date.getFullYear();\n" +
"			}\n" +
"		</script>\n" +
"	</body>\n" +
"</html>";
}
