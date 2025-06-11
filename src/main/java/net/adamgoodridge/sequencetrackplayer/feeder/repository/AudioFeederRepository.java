package net.adamgoodridge.sequencetrackplayer.feeder.repository;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

import java.util.*;


@Repository
public interface AudioFeederRepository extends MongoRepository<AudioFeeder, Long>, CustomAudioFeederRepository{
    List<AudioFeeder> getAllByAudioInfoNotNull();
    //get loading feeds
    int countAllByFeedNameAndAndAudioInfoNotNull(String feedName);
    List<AudioFeeder> getAllByAudioInfoNull();
    @Query("{feedName:{$regex: /.*mp3$|.*m4a/}}")
    List<AudioFeeder> getAllByAudioInfoTitleErrors();
}
