package net.adamgoodridge.sequencetrackplayer.shortcut;

import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface ShortcutRepository extends MongoRepository<Shortcut, String> {
}
