package net.adamgoodridge.sequencetrackplayer.livefeeder;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.util.*;

@Service
public class LiveFeederService {
	
	private final RestTemplate restTemplate;
	private static final Logger logger = LoggerFactory.getLogger(LiveFeederService.class.getName());

	@Autowired
	public LiveFeederService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public List<FeederOutputThymeleaf> getAll() {
		//todo
		ResponseEntity<List<FeederOutput>> response = restTemplate.exchange(
				"http://192.168.181.24:8085/api/v1/feeds", HttpMethod.GET,null, new ParameterizedTypeReference<List<FeederOutput>>(){
				
				});
		List<FeederOutput> feeders = response.getBody();
		List<FeederOutputThymeleaf> output = new ArrayList<>();
		for(FeederOutput feederOutput: feeders) {
			FeederOutputThymeleaf feederOutputThymeleaf = new FeederOutputThymeleaf(feederOutput);
			output.add(feederOutputThymeleaf);
		}
		return output;
	}
	
	public FeederOutputSummary getSummary() {
		
		FeederOutputSummary response = null;
		try {
			response = restTemplate.getForObject("http://192.168.181.24:8085/api/v1/feeds/summary", FeederOutputSummary.class);
		} catch (RestClientException e) {
			logger.error(e.getMessage());
		}
		return response;
	}
}
