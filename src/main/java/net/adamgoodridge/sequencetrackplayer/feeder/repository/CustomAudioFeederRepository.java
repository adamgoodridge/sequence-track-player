package net.adamgoodridge.sequencetrackplayer.feeder.repository;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.stereotype.*;

import java.util.*;


@Repository
public interface CustomAudioFeederRepository  {
    //for shuffle
    AudioFeeder getRandomBySessionId(String seasonId);
    AudioFeeder getRandomAudioFileNotNull();

    AudioFeeder getAudioFeederByFeedName(String feedName);

    List<AudioFeeder> getAllAudioNegativeOne();
    List<AudioFeeder> getAllByIsIncludeInFullScreenShuffle();
}
