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
}
