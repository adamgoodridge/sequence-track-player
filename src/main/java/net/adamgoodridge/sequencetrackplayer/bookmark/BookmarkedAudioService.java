package net.adamgoodridge.sequencetrackplayer.bookmark;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class BookmarkedAudioService {

    private final BookmarkedAudioRepository bookmarkedAudioRepository;

    @Autowired
    public BookmarkedAudioService(BookmarkedAudioRepository bookmarkedAudioRepository) {
        this.bookmarkedAudioRepository = bookmarkedAudioRepository;
    }

    public List<BookmarkedAudio> getAll() {
        return bookmarkedAudioRepository.findAll();
    }
    public Optional<BookmarkedAudio> getById(String id){
        return bookmarkedAudioRepository.findById(id);
    }
    public BookmarkedAudio getBookedMarked(AudioIOFileManager audioInfo) {
        String path = audioInfo.getUrl().replace(ConstantText.DEFAULT_SERVER_URL,"/");
        return bookmarkedAudioRepository.findByPath(path);
    }

    public void delete(BookmarkedAudio bookmarkedAudio) {
        bookmarkedAudioRepository.deleteById(bookmarkedAudio.getBookmarkId());
    }

    public void add(AudioFeeder audioFeeder) {
        String path = audioFeeder.getAudioIOFileManager().getFile().getHref().replace(ConstantText.DEFAULT_SERVER_URL,"/");
        BookmarkedAudio bookmarkedAudio = new BookmarkedAudio(path);
        bookmarkedAudioRepository.save(bookmarkedAudio);
    }

}
