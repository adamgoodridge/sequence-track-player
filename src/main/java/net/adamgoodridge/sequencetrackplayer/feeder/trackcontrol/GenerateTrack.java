package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;


import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

import java.util.concurrent.*;

public class GenerateTrack extends TrackControl{
    private final FeedRequest feedRequest;
    public GenerateTrack(PreferredRandomSettings preferredRandomSettings, FeedRequest feedRequest) {
        super(preferredRandomSettings);
        this.feedRequest = feedRequest;
    }
    public AudioFeeder process() {
        feedRequest.removeStartingSlash();
        AudioFeeder audioFeeder = new AudioFeeder(feedRequest.getName());
        audioFeeder.setId(feedRequest.getFeedId());
        audioFeeder.setCompletableFuture(fetchTrackFromFileSystem(feedRequest));
        return audioFeeder;
    }

    public CompletableFuture<AudioIOFileManager> fetchTrackFromFileSystem(FeedRequest feedRequest) {

        return CompletableFuture.supplyAsync(() -> generateFeed(feedRequest)).orTimeout(40, TimeUnit.SECONDS);

    }

    private AudioIOFileManager generateFeed(FeedRequest feedRequest) {
        try {
            String fullSystemPath = feedRequest.getFullSystemPath();
            if (feedRequest.isRequestType(FeedRequestType.RANDOM))
                return processRandomTrackFromFile(feedRequest.getName());
            if (feedRequest.isRequestType(FeedRequestType.BOOKMARK))
                return this.getNasConnectorService().getBookmarkedTrack(feedRequest.getPath());
            return this.getNasConnectorService().getTrack(fullSystemPath, feedRequest.getFeedRequestType());
        } catch (GetFeedException e) {
            throw new CompletionException(e);
        }
    }

}
