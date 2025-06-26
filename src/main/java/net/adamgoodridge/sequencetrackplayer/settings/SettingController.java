package net.adamgoodridge.sequencetrackplayer.settings;


import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.shortcut.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import static net.adamgoodridge.sequencetrackplayer.settings.SettingName.*;

@Controller
@RequestMapping("/setting")
@SuppressWarnings("unused")
public class SettingController {
    private final ShortcutService shortcutService;

    private final NasConnectorService nasConnectorService;

    private final SettingService settingService;

    private final FeedService feedService;
    @Autowired
    public SettingController(ShortcutService shortcutService, SettingService settingService, FeedService feedService) {
        this.shortcutService = shortcutService;
        this.nasConnectorService = new NasConnectorFileSystem();
        this.settingService = settingService;
        this.feedService = feedService;
    }

    @RequestMapping("/shortcut/showForm")
    public String shortcutForm(Model model) {
        String[] feedNames = nasConnectorService.listSubFiles("");
        String[] alreadyInShortCuts = feedService.getShortcuts()
                .stream().map(Shortcut::getFeedName).toArray(String[]::new);
        model.addAttribute("alreadyInShortCuts", alreadyInShortCuts);
        model.addAttribute("feedNames", feedNames);
        model.addAttribute("currentFeeds", shortcutService.getShortcuts());
        return "settings/shortcut-form";
    }

    @RequestMapping("/shortcut/processForm")
    public String shortcutProcessForm(@RequestParam(value = "shortcuts") String[] shortcuts) {
        shortcutService.saveShortcuts(shortcuts);
        return "redirect:/feed/list";
    }

    @RequestMapping("/showForm")
    public String silenceForm(Model model, @RequestParam(value ="restored", required = false, defaultValue = "false") boolean settingRestored) {
        java.lang.reflect.Method method;
        SettingsForm settingsForm = new SettingsForm();
        //todo
        //method = settingsForm.getClass().getMethod("set");
        String currentSilenceLength = settingService.getSetting(SettingName.SILENCE_LENGTH).getValue();
        settingsForm.setSilenceLength(currentSilenceLength);
        String dayOfWeek = settingService.getSetting(SettingName.DAY_OF_WEEK).getValue();
        settingsForm.setDayOfWeek(dayOfWeek);
        String hourOfDay = settingService.getSetting(SettingName.HOUR_OF_DAY).getValue();
        settingsForm.setHourOfDay(hourOfDay);
        Boolean isScanning = settingService.getBoolean(IS_SCANNING);
        settingsForm.setScanning(isScanning);
        Boolean calendarView = settingService.getBoolean(SettingName.CALENDAR_VIEW);
        settingsForm.setCalendarView(calendarView);
        Boolean logoView = settingService.getBoolean(SettingName.LOGO_VIEW);
        settingsForm.setLogoView(logoView);
        Boolean randomFromCurrent = settingService.getBoolean(SettingName.RANDOM_FROM_CURRENT);
        settingsForm.setRandomFromCurrent(randomFromCurrent);
        Boolean overwriteFeed = settingService.getBoolean(SettingName.OVERWRITE_FEED);
        settingsForm.setOverwriteFeed(overwriteFeed);
        Boolean regularlyChangeToRandom = settingService.getBoolean(SettingName.REGULARLY_CHANGE_TO_RANDOM);
        settingsForm.setRegularlyChangeToRandom(regularlyChangeToRandom);
        String trackCurrentCount = settingService.getSettingValue(SettingName.TRACK_CURRENT_COUNT);
        settingsForm.setTrackCurrentCount(trackCurrentCount);
        model.addAttribute("settingsForm", settingsForm);
        model.addAttribute("settingRestored", settingRestored);
        return "settings/settings-form";
    }
    @RequestMapping("/defaults")
    public String defaults() {
        settingService.deleteAndReset();
        return "redirect:/setting/showForm?restored=true";
    }
    @RequestMapping("/processForm")
    public String silenceProcessForm(@ModelAttribute SettingsForm settingsForm) {
        settingService.saveSettings(settingsForm.toSettingsRows());
        return "redirect:/feed/list";
    }
    @PutMapping(value = "/set/{settingNameValue}/{status}", produces = "application/json")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateBoolean(@PathVariable String settingNameValue, @PathVariable boolean status) {
        SettingName settingName;
        switch (settingNameValue) {
            case "shuffle" ->
                 settingName = SettingName.IS_SCANNING;
            case "regularly_change_to_random" ->
                settingName = SettingName.REGULARLY_CHANGE_TO_RANDOM;
            //todo
            default -> throw new ShufflePlayerError("Setting value isn't supported");
        }
        settingService.saveBoolean(settingName,status);
    }

}