package net.adamgoodridge.sequencetrackplayer.bookmark;

import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;
@Repository
public interface BookmarkedAudioRepository extends MongoRepository<BookmarkedAudio, String> {

     BookmarkedAudio findByPath(String path);
}
