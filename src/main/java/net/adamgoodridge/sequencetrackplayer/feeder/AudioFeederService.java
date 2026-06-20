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
    public AudioFeeder getRandomByFeedName(String feedName, boolean isHolidayPeriod) {
        if(isHolidayPeriod)
            return audioFeederRepository.getRandomByFeedNameInHolidayPeriod(feedName);
        return audioFeederRepository.getAudioFeederByFeedName(feedName);
    }
    public void delete(AudioFeeder feeder) {
        audioFeederRepository.delete(feeder);
    }

    public void save(List<AudioFeeder> audioFeeders) {
        audioFeederRepository.saveAll(audioFeeders);
    }

    public List<AudioFeeder> getAllByAudioIOFileManagerNotNull() {
        return audioFeederRepository.getAllByAudioIOFileManagerNull();
    }
    public void deleteAllByAudioInfoNull() {
        List<AudioFeeder> loadedFeeds = audioFeederRepository.getAllByAudioIOFileManagerNull();
        audioFeederRepository.deleteAll(loadedFeeds);
    }

    public int countFeedNameAndLoaded(String feeName) {
        return audioFeederRepository.countAllByFeedNameAndAndAudioIOFileManagerNotNull(feeName);
    }

    public void deleteAllByAudioNegativeOne() {
        List<AudioFeeder> audioFeeders = audioFeederRepository.getAllAudioNegativeOne();
        audioFeederRepository.deleteAll(audioFeeders);
    }

    public List<AudioFeeder> getShufflesAudioFeeders() {
        return audioFeederRepository.getAllByIsIncludeInFullScreenShuffle();
    }
}
