
public class StatsConfig {
	private PropertiesFile config;
	
	public StatsConfig (PropertiesFile config) {
		this.config = config;
	}
	
	//base configuration
	public String getStatsCacheFile(){
		return config.getString("statsCacheFile", "statsCache");
	}
	
	public String getStatsBaseResource() {
		return config.getString("statsBaseResource", "mcstats"); 
	}
	
	public String getResourceSaveDirectory() {
		return config.getString("resourceSaveDirectory", "stats");
	}
	
	public int getSecondsBetweenSaves() {
		return config.getInt("secondsBetweenSaves", 60);
	}
		
	//webserver configuration
	public boolean getWebserverEnabled() {
		return config.getBoolean("webserverEnabled", false);
	}
	
	public int getHttpBacklog() {
		return config.getInt("httpBacklog", 8);
	}
	
	public int getHttpPort() {
		return config.getInt("httpPort", 8080);
	}
	
	public String getHttpServerContextRoot() {
		return config.getString("httpServerContextRoot", "/");
	}
}
