package net.adamgoodridge.sequencetrackplayer.bookmark;


import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller()
@RequestMapping("/bookmark")
public class BookmarkController {

    private final FeedService feedService;

    private final BookmarkedAudioService bookmarkedAudioService;
    @Autowired
    public BookmarkController(FeedService feedService, BookmarkedAudioService bookmarkedAudioService) {
        this.feedService = feedService;
        this.bookmarkedAudioService = bookmarkedAudioService;
    }


    @RequestMapping("/get/{bookmarkID}")
    public String getBookmark(@PathVariable String bookmarkID) throws DataBaseError {
        Optional<BookmarkedAudio> optionalBookmarkedAudio = bookmarkedAudioService.getById(bookmarkID);
        if (optionalBookmarkedAudio.isEmpty())
            throw new ServerError("Bookmark ID doesn't exists in database.");

        //get the row from the database
        FeedRequest feedRequest = optionalBookmarkedAudio.get().toRequest();


        long feedId = feedService.populateFeed(feedRequest);
        return "redirect:/feed/get/" + feedId;
    }
    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "false") Boolean isDeleted, Model model) {
        List<BookmarkedAudio> bookmarks = bookmarkedAudioService.getAll();
        model.addAttribute("isDeleted",isDeleted);
        model.addAttribute("bookmarks", bookmarks);
        return "list-bookmark-feeds";
    }


}
