import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.logging.Level;

import com.sun.net.httpserver.*;

public class MCStats extends SuperPlugin {

	private final StatsListener listener;
	private final StatsPersister persister;
	private final StatsConfig statsConfig;
	private final StatsManager statsManager;
	
	private ShutdownHook hook = new ShutdownHook();
	
	private List<PluginRegisteredListener> psrs;
	private HttpServer server;
	
	public MCStats() {
		super("MCStats");
		psrs = new ArrayList<PluginRegisteredListener>();
		
		statsConfig = new StatsConfig(config);
		persister = new StatsPersister(statsConfig, log);
		statsManager = new StatsManager(statsConfig, persister.getStats());
		listener = new StatsListener(statsManager);
	}

	@Override
	//Attach listener hooks
	public void enableExtra() {
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, listener, this, PluginListener.Priority.MEDIUM));
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.MEDIUM));
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, PluginListener.Priority.MEDIUM));
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.ITEM_DROP, listener, this, PluginListener.Priority.MEDIUM));
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM));
		psrs.add(etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, listener, this, PluginListener.Priority.MEDIUM));
		
		//start the http server
		String resource = statsConfig.getStatsResource();
		String contextRoot = resource.substring(0, resource.lastIndexOf('/') + 1);

		log.log(Level.INFO, "Starting MCStats web server.");
		try {
			server = HttpServer.create(new InetSocketAddress(statsConfig.getHttpPort()), statsConfig.getHttpBacklog());
			server.createContext(contextRoot, new StatsHttpHandler(statsManager, statsConfig));
			server.setExecutor(null); // creates a default executor
			server.start();
			log.log(Level.INFO, String.format("Server stats available at http://%s:%s%s", 
					server.getAddress(), statsConfig.getHttpPort(), resource));
		} catch (IOException e) {
			log.log(Level.SEVERE, "MCStats failed to start http server", e);
		}
		
		
		
		//register a shutdown hook
		Runtime.getRuntime().addShutdownHook(hook);
		persister.startPersisting();
	}

	@Override
	//Detach listener hooks
	public void disableExtra() {
		Runtime.getRuntime().removeShutdownHook(hook);
		persister.stopPersisting();
		
		persister.saveStats();
		
		for(PluginRegisteredListener psr : psrs) {
			etc.getLoader().removeListener(psr);
		}
		psrs = new ArrayList<PluginRegisteredListener>();
		
		//stop the http server
		log.log(Level.INFO, "MCStats stopping web server.");
		server.stop(1);
	}
	
	private class ShutdownHook extends Thread {
		public void run() { 
			persister.saveStats(); System.out.println("MCStats persisting player statistics on exit.");
		}
	}

}
