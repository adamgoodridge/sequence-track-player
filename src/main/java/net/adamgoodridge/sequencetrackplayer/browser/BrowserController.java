package net.adamgoodridge.sequencetrackplayer.browser;
//allow file to browse the file system


import jakarta.servlet.http.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.directory.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static net.adamgoodridge.sequencetrackplayer.ConstantText.*;


@Controller()
@RequestMapping("/browser")
public class BrowserController {

    private final NasConnectorService nasConnectorFileSystem;
    private final FeedService feedService;
    private final SettingService settingService;

    @SuppressWarnings("unused")
    @Autowired
    public BrowserController(FeedService feedService, SettingService settingService) {
        this.nasConnectorFileSystem = new NasConnectorFileSystem();
        this.feedService = feedService;
        this.settingService = settingService;
    }


    @RequestMapping("/path/**")
    //tells calenderViewChange whether it is worth looking at calenderViewChange
    public String browse(HttpServletRequest httpServletRequest, Model model,
                         @RequestParam(value = "calenderViewChange", required = false, defaultValue = "false") boolean calenderViewChange,
                         @RequestParam(value = "calenderView", required = false, defaultValue = "false") boolean calenderView) {
        //keep/add forward slash if not existed
        String path = BrowserUtils.getFilePathFromUrl(httpServletRequest.getRequestURI(), URL_ENDING_NO_FEED);
        if (calenderViewChange) {
            settingService.saveCalendarView(calenderView);
        }
        return browseNextDirectory(path, model, Long.parseLong("-1"));
    }

    private String browseNextDirectory(String originalPath, Model model, long feedId) {
        //check forward slash is at start and remove it if it is
        String path = originalPath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        String feedName = path.split("/")[0];
        if (isAudioFile(path)) {
            AudioFeeder audioFeeder = new AudioFeeder(feedName);
            audioFeeder.setId(feedId);
            audioFeeder.setStartPath(path);
            return playFeed(audioFeeder);
        }
        List<BreadCrumb> breadcrumbs = BrowserUtils.generateBreadCrumbs(path);
        model.addAttribute("breadcrumbs", breadcrumbs);
        String lastPath = "";
        if (breadcrumbs != null)
            lastPath = breadcrumbs.get(breadcrumbs.size() - 1).path();

        model.addAttribute("baseUrl", URL_ENDING_NO_FEED);
        model.addAttribute("lastPath", lastPath);
        model.addAttribute("fullPath", path);
        if (isShowCalendar(path)) {
            return showCalendar(model, path);
        }
        String[] subFiles = Arrays.stream(nasConnectorFileSystem.listSubFiles(path))
                    .map(file -> "/" + file)
                    .toArray(String[]::new);
        model.addAttribute("rootPath", path);
        model.addAttribute("subFiles", subFiles);
        return "browser";
        }


    private String showCalendar(Model model, String path) {
        List<DateForCalendarView> dates = nasConnectorFileSystem.listDaysInYears(path);
        model.addAttribute("year", path.split("/")[1]);
        model.addAttribute("yearView", true);
        model.addAttribute("dates", dates);
        return "calendar";
    }

    private String playFeed(AudioFeeder audioFeeder) {
        //chop gets rid of character
        FeedRequest feedRequest = new FeedRequest.Builder()
                .name(audioFeeder.getFeedName())
                .path(audioFeeder.getStartPath())
                .feedId(audioFeeder.getId())
                .feedRequestType(FeedRequestType.BOOKMARK)
                .build();
        //get rid of the last slash
        long feedId = feedService.populateFeed(feedRequest);
        return "redirect:/feed/get/" + feedId;
    }

    private boolean isAudioFile(String path) {
        return path.endsWith(".mp3/") || path.endsWith(".m4a/");
    }

    private boolean isShowCalendar(String path) {
        return path.matches(ENDING_PATH_IN_YEAR_REGEX) && settingService.getBoolean(SettingName.CALENDAR_VIEW);
    }

    @SuppressWarnings("unused")
    @RequestMapping("/feed/{feedId}/**")
    public String browseByFeedId(@PathVariable long feedId, Model model, HttpServletRequest httpServletRequest) {
        Optional<AudioFeeder> optional = feedService.getAudioFeeder(feedId);
        //END baseUrl
        if (optional.isEmpty())
            throw new NotFoundError("No feed with the id of " + feedId + "cannot be found.");;
        String baseUrl = URL_ENDING_FEED + feedId + "/";
        String path = BrowserUtils.getFilePathFromUrl(httpServletRequest.getRequestURI(), baseUrl);
        String folderPath;
        String currentFile;
        String link;
        AudioFeeder audioFeeder = optional.get();
        if (isAudioFile(path)) {
            audioFeeder.setId(feedId);
            audioFeeder.setStartPath(path.substring(0, path.length() - 1));
            return playFeed(audioFeeder);
        }
        if (path.isEmpty()) {
            //mp3 folder
            //in dir when audio file is in.
            String fullPath = audioFeeder.getAudioIOFileManager().getFile().getFullPath().replace("\\", "/");
            int lastBackslashIndex = fullPath.lastIndexOf("/");
            folderPath = fullPath.substring(0, lastBackslashIndex);
            currentFile = audioFeeder.getAudioIOFileManager().getFile().getFullPathLocalFileSystem();

        } else {
            folderPath = path;
            currentFile = audioFeeder.fileInFolder(path);
        }
        List<BreadCrumb> breadcrumbs = BrowserUtils.generateBreadCrumbs(folderPath);
        model.addAttribute("breadcrumbs", breadcrumbs);
        // "/" is in-front of the filename
        model.addAttribute("currentFile", currentFile);
        String[] subFiles = Arrays.stream(nasConnectorFileSystem.listSubFiles(folderPath)).map(file -> "/" + file)
                .toArray(String[]::new);
        String lastPath = "";
        if (breadcrumbs != null) {
            lastPath = breadcrumbs.get(breadcrumbs.size() - 1).path();
            model.addAttribute("rootPath", folderPath);
            //fixed bug it takes straight to mp3 file
            subFiles = Arrays.stream(subFiles).toArray(String[]::new);
            model.addAttribute("subFiles", subFiles);
        }
        //model
        model.addAttribute("lastPath", lastPath);
        model.addAttribute("baseUrl", baseUrl);
        return "browser";
    }

}
