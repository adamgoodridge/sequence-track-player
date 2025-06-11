package net.adamgoodridge.sequencetrackplayer.livefeeder;

import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller()
@RequestMapping("/live")
public class LiveFeederController {
	private final LiveFeederService liveFeederService;
	
	public LiveFeederController(LiveFeederService liveFeederService) {
		this.liveFeederService = liveFeederService;
	}
	
	@RequestMapping("/list")
	public String getAll(Model model) {
		List<FeederOutputThymeleaf> feederOutputThymeleafList =  liveFeederService.getAll();
		
		
		model.addAttribute("feeds", feederOutputThymeleafList);
		return "live-feeds";
	}
}
