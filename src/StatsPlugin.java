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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Level;

import com.sun.net.httpserver.*;

public class StatsPlugin extends SuperPlugin {

	private final StatsPluginListener listener;
	private final StatsModel model;
	private final StatsConfig config;
	private final StatsController controller;
	
	private ShutdownHook hook = new ShutdownHook();
	
	private List<PluginRegisteredListener> prls;
	private HttpServer server;
	
	public StatsPlugin() {
		super("MCStats");
		prls = new ArrayList<PluginRegisteredListener>();
		
		config = new StatsConfig(baseConfig);
		model = new StatsModel(config, log);
		controller = new StatsController(config, model.getStats());
		listener = new StatsPluginListener(controller);
		
		// Write updatr file
		UpdatrWriter.writeUpdatrFile("MCStats.updatr");
	}

	@Override
	//Attach listener hooks
	public void enableExtra() {
		// Register hMod command
		etc.getInstance().addCommand("/played", " - displays your total play time.");
		//configure hMod hooks
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.BAN, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.ITEM_DROP, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, listener, this, PluginListener.Priority.LOW));
		prls.add(etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.LOW));
		
		//purge any users marked for removal
		for(String playerName : config.getPlayersToPurge()) {
			model.purgePlayer(playerName);
		}
		config.clearPlayersToPurge();
		
		//reset all playtimes if requested
		if(config.getResetPlaytime()) {
			log.log(Level.INFO, "Resetting all player play times");
			model.resetAllPlaytimes();
			config.clearResetPlaytime();
		}
		
		//start the http server if it's enabled
		if (config.getWebserverEnabled()) {
			String resource = config.getStatsBaseResource();
			String contextRoot = config.getHttpServerContextRoot();
			
			log.log(Level.INFO, "Starting MCStats web server.");
			try {
				server = HttpServer.create(
						new InetSocketAddress(config.getHttpPort()),
						config.getHttpBacklog());
				server.createContext(contextRoot, new StatsHttpHandler(
						model, config));
				server.setExecutor(null); // creates a default executor
				server.start();
				log.log(Level.INFO, String.format(
						"Server stats available at http://[hostname]:%s%s%s, %s, %s, and %s",
						config.getHttpPort(),
						contextRoot,
						resource + ".xml",
						resource + ".json",
						resource + ".js",
						resource + ".html"));
			} catch (IOException e) {
				log.log(Level.SEVERE, "MCStats failed to start http server", e);
			}
		}
		//register a shutdown hook
		Runtime.getRuntime().addShutdownHook(hook);
		controller.logOutAllPlayers();
		controller.logInOnlinePlayers();
		model.startPersisting();
	}

	@Override
	//Detach listener hooks
	public void disableExtra() {
		Runtime.getRuntime().removeShutdownHook(hook);
		controller.logOutAllPlayers();
		model.stopPersisting();
		
		model.saveStats();
		model.saveUserFiles();
		
		for(PluginRegisteredListener psr : prls) {
			etc.getLoader().removeListener(psr);
		}
		prls = new ArrayList<PluginRegisteredListener>();
		
		//stop the http server if it's enabled
		if(config.getWebserverEnabled()) {
			log.log(Level.INFO, "MCStats stopping web server.");
			server.stop(1);
			server.removeContext(config.getHttpServerContextRoot());
			server = null;
		}
	}
	
	private class ShutdownHook extends Thread {
		public void run() { 
			controller.logOutAllPlayers();
			model.saveStats(); 
			System.out.println("MCStats persisting player statistics on exit.");
		}
	}

}
