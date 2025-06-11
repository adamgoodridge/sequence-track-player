package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller()
@RequestMapping("/shuffle")
public class ShuffleController {
    private final FeedService feedService;

    private final SettingService settingService;

    private final  AudioFeederService audioFeederService;
    @Autowired
    public ShuffleController(FeedService feedService, SettingService settingService, AudioFeederService audioFeederService) {
        this.feedService = feedService;
        this.settingService = settingService;
        this.audioFeederService = audioFeederService;
    }

    @RequestMapping("")
    public String  shuffle(Model model) {
        //settings
        String sessionId = feedService.generateSessionId();
        feedService.takeOwnershipShuffleAudioFeeders(sessionId);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute(ConstantText.FEED_ATTRIBUTE_SILENCE, settingService.getSettingValue(SettingName.SILENCE_LENGTH));
        model.addAttribute(ConstantText.FEED_ATTRIBUTE_IS_SCANNING, settingService.isScanning());
        return "shuffle";
    }

    @RequestMapping("/shuffle")
    public String shuffleProcessForm(Model model) {
        List<AudioFeederItemDTO> audioFeederItems =  audioFeederService.getAllByAudioInfoNotNull().stream().map(AudioFeederItemDTO::new).toList();
        model.addAttribute("audioFeederItems",audioFeederItems);
        return "shuffle-item-form";
    }
    @RequestMapping("/select/processForm")
    public String shuffleProcessForm(@RequestParam(value = "feederIds") String[] feedIds) {
        feedService.setShuffleStatus(feedIds);
        return "redirect:/shuffle";
    }
    public String error(Model model) {
        model.addAttribute(ConstantText.ERROR_PAGE_ATTRIBUTE_HEADING,"Server Error");
        model.addAttribute(ConstantText.ERROR_PAGE_ATTRIBUTE_INFO , "");
        return "custom-error";
    }
}
