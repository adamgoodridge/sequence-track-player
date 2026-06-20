package net.adamgoodridge.sequencetrackplayer.exceptions.errors;

import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;

public class GetRandomFeedError extends ShufflePlayerError {
    public GetRandomFeedError(RetrieveAudioFeeder retrieveAudioFeeder, String reason) {
        super("Cannot find random track for " + retrieveAudioFeeder.getSearchFor() + ", Reason: " + reason);
    }

}
