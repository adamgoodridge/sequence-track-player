package net.adamgoodridge.sequencetrackplayer.settings;

import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface SettingRepository extends MongoRepository<Setting, String> {

    Optional<Setting> findSettingByNameEquals(String name);
}
