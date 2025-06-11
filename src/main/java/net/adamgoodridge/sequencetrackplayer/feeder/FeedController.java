package net.adamgoodridge.sequencetrackplayer.feeder;

import jakarta.servlet.http.*;
import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.livefeeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


//js can  error out in odd way as json incorrect
@Controller
@RequestMapping("/feed")
@SuppressWarnings({"unused", "SameReturnValue"})
public class FeedController {
	private final Logger logger =  LoggerFactory.getLogger(this.getClass());

	private final SettingService settingService;

	private final FeedService feedService;


	private final AudioFeederService audioFeederService;
	private final LiveFeederService liveFeederService;

	@Autowired
	public FeedController(SettingService settingService, FeedService feedService, AudioFeederService audioFeederService, LiveFeederService liveFeederService) {
		this.settingService = settingService;
		this.feedService = feedService;
		this.audioFeederService = audioFeederService;
		this.liveFeederService = liveFeederService;
	}

	@RequestMapping("/get/random/**")
	public String getRandom(HttpServletRequest httpServletRequest) {
		String feedName = httpServletRequest.getRequestURI().split("/get/random/")[1];
		FeedRequest feedRequest = new FeedRequest.Builder()
				.name(feedName)
				.feedRequestType(FeedRequestType.RANDOM)
				.path(feedName)
				.build();
		//todo customize name for subfeed eg "random/rock"
		long feedId = feedService.populateFeed(feedRequest);
		return "redirect:/feed/get/" + feedId;
	}


	//loads something in the current list
	@RequestMapping("/get/{feedId}")
	public String getItem(Model model, @PathVariable int feedId) {
		Optional<AudioFeeder> optionalAudioFeeder = feedService.getAudioFeeder(feedId);
		if (optionalAudioFeeder.isEmpty())
			throw new ServerError("The requested feed ID is not currently in the list");
		AudioFeeder audioFeeder = optionalAudioFeeder.get();
		audioFeeder.setSessionId(feedService.generateSessionId());
		feedService.updateAudioFeeder(audioFeeder);
		FeedCurrentPlayingInfoDTO feedCurrentPlayingInfo = new FeedCurrentPlayingInfoDTO(audioFeeder, feedId, null);
		model.addAttribute(ConstantText.FEED_ATTRIBUTE_CURRENT_FEED, feedCurrentPlayingInfo);
		//settings
		model.addAttribute(ConstantText.FEED_ATTRIBUTE_SILENCE, settingService.getSettingValue(SettingName.SILENCE_LENGTH));
		model.addAttribute("sessionId", audioFeeder.getSessionId());
		model.addAttribute(ConstantText.FEED_ATTRIBUTE_IS_SCANNING, settingService.getBoolean(SettingName.IS_SCANNING));
		model.addAttribute("isRandomChange", settingService.getBoolean(SettingName.REGULARLY_CHANGE_TO_RANDOM));
		return "feed";
	}

	@SuppressWarnings("unused")
	@RequestMapping("/list")
	public String home(Model model) {

		//whether it should show logo view
		String view = settingService.getBoolean(SettingName.LOGO_VIEW)
				? "list-feeds-icon-view" : "list-feeds";
		model.addAttribute("heading", getHeadingForMostRecentApi());
		model.addAttribute("shortcuts", feedService.getShortcuts());
		model.addAttribute("compileTime", CompileTime.get());
		return view;
	}
	private String getHeadingForMostRecentApi() {

		String headingColour;
		String summaryText;
		//live stream
		FeederOutputSummary summary = liveFeederService.getSummary();
		if (summary != null) {
			summaryText =  "Welcome, the live feed status" + summary.success() + " out of "+ summary.total();
			headingColour = summary.success() == summary.total() ? "green" : "red";
		} else {
			summaryText = "Cannot connect to the most recent API";
			headingColour = "red";
		}
		return "<h1 style=\"background-color:" + headingColour + "\">"+ summaryText + "</h1>";
	}

	@SuppressWarnings("unused")
	@RequestMapping("/list/shuffle")
	public String listAllShuffleFeeds(Model model) {
		model.addAttribute("feedNames", feedService.feedNames());
		return "list-feeds-all";
	}

	@SuppressWarnings("unused")
	@RequestMapping("/list/all")
	public String listAll(Model model) {
		return "redirect:/feed/***REMOVED***r";
	}

	@RequestMapping("/list/current")
	public String current(Model model, @RequestParam(value = "deletedLoading", required = false, defaultValue = "false") boolean deletedLoading) {
		List<AudioFeeder> audioFeeders = feedService.getLoadedAudioFeeders();
		List<AudioFeederItemDTO> audioFeederItems = audioFeeders
				.stream().map(AudioFeederItemDTO::new).toList();
		List<AudioFeederItemsByFeed> audioFeederItemsByFeeds = AudioFeederItemsByFeed.sort(audioFeederItems);
		model.addAttribute("feedSize", audioFeederItems.size());
		model.addAttribute("audioFeederItemsByFeeds", audioFeederItemsByFeeds);
		return "list-current-feeds";
	}


	@SuppressWarnings("unused")
	@RequestMapping("/clear")
	public String clearList() {
		feedService.removeAll();
		return "redirect:/feed/list/current";
	}
	@RequestMapping("/repair")
	public String repair() {
		audioFeederService.repairDocument();
		return "redirect:/feed/list/current";
	}

	@SuppressWarnings("unused")
	@RequestMapping("/clear/loading")
	public String clearLoadingList() {
		feedService.removeAllLoading();
		return "redirect:/feed/list/current?deletedLoading=true";
	}

	//not used in gui for now
	@SuppressWarnings("unused")
	@RequestMapping("/clear/{feedTrackIndex}")
	public String clearFeed(Model model, @PathVariable long feedTrackIndex) {
		Optional<AudioFeeder> item = feedService.getAudioFeeder(feedTrackIndex);
		String message;
		if(item.isEmpty()) {
			message = "No shuffle feed found at id " + feedTrackIndex;
		} else {
			AudioFeeder deletedItem = feedService.removeAudioFeeder(feedTrackIndex);
			message = "The ***REMOVED***r feed "+item.get().getFeedName()+" (id " + feedTrackIndex + ") was deleted";

		}
		model.addAttribute("deleted", message);
		return "redirect:/";
	}

}
