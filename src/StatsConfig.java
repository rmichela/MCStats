
public class StatsConfig {
	private PropertiesFile config;
	
	public StatsConfig (PropertiesFile config) {
		this.config = config;
	}
	
	public String getStatsCacheFile(){
		return config.getString("statsCacheFile", "statsCache");
	}
	
	public int getHttpPort() {
		return config.getInt("httpPort", 8080);
	}
	
	public String getStatsResource() {
		return config.getString("statsResource", "/mcstats.xml"); 
	}
	
	public int getHttpBacklog() {
		return config.getInt("httpBacklog", 8);
	}
	
	public int getStatsSaveSeconds() {
		return config.getInt("statsSaveSeconds", 60);
	}
}
