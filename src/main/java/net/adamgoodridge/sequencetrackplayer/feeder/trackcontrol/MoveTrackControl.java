package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.slf4j.*;

import java.util.*;

public class MoveTrackControl extends TrackControl {
    private static final Logger logger = LoggerFactory.getLogger(
            MoveTrackControl.class.getName());
    private AudioFeeder audioFeeder;


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
        AudioIOFileManager audioIOFileManager = goToPreviousFolder(audioFeeder.getAudioIOFileManager());
        //not directory, nothing needs to done
        if (audioIOFileManager.getFile().isDirectory())
            audioIOFileManager = diveInFolder(audioIOFileManager, false);

        audioFeeder.setAudioIOFileManager(audioIOFileManager);
    }

    private AudioIOFileManager goToPreviousFolder(AudioIOFileManager audioIOFileManagerIn) {
        AudioIOFileManager audioIOFileManager = audioIOFileManagerIn;
        while (audioIOFileManager.getFileNo() == -1) {
            audioIOFileManager = audioIOFileManager.getParentDir();
            if (audioIOFileManager == null)
                throw new JsonReturnError("Gone back to the start of the file");
            audioIOFileManager.decreaseFileNo();
        }
        return audioIOFileManager;
    }

    public void increaseFileNo() {
        if (shouldGetNewRandomTrack(audioFeeder))
                getNewRandomTrack();
        nextTrack(audioFeeder);
    }


    private void getNewRandomTrack() {
        FeedRequest feedRequest = FeedRequest.builder()
                .name(audioFeeder.getFeedName())
                .feedId(audioFeeder.getId())
                .feedRequestType(FeedRequestType.RANDOM)
                .build();
        audioFeeder = new AudioFeederFactory(this.getPreferredRandomSettings(),
                feedRequest).process();
    }

    private boolean shouldGetNewRandomTrack(AudioFeeder audioFeeder) {
        if (!this.getPreferredRandomSettings().isRegularlyTrackChange())
            //todo
            return audioFeeder.getCurrentTrackCount() >= this.getPreferredRandomSettings().getTrackCurrentCount();
        return false;
    }

    private void nextTrack(AudioFeeder audioFeeder) {
        if (audioFeeder.getAudioIOFileManager() == null) {
            audioFeeder.setAudioIOFileManager(audioFeeder.getPreviousAudioPlayed());
            //as we already decreased the file index therefore it is now at it's limit
            audioFeeder.getAudioIOFileManager().increaseFileNo();
        } else {
            AudioIOFileManager audioIOFileManager = audioFeeder.getAudioIOFileManager();
            audioFeeder.setPreviousAudioPlayed(audioIOFileManager);
            audioIOFileManager.increaseFileNo();
            goUpDirectoryStructureIfRequired(audioIOFileManager);
            goDownDirectoryStructureIfRequired(audioIOFileManager);
            //we have to  reassign it as if different object it will be new reference
            audioFeeder.setAudioIOFileManager(audioIOFileManager);
            audioFeeder.increaseTrackCount();
        }
        audioFeeder.getAudioIOFileManager().setCurrentPosition(0);

    }
    private void goUpDirectoryStructureIfRequired(AudioIOFileManager audioIOFileManager) {
        while (audioIOFileManager.isAtEnd()) {
            audioIOFileManager = audioIOFileManager.getParentDir();
            if (audioIOFileManager == null)
                throw new JsonReturnError("end of files");
            //visiting the next folder in which the parent folder on before
            audioIOFileManager.increaseFileNo();
        }
    }
    private void goDownDirectoryStructureIfRequired(AudioIOFileManager audioIOFileManager) {
        while (audioIOFileManager.isDir()) {
            AudioIOFileManager temp = diveInFolder(audioIOFileManager, true);
            if (Boolean.TRUE.equals(temp.isEmpty())) {
                audioIOFileManager.increaseFileNo();
            } else {
                audioIOFileManager = temp;
            }
        }
    }

    private AudioIOFileManager diveInFolder(AudioIOFileManager audioIOFileManager, Boolean isAtStartOfFolder) {

        List<DataItem> children;
        //TODO
        children = super.getNasConnectorService().getFiles(audioIOFileManager.getUrl());
        int index = Boolean.TRUE.equals(isAtStartOfFolder) ? 0 : children.size() - 1;
        audioIOFileManager = new AudioIOFileManager(children, index, audioIOFileManager);

        logger.warn("Failure to get the next file.");

        return audioIOFileManager;
    }


}
