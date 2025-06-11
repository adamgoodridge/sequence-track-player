package net.adamgoodridge.sequencetrackplayer.feeder;


import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.bookmark.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SuppressWarnings("unused")
@RestController()
@RequestMapping("/feed/json")
public class FeedJsonController {
    private final Logger logger =  LoggerFactory.getLogger(this.getClass());
    private final Gson gson = new Gson();
    private final BookmarkedAudioService bookmarkedAudioService;


    private final SettingService settingService;

    private final FeedService feedService;


    private final AudioFeederService audioFeederService;

    @SuppressWarnings("unused")
    @Autowired
    public FeedJsonController(BookmarkedAudioService bookmarkedAudioService, SettingService settingService, FeedService feedService, AudioFeederService audioFeederService) {
        this.bookmarkedAudioService = bookmarkedAudioService;
        this.settingService = settingService;
        this.feedService = feedService;
        this.audioFeederService = audioFeederService;
    }

    @SuppressWarnings("unused")
    @GetMapping(value = "/status/{feedTrackListIndex}", produces = "application/json")
    public String status(@PathVariable int feedTrackListIndex) {
        Map<String, String> output = new HashMap<>();
        try {
            AudioFeeder audioFeeder = feedService.checkAndUpdateStatus(feedTrackListIndex);
            output.put("isLoaded", audioFeeder.getAudioIOFileManager() != null ? "true" : "false");
        } catch (GetFeedException e) {
            throw new JsonReturnError(e.getMessage());
        }
        return gson.toJson(output);
    }

    @SuppressWarnings("unused")
    @GetMapping(value = "/get/{action}/{feedTrackListIndex}", produces = "application/json")
    public String getNextTextOnly(@PathVariable Long feedTrackListIndex, @PathVariable String action, @RequestParam("sessionId") String sessionId,
                                                @RequestParam(value = "isScannerPage", required = false, defaultValue = "false") boolean isScannerPage) {
        AudioFeeder audioFeeder = getAudioFeederAndValidate(feedTrackListIndex, sessionId);

        /*
            move the track after the track is played OR prev/next is clicked before ***REMOVED***r mode will move the next feed
         */
        feedService.trackControl(audioFeeder, TrackAction.fromString(action));
        long id;
        //see if it is loaded
        boolean status;
        status = checkAndUpdateStatus(audioFeeder);
        if (!action.equals("current") && settingService.getBoolean(SettingName.IS_SCANNING) && status) {
            //season takes ownership over the feed, the sessionId is already checked
            if (isScannerPage) {
                audioFeeder = feedService.getRandomAudioFeeder(sessionId);
            } else {
                //if page single feed view
                audioFeeder = feedService.getRandomAudioFeeder();
                audioFeeder.setSessionId(sessionId);
                feedService.updateAudioFeeder(audioFeeder);
            }
            id = audioFeeder.getId();
        } else
            id = feedTrackListIndex;
        BookmarkedAudio bookmarkedAudio = null;
        if(status)
            bookmarkedAudio = bookmarkedAudioService.getBookedMarked(audioFeeder.getAudioIOFileManager());
        //index in-case random did change it
        FeedCurrentPlayingInfoDTO playing = new FeedCurrentPlayingInfoDTO(audioFeeder, id, bookmarkedAudio);
        return gson.toJson(playing);
    }

    private boolean checkAndUpdateStatus(AudioFeeder audioFeeder) {
        boolean status;
        try {
            status = feedService.checkAndUpdateStatus(audioFeeder.getId()).getAudioIOFileManager() != null;
        } catch (GetFeedException e) {
            throw new JsonReturnError("Failed to load the feed:" + e.getMessage());
        }
        return status;
    }

    private AudioFeeder getAudioFeederAndValidate(Long feedTrackListIndex, String sessionId) {
        Optional<AudioFeeder> optionalAudioFeeder = feedService.getAudioFeeder(feedTrackListIndex);
        if (optionalAudioFeeder.isEmpty())
            throw new JsonReturnError("There's no feed found loaded on the server currently");
        AudioFeeder audioFeeder = optionalAudioFeeder.get();
        if (audioFeeder.getAudioIOFileManager() == null)
            throw new JsonReturnError("Feed found but uncompleted There's no file manager found for the feed");
        checkSessionId(audioFeeder, sessionId);
        return optionalAudioFeeder.get();
    }

    @GetMapping(value = "/update/length/{feedTrackListIndex}/{length}", produces = "application/json")
    public String updateLength(@PathVariable long feedTrackListIndex, @PathVariable int length, @RequestParam("sessionId") String sessionId) {
        AudioFeeder audioFeeder = getAudioFeederAndValidate(feedTrackListIndex,sessionId);
        audioFeeder.getAudioIOFileManager().setCurrentPosition(length);
        audioFeederService.save(audioFeeder);
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getBookedMarked(audioFeeder.getAudioIOFileManager());
        FeedCurrentPlayingInfoDTO playing = new FeedCurrentPlayingInfoDTO(audioFeeder, feedTrackListIndex, bookmarkedAudio);
        return new Gson().toJson(playing);
    }
    //todo
    //For shuffle page
    @RequestMapping(value = "/{type}", produces = "application/json")
    public @ResponseBody String getAll(@PathVariable String type) {
        List<AudioFeeder> audioFeeders = type.equals("shuffle") ?
                feedService.getShufflesAudioFeeders() : feedService.getLoadedAudioFeeders();

        List<FeedCurrentPlayingInfoDTO> currentPlayingInfos = new ArrayList<>();
        audioFeeders.forEach(value -> {
            if (value.getAudioIOFileManager() != null) {
                BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getBookedMarked(value.getAudioIOFileManager());
                FeedCurrentPlayingInfoDTO playing = new FeedCurrentPlayingInfoDTO(value, value.getId(), bookmarkedAudio);
                currentPlayingInfos.add(playing);
            }
        });
        return new Gson().toJson(currentPlayingInfos);
    }

    @SuppressWarnings("unused")
    @RequestMapping(value = "/logging/{message}", produces = "application/json")
    public String logging(@PathVariable String message) {
        logger.info(message);
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return gson.toJson(map);
    }

    private void checkSessionId(AudioFeeder audioFeeder, String sessionId) {
        if (!audioFeeder.getSessionId().equals(sessionId))
            throw new JsonReturnError("Invalid Session ID, there is another tab that control over this control" + audioFeeder.getFeedName());
    }
}
