package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.slf4j.*;

import java.util.*;

public class MoveTrackControl extends TrackControl {
    private static final Logger logger = LoggerFactory.getLogger(
            MoveTrackControl.class.getName());
    private AudioFeeder audioFeeder;
    private boolean needsToRandomize = false;

    public MoveTrackControl(PreferredRandomSettings preferredRandomSettings, AudioFeeder audioFeeder) {
        super(preferredRandomSettings);
        this.audioFeeder = audioFeeder;
    }
    public void decreaseFileNo() {
        //in-case you are at the end of the files
        AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
        if (audioIOFileManager != null) {
            setPreviousFolder();
        } else {
            audioIOFileManager = audioFeeder.getPreviousAudioPlayed();
            //as we already increased the file index therefore it is now at it's limit
            audioIOFileManager.decreaseFileNo();
            audioFeeder.decreaseTrackCount();
        }
        //as there is new track loaded
        audioFeeder.getAudioIOFileManager().setCurrentPosition(0);
    }

    private void setPreviousFolder() {
        audioFeeder.setPreviousAudioPlayed(audioFeeder.getAudioIOFileManager());
        audioFeeder.getAudioIOFileManager().decreaseFileNo();
        //needs to go back to a folder
        AudioIOFileManager audioIOFileManager = goToPreviousFolder();
        //not directory, nothing needs to done
        if (audioIOFileManager.getFile().isDirectory())
            audioIOFileManager = diveInFolder(audioIOFileManager, false);

        audioFeeder.setAudioIOFileManager(audioIOFileManager);
    }

    private AudioIOFileManager goToPreviousFolder() {
        AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
        while (audioIOFileManager.getFileNo() == -1) {
            audioIOFileManager = audioIOFileManager.getParentDir();
            if (audioIOFileManager == null)
                throw new JsonReturnError("Gone back to the start of the file");
            audioIOFileManager.decreaseFileNo();
        }
        return audioIOFileManager;
    }

    public void increaseFileNo() {
        if (shouldGetNewRandomTrack()) {
            needsToRandomize = true;
        } else {
            nextTrack();
        }
    }


    private void getNewRandomTrack() {
        FeedRequest feedRequest = FeedRequest.builder()
                .name(audioFeeder.getFeedName())
                .feedId(audioFeeder.getId())
                .feedRequestType(FeedRequestType.RANDOM)
                .build();
        audioFeeder = new AudioFeederFactory(this.getPreferredRandomSettings(),feedRequest).process();

    }

    private boolean shouldGetNewRandomTrack() {
        if (this.getPreferredRandomSettings().isRegularlyTrackChange())
            //todo
            return audioFeeder.getCurrentTrackCount() >= this.getPreferredRandomSettings().getTrackCurrentCount();
        return false;
    }

    private void nextTrack() {
        if (audioFeeder.getAudioIOFileManager() == null) {
            audioFeeder.setAudioIOFileManager(audioFeeder.getPreviousAudioPlayed());
            //as we already decreased the file index therefore it is now at it's limit
            audioFeeder.getAudioIOFileManager().increaseFileNoAndResetCurrentPosition();
        } else {
            AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
            audioFeeder.setPreviousAudioPlayed(audioIOFileManager);
            audioIOFileManager.increaseFileNoAndResetCurrentPosition();
            goUpDirectoryStructureIfRequired();
            goDownDirectoryStructureIfRequired();
            //we have to  reassign it as if different object it will be new reference
            }
        audioFeeder.getAudioIOFileManager().setCurrentPosition(0);

    }
    private void goUpDirectoryStructureIfRequired() {
        AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
        while (audioIOFileManager.isAtEnd()) {
            audioIOFileManager = audioIOFileManager.getParentDir();
            if (audioIOFileManager == null)
                throw new JsonReturnError("end of files");
            //visiting the next folder in which the parent folder on before
            audioIOFileManager.increaseFileNoAndResetCurrentPosition();
        }
        audioFeeder.setAudioIOFileManager(audioIOFileManager);
    }
    private void goDownDirectoryStructureIfRequired() {
        AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
        while (audioIOFileManager.isDir()) {
            AudioIOFileManager temp = diveInFolder(audioIOFileManager, true);
            if (Boolean.TRUE.equals(temp.isEmpty())) {
                audioIOFileManager.increaseFileNoAndResetCurrentPosition();
            } else {
                audioIOFileManager = temp;
            }
            audioFeeder.setAudioIOFileManager(audioIOFileManager);
        }
    }

    private AudioIOFileManager diveInFolder(AudioIOFileManager audioIOFileManager, Boolean isAtStartOfFolder) {

        List<DataItem> children;
        //TODO
        children = super.getNasConnectorService().getFiles(audioIOFileManager.getCurrentFullPath());
        int index = Boolean.TRUE.equals(isAtStartOfFolder) ? 0 : children.size() - 1;
        audioIOFileManager = new AudioIOFileManager(children, index, audioIOFileManager);
        return audioIOFileManager;
    }
    public boolean isNeedsToRandomize() {
        return needsToRandomize;
    }


}
