package net.adamgoodridge.sequencetrackplayer.feeder;


import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.directory.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.sequence.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.shortcut.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

@Service
public class FeedService {
    private final Map<Long, AudioFeeder> audioFeedersLoaded = new HashMap<>();
    private final SettingService settingService;
    private final AudioFeederService audioFeederService;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final ShortcutService shortcutService;
    private final NasConnectorService nasConnectorService;

    @Autowired
    public FeedService(SettingService settingService, AudioFeederService audioFeederService, SequenceGeneratorService sequenceGeneratorService, ShortcutService shortcutService) {
        this.settingService = settingService;
        this.audioFeederService = audioFeederService;
        this.nasConnectorService = new NasConnectorFileSystem();
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.shortcutService = shortcutService;
    }


    public String generateSessionId() {
        return RandomStringUtils.random(20, true, true);
    }

    public long populateFeed(FeedRequest feedRequest) {
        if (!feedRequest.hasId())
            feedRequest.setFeedId(sequenceGeneratorService.generateSequence(AudioFeeder.SEQUENCE_NAME));

        AudioFeeder audioFeeder = feedRequest.isRequestType(FeedRequestType.RANDOM) ?
                audioFeederService.getRandomByFeedName(feedRequest.getName()) :
                null;

        if (audioFeeder != null)
            return audioFeeder.getId();

        audioFeeder = new GenerateTrack(settingService.getPreferredRandomSettings(), feedRequest).process();
        audioFeedersLoaded.put(audioFeeder.getId(), audioFeeder);
        audioFeederService.save(audioFeeder);
        return audioFeeder.getId();
    }


    public Optional<AudioFeeder> getAudioFeeder(long index) {
        return audioFeederService.get(index);
    }

    public void updateAudioFeeder(AudioFeeder audioFeeder) {
        audioFeederService.save(audioFeeder);
    }


    public void removeAll() {
        audioFeedersLoaded.clear();
        audioFeederService.deleteAll();
        sequenceGeneratorService.resetSequence(AudioFeeder.SEQUENCE_NAME);
    }

    public AudioFeeder removeAudioFeeder(long feedTrackIndex) {
        Optional<AudioFeeder> optionalAudioFeeder = audioFeederService.get(feedTrackIndex);
        if (optionalAudioFeeder.isEmpty())
            return null;
        AudioFeeder audioFeeder = optionalAudioFeeder.get();
        audioFeederService.delete(audioFeeder);
        AudioFeeder audioFeederLoaded = audioFeedersLoaded.get(feedTrackIndex);
        if (audioFeederLoaded != null)
            audioFeedersLoaded.remove(feedTrackIndex);
        return audioFeeder;
    }

    public List<AudioFeeder> getLoadedAudioFeeders() {
        return audioFeederService.getAll().stream().filter(audioFeeder -> audioFeeder.getAudioIOFileManager() != null).toList();
    }

    public AudioFeeder getRandomAudioFeeder() {
        return audioFeederService.getRandom();
    }

    public AudioFeeder getRandomAudioFeeder(String sessionId) {
        return audioFeederService.getRandomBySessionId(sessionId);
    }

    public AudioFeeder checkAndUpdateStatus(long id) throws GetFeedException {
        //making sure that we don't override session
        Optional<AudioFeeder> optional = audioFeederService.get(id);
        if (optional.isEmpty())
            throw new NotFoundError("Feed doesn't exists in the database!");
        AudioFeeder audioFeeder = optional.get();
        if (audioFeeder.getAudioIOFileManager() != null)
            return audioFeeder;
        AudioFeeder audioFeederLoaded = audioFeedersLoaded.get(id);
        if (audioFeederLoaded == null)
            throw new NotFoundError("Feed had an error when loading!");
        CompletableFuture<AudioIOFileManager> future = audioFeederLoaded.getCompletableFuture();
        if (!future.isDone())
            return audioFeeder;
        try {
            audioFeeder.setAudioIOFileManager(future.get());
        } catch (ExecutionException | InterruptedException e) {
            audioFeeder.setErrorMessage("Cannot load the feed");
        }

        if (audioFeeder.getErrorMessage() != null)
            throw new GetFeedException(audioFeeder.getErrorMessage());
        audioFeedersLoaded.remove(audioFeeder.getId());
        audioFeederService.save(audioFeeder);
        return audioFeeder;
    }

    public void takeOwnershipShuffleAudioFeeders(String sessionId) {
        List<AudioFeeder> audioFeeders = audioFeederService.getShufflesAudioFeeders();
        audioFeeders.forEach(audioFeeder -> audioFeeder.setSessionId(sessionId));
        audioFeederService.save(audioFeeders);
    }

    public String[] feedNames() {
        String[] names = nasConnectorService.listSubFeeds("");
        names = ArrayUtils.addAll(names, nasConnectorService.listSubFiles("FEEDD"));
        names = ArrayUtils.addAll(names, nasConnectorService.listSubFiles("FEEDD/SUBFEEDA"));
        names = ArrayUtils.addAll(names, nasConnectorService.listSubFeeds("TESTTING-OPEN-MHZ"));
        return names;
    }

    public void removeAllLoading() {
        audioFeedersLoaded.clear();
        audioFeederService.deleteAllByAudioInfoNull();
        audioFeederService.deleteAllByAudioNegativeOne();
    }

    public List<Shortcut> getShortcuts() {
        List<Shortcut> shortcuts = shortcutService.getShortcuts();
        for (int i = 0; i < shortcuts.size(); i++) {
            Shortcut shortcut = shortcuts.get(i);
            int count = audioFeederService.countFeedNameAndLoaded(shortcut.getFeedName());
            String feedTitle = shortcut.getFeedName();
            feedTitle += count > 0 ? "(" + count + ")" : "";
            shortcut.setTitle(feedTitle);
            shortcuts.set(i, shortcut);
        }
        return shortcuts;
    }

    public void setShuffleStatus(String[] data) {
        List<Long> scanningAble = new ArrayList<>();
        for (String id : data)
            scanningAble.add(Long.valueOf(id));
        List<AudioFeeder> audioFeeders = audioFeederService.getAllByAudioInfoNotNull();
        audioFeeders.stream().filter(audioFeeder -> scanningAble.contains(audioFeeder.getId()))
                .forEach(audioFeeder -> {
                    audioFeeder.setIncludeInFullScreenShuffle(true);
                    audioFeederService.save(audioFeeder);
                });
    }


    public List<AudioFeeder> getShufflesAudioFeeders() {
        return audioFeederService.getShufflesAudioFeeders();
    }
    /**
     * This method is used to track the control of the audio feeder.
     * It will increase or decrease the file number based on the action.
     *
     * @param audioFeeder the audio feeder to track
     * @param action      the action to perform (NEXT or PREVIOUS)
     */
    public void trackControl(AudioFeeder audioFeeder, TrackAction action) {
        MoveTrackControl moveTrackControl = new MoveTrackControl(settingService.getPreferredRandomSettings(), audioFeeder);
        if (action == TrackAction.NEXT)
            //include getting new random track if the limit is reached
            moveTrackControl.increaseFileNo();
        else if (action == TrackAction.PREVIOUS)
            moveTrackControl.decreaseFileNo();
        updateAudioFeeder(audioFeeder);
    }
}