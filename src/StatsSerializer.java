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
	
	public static String statsAsJson(List<PlayerStatistics> rawStats) {
		String response;
		try {
			response = JSONEncoder.getJSONEncoder(StatsSerializerMessage.class).encode(buildStatsMessage(rawStats));
		} catch (Exception ex) {
			response = ex.getMessage();
		}
		
		return response;
	}
	
	public static String statsAsJavascript(List<PlayerStatistics> rawStats) {
		String json = statsAsJson(rawStats);
		return "var mcStatsRawData = " + json + ";";
	}
	
	//XML serialization must be done in pieces due to the limitations of jaxb.
	//This does, however, give us more control over the final markup.
	public static String statsAsXml(List<PlayerStatistics> rawStats) {
		StringBuilder response = new StringBuilder();
		StatsSerializerMessage message = buildStatsMessage(rawStats);
		
		try {
			response.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			response.append("<mcStats>");
			response.append("<playersOnline>\n");
			
			for(OnlinePlayer p : message.getPlayersOnline()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBContext jc = JAXBContext.newInstance(StatsSerializerMessage.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
				marshaller.marshal(p, out);
				response.append(out.toString());
				response.append("\n");
			}
			
			response.append("</playersOnline>");
			response.append("<playerStats>\n");
			
			for(PlayerStatistics p : message.getPlayerStats()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JAXBContext jc = JAXBContext.newInstance(StatsSerializerMessage.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
				marshaller.marshal(p, out);
				response.append(out.toString());
				response.append("\n");
			}
			
			response.append("</playerStats>");
			response.append("</mcStats>");
		} catch (Exception ex) {
			response.append(ex.getMessage());
		}
		
		return response.toString();
	}
	
	private static StatsSerializerMessage buildStatsMessage(List<PlayerStatistics> rawStats) {
		//build the list of player names online. (java needs linq).
		List<OnlinePlayer> playersOnline = new ArrayList<OnlinePlayer>();
		for (Player p : etc.getServer().getPlayerList()) {
			OnlinePlayer op = new OnlinePlayer();
			op.playerName = p.getName();
			op.groups = p.getGroups();
			playersOnline.add(op);
		}
		
		StatsSerializerMessage message = new StatsSerializerMessage();
		message.setPlayersOnline(playersOnline);
		message.setPlayerStats(rawStats);
		return message;
	}
 
	public static String statsAsHtml(StatsConfig config) {
		return 
"<!DOCTYPE HTML>\n" +
"<html>\n" +
"	<head>\n" +
"		<title>Minecraft Player Statistics</title>\n" +
"		<meta http-equiv='refresh' content='" + config.getSecondsBetweenPageRefreshes() + "' >\n" +
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
"			th {\n" +
"				padding: 5px;\n" +
"			}\n" +
"\n" +
"			#stats th {\n" +
"				background-repeat: no-repeat;\n" +	
"				background-position: center right;\n" +	
"				cursor: pointer;\n" +
"				padding-right: 20px;\n" +
"			}\n" +
"\n" +			
"			tr.odd {\n" +
"				background: #f1f1f1;\n" +
"			}\n" +
"\n" +			
"			td {\n" +
"				padding:5px;\n" +
"				border-right: 1px ridge #d2d2d2;\n" +
"			}\n" +
"\n" +
"			th.header {\n" +
"				background-image:url(data:image/gif;base64,R0lGODlhFQAJAIAAACMtMP///yH5BAEAAAEALAAAAAAVAAkAAAIXjI+AywnaYnhUMoqt3gZXPmVg94yJVQAAOw%3D%3D);\n" +
"			}\n" +
"\n" +	
"			th.headerSortDown {\n" +
"				background-image:url(data:image/gif;base64,R0lGODlhFQAEAIAAACMtMP///yH5BAEAAAEALAAAAAAVAAQAAAINjB+gC+jP2ptn0WskLQA7);\n" +
"			}\n" +
"\n" +	
"			th.headerSortUp {\n" +
"				background-image:url(data:image/gif;base64,R0lGODlhFQAEAIAAACMtMP///yH5BAEAAAEALAAAAAAVAAQAAAINjI8Bya2wnINUMopZAQA7);\n" +
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
"\n" +
"			.admins {\n" +
"				color: #FF4040;\n" +
"			}\n" +
"\n" +
"			.mods {\n" +
"				color: #088A85;\n" +
"			}\n" +
"\n" +
"			.vip {\n" +
"				color: #49FF40;\n" +
"			}\n" +
"\n" +
"			.center {\n" +
"				text-align: center;\n" +
"			}\n" +
"\n" +
"			.right {\n" +
"				text-align: right;\n" +
"			}\n" +
"		</style>\n" +
"	</head>\n" +
"	<body>\n" +
"		<h1>Minecraft Player Statistics</h1>\n" +
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
"			</tbody>\n" +
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
"		<script src='" + config.getStatsBaseResource() + ".js'></script>\n" +
"		<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js'></script>\n" +
"		<script src='http://tablesorter.com/jquery.tablesorter.min.js'></script>\n" +
"		<script>\n" +
"			//build the Players Online list\n" +
"			var playersOnline = document.getElementById('playersOnlineList');\n" +
"			mcStatsRawData.playersOnline.sort(statsSort);\n" +
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
"				var playerNameTd = tr.insertCell(col++);\n" +
"				var playerNameSpan = document.createElement('span');\n" +
"				playerNameSpan.setAttribute('class', 'pName' + groupConcat(ps.playerGroups));\n" +
"				playerNameSpan.innerHTML = ps.playerName;\n" +
"				playerNameTd.appendChild(playerNameSpan);\n" +
"\n" +
"				var playerGroups = tr.insertCell(col++);\n" +
"				playerGroups.innerHTML = groupConcat(ps.playerGroups);\n" +
"\n" +
"				var placed = tr.insertCell(col++);\n" +
"				placed.setAttribute('class', 'right');\n" +
"				placed.innerHTML = ps.blocksPlaced;\n" +
"\n" +
"				var destroyed = tr.insertCell(col++);\n" +
"				destroyed.setAttribute('class', 'right');\n" +
"				destroyed.innerHTML = ps.blocksDestroyed;\n" +
"\n" +
"				var dropped = tr.insertCell(col++);\n" +
"				dropped.setAttribute('class', 'right');\n" +
"				dropped.innerHTML = ps.itemsDropped;\n" +
"\n" +
"				var traveled = tr.insertCell(col++);\n" +
"				traveled.setAttribute('class', 'right');\n" +
"				traveled.innerHTML = ps.metersTraveled;\n" +
"\n" +
"				var playersince = tr.insertCell(col++);\n" +
"				playersince.setAttribute('class', 'center');\n" +
"				playersince.innerHTML = formatDate(ps.playerSince);\n" +
"\n" +
"				var lastLogin = tr.insertCell(col++);\n" +
"				lastLogin.setAttribute('class', 'center');\n" +
"				lastLogin.innerHTML = formatDate(ps.lastLogin);\n" +
"\n" +
"				var totalPlaytime = tr.insertCell(col++);\n" +
"				totalPlaytime.setAttribute('class', 'right');\n" +
"				totalPlaytime.innerHTML = ps.totalPlaytime;\n" +
"\n" +
"				var sessionPlaytime = tr.insertCell(col++);\n" +
"				sessionPlaytime.setAttribute('class', 'right');\n" +
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
"			function formatDate(unixTimestamp) {\n" +
"				if(unixTimestamp == '') {\n" +
"					return '';\n" +
"				} else {\n" +
"					var date = new Date(parseInt(unixTimestamp));\n" +
"					return (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();\n" +
"				}\n" +
"			}\n" +
"\n" +
"			//sortable columns\n" +
"			$(document).ready(function() { \n" +
"				$('#stats').tablesorter(); \n" +
"			}); \n" +
"		</script>\n" +
"	</body>\n" +
"</html>";
	}
}
