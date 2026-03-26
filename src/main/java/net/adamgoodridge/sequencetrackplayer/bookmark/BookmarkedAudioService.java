package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class BookmarkedAudioService {

    private final BookmarkedAudioRepository bookmarkedAudioRepository;

    private final FeedService feedService;

    @Autowired
    public BookmarkedAudioService(BookmarkedAudioRepository bookmarkedAudioRepository, FeedService feedService) {
        this.bookmarkedAudioRepository = bookmarkedAudioRepository;
        this.feedService = feedService;
    }

    public List<BookmarkedAudio> getAll() {
        return bookmarkedAudioRepository.findAll();
    }
    public BookmarkedAudio getById(String id){
        Optional<BookmarkedAudio> found = bookmarkedAudioRepository.findById(id);
        if(found.isEmpty())
            throw new JsonBookmarkNotFoundError(id);
        return found.get();
    }
    public BookmarkedAudio getBookedMarked(AudioIOFileManager audioInfo) {
        String path = audioInfo.getUrl().replace(ConstantText.DEFAULT_SERVER_URL,"/");
        return bookmarkedAudioRepository.findByPath(path);
    }

    public void delete(String bookmarkId) {
        BookmarkedAudio bookmarkedAudio = getById(bookmarkId);
        bookmarkedAudioRepository.deleteById(bookmarkedAudio.getBookmarkId());
    }

    public BookmarkedAudio add(Long feedTrackIndex) {
        AudioFeeder feeder = feedService.getAudioFeeder(feedTrackIndex).orElseThrow(() -> new JsonNotFoundError("There's no feed found loaded on the server currently"));
        if (feeder.getAudioIOFileManager() == null)
            throw new JsonNotFoundError("There's no feed found loaded on the server currently");
        String path = feeder.getAudioIOFileManager().getUrl().replace(ConstantText.DEFAULT_SERVER_URL,"/");
        BookmarkedAudio bookmarkedAudio = new BookmarkedAudio(path);
        bookmarkedAudioRepository.save(bookmarkedAudio);
        return bookmarkedAudio;
    }

}
