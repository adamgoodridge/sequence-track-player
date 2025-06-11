package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.feeder.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class AudioFeederService {
    private final AudioFeederRepository audioFeederRepository;

    @Autowired
    public AudioFeederService(AudioFeederRepository audioFeederRepository) {
        this.audioFeederRepository = audioFeederRepository;
    }

    public Optional<AudioFeeder> get(Long id){
                return audioFeederRepository.findById(id);
    }

    public void save(AudioFeeder audioFeeder) {
        audioFeederRepository.save(audioFeeder);
    }

    public List<AudioFeeder> getAll() {
        return audioFeederRepository.findAll();
    }

    public void deleteAll() {
        audioFeederRepository.deleteAll();
    }
    public AudioFeeder getRandom() {
        return audioFeederRepository.getRandomAudioFileNotNull();
    }
    public AudioFeeder getRandomBySessionId(String sessionId) {
        return audioFeederRepository.getRandomBySessionId(sessionId);
    }
    public AudioFeeder getRandomByFeedName(String feedName) {
        return audioFeederRepository.getAudioFeederByFeedName(feedName);
    }

    public void delete(AudioFeeder feeder) {
        audioFeederRepository.delete(feeder);
    }

    public void save(List<AudioFeeder> audioFeeders) {
        audioFeederRepository.saveAll(audioFeeders);
    }

    public List<AudioFeeder> getAllByAudioInfoNotNull() {
        return audioFeederRepository.getAllByAudioInfoNotNull();
    }
    public void deleteAllByAudioInfoNull() {
        List<AudioFeeder> loadedFeeds = audioFeederRepository.getAllByAudioInfoNull();
        audioFeederRepository.deleteAll(loadedFeeds);
    }

    public int countFeedNameAndLoaded(String feeName) {
        return audioFeederRepository.countAllByFeedNameAndAndAudioInfoNotNull(feeName);
    }

    public void deleteAllByAudioNegativeOne() {
        List<AudioFeeder> audioFeeders = audioFeederRepository.getAllAudioNegativeOne();
        audioFeederRepository.deleteAll(audioFeeders);
    }

    public List<AudioFeeder> getShufflesAudioFeeders() {
        return audioFeederRepository.getAllByIsIncludeInFullScreenShuffle();
    }
    public void repairDocument(){
        List<AudioFeeder> audioFeeders = audioFeederRepository.getAllByAudioInfoTitleErrors();
        for(AudioFeeder audioFeeder: audioFeeders) {
            String feedName = audioFeeder.getFeedName();
            String[] split = feedName.split("/");
            /*
            EXAMPLES
            
                /FEEDD/SUBFEEDA/10326_SUBFEEDA_North-South/2022-01-05_Wednesday/FEEDD_2022-01-05_Wednesday_23-50-42_10326_SUBFEEDA_North-South.m4a
                /***REMOVED******REMOVED***/2022/2022-02_February/2022-02-26_Saturday/***REMOVED******REMOVED***_AUDIOFILE_2022-02-26_Saturday_19-47-44.mp3
             */
                if(split[1].equals("FEEDD")) {
                    feedName =  split[1] + "/" + split[2] + "/" + split[3];
                }  else {
                    feedName = split[1];
                }
                audioFeeder.setFeedName(feedName);
                audioFeederRepository.save(audioFeeder);
        }
    }
}
