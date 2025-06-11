package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.directory.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

public abstract class TrackControl {
    //need for loading feeds

    private static final int MAX_AMOUNT_OF_TRIES = 10;
    private final NasConnectorService nasConnectorService;
    private final PreferredRandomSettings preferredRandomSettings;
    protected TrackControl(PreferredRandomSettings preferredRandomSettings) {
        this.nasConnectorService = new NasConnectorFileSystem();
        this.preferredRandomSettings = preferredRandomSettings;
    }

    protected AudioIOFileManager processRandomTrackFromFile(String feedName) {
        int tires = 0;
        do {
            try {
                return nasConnectorService.getRanTrack(feedName, preferredRandomSettings);
            } catch (IllegalArgumentException | GetFeedException | ServerFailConnectionError ex) {
                tires++;
            }
        } while (tires < MAX_AMOUNT_OF_TRIES);
        throw new JsonReturnError(ConstantText.ERROR_RANDOM_TRACK);
    }

    public NasConnectorService getNasConnectorService() {
        return nasConnectorService;
    }

    public PreferredRandomSettings getPreferredRandomSettings() {
        return preferredRandomSettings;
    }
}
