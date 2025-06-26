package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

public abstract class TrackControl {
    //need for loading feeds

    private final NasConnectorService nasConnectorService;
    private final PreferredRandomSettings preferredRandomSettings;
    protected TrackControl(PreferredRandomSettings preferredRandomSettings) {
        this.nasConnectorService = new NasConnectorFileSystem();
        this.preferredRandomSettings = preferredRandomSettings;
    }


    public NasConnectorService getNasConnectorService() {
        return nasConnectorService;
    }

    public PreferredRandomSettings getPreferredRandomSettings() {
        return preferredRandomSettings;
    }
}
