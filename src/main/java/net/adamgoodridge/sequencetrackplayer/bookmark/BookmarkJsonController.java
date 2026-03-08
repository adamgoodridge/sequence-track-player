package net.adamgoodridge.sequencetrackplayer.bookmark;

import com.google.gson.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller()
@RequestMapping("/bookmark/json")
public class BookmarkJsonController {

    private final FeedService feedService;

    private final BookmarkedAudioService bookmarkedAudioService;
    @Autowired
    public BookmarkJsonController(FeedService feedService, BookmarkedAudioService bookmarkedAudioService) {
        this.feedService = feedService;
        this.bookmarkedAudioService = bookmarkedAudioService;
    }
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBookmark(@PathVariable String id) {
        bookmarkedAudioService.delete(id);

    }
    @PostMapping("/add/{feedTrackIndex}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public BookmarkedAudio addBookmark(@PathVariable long feedTrackIndex) {
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.add(feedTrackIndex);
        return bookmarkedAudio;
    }

}
