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
        Optional<BookmarkedAudio> found = bookmarkedAudioService.getById(id);
        if(found.isEmpty())
            throw new JsonNotFoundError("Bookmark id (" + id + ") cannot find in the database.");

        bookmarkedAudioService.delete(found.get());

    }
    @PutMapping("/add/{feedTrackIndex}")
    @RequestMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addBookmark(@PathVariable int feedTrackIndex) {
        Optional<AudioFeeder> optionalAudioFeeder = feedService.getAudioFeeder(feedTrackIndex);
        if(optionalAudioFeeder.isEmpty())
            throw new JsonNotFoundError("There's no feed found loaded on the server currently");

        AudioFeeder audioFeeder = optionalAudioFeeder.get();
        bookmarkedAudioService.add(audioFeeder);
        BookmarkedAudio bookmarkedAudio = bookmarkedAudioService.getBookedMarked(audioFeeder.getAudioIOFileManager());
        return new Gson().toJson(bookmarkedAudio);
    }

}
