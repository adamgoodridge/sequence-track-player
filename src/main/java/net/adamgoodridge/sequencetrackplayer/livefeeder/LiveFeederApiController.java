package net.adamgoodridge.sequencetrackplayer.livefeeder;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@Controller
@RequestMapping("/api/live")
public class LiveFeederApiController {
	private final LiveFeederService liveFeederService;

	public LiveFeederApiController(LiveFeederService liveFeederService) {
		this.liveFeederService = liveFeederService;
	}
	
	@RequestMapping("/list")
	public String getAll(Model model) {
		List<FeederOutputThymeleaf> feederOutputThymeleafList =  liveFeederService.getAll();


		model.addAttribute("feeds", feederOutputThymeleafList);
		return "live-feeds";
	}

	@GetMapping("/summary")
	@ResponseBody
	public String getSummary() {

		//live stream
		FeederOutputSummary summary = liveFeederService.getSummary();
		if (summary == null) {
			throw new JsonReturnError("Can't get result");
		}
		Gson gson = new Gson();
		FeederOutputSummary summaryOutput = new FeederOutputSummary(summary.success(), summary.total());
		return gson.toJson(summaryOutput);
	}
}
