package net.adamgoodridge.sequencetrackplayer.settings;

import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface SettingArrayRepository extends MongoRepository<SettingArray, String> {
    Optional<SettingArray> findBySettingName(String settingName);
}