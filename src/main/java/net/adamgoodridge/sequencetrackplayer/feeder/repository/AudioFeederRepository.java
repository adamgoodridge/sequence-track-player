package net.adamgoodridge.sequencetrackplayer.feeder.repository;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

import java.util.*;


@Repository
public interface AudioFeederRepository extends MongoRepository<AudioFeeder, Long>, CustomAudioFeederRepository{
    List<AudioFeeder> getAllByAudioIOFileManagerNull();
    //get loading feeds
    int countAllByFeedNameAndAndAudioIOFileManagerNotNull(String feedName);
    @Query("{feedName:{$regex: /.*mp3$|.*m4a/}}")
    List<AudioFeeder> getAllByAudioIOFileManagerTitleErrors();

    //The regex to match 20##-12-[21-31]is:
    @Query("""
    {
      "feedName": ?0,
      "audioInfo.files.0.name": {
        "$regex": "20[0-9]{2}-12-[2-3][0-9]"
      }
    }
    """)
   AudioFeeder getRandomByFeedNameInHolidayPeriod(String feedName);
}
