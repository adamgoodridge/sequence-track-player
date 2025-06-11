package net.adamgoodridge.sequencetrackplayer.settings;

import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface SettingRepository extends MongoRepository<Setting, String> {

    Setting findSettingByNameEquals(String name);
}
