import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StatsHttpHandler implements HttpHandler {

	private StatsConfig config;
	private StatsModel stats;
	
	public StatsHttpHandler(StatsModel stats, StatsConfig config)
	{
		this.config = config;
		this.stats = stats;
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		String request = t.getRequestURI().getPath();
        if(request.endsWith(config.getStatsBaseResource() + ".xml")) {
        	handleXML(t);
        } else if(request.endsWith(config.getStatsBaseResource() + ".json")) {
        	handleJson(t);
        } else {
        	handle404(t);
        }
	}
	
	private void handleXML(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsXml(stats.getRawStats());
		
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}
	
	private void handleJson(HttpExchange t) throws IOException {
		String response = StatsSerializer.statsAsJson(stats.getRawStats());
		
		t.sendResponseHeaders(200, response.length());
		OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();	
	}
	
	private void handle404(HttpExchange t) throws IOException {
		String response = t.getRequestURI().getPath() + " NOT FOUND";
        t.sendResponseHeaders(404, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();		
	}
}
