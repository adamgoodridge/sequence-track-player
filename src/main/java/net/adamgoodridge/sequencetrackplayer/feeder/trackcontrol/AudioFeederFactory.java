package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;


import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

import java.util.concurrent.*;

public class AudioFeederFactory {
    private static final int MAX_AMOUNT_OF_TRIES = 3; // Maximum number of attempts to fetch a track
    private final FeedRequest feedRequest;
    private final PreferredRandomSettings preferredRandomSettings;

	public AudioFeederFactory(PreferredRandomSettings preferredRandomSettings, FeedRequest feedRequest) {
		this.feedRequest = feedRequest;
		this.preferredRandomSettings = preferredRandomSettings;
	}

	public AudioFeeder process() {
        AudioFeeder audioFeeder = new AudioFeeder(feedRequest.getName());
		if (feedRequest.hasId())
			audioFeeder.setId(feedRequest.getFeedId());
        audioFeeder.setCompletableFuture(createFeed(feedRequest));
        return audioFeeder;
    }

    private CompletableFuture<AudioIOFileManager> createFeed(FeedRequest feedRequest) {

        return CompletableFuture.supplyAsync(() -> generateFeed(feedRequest)).orTimeout(40, TimeUnit.SECONDS);

    }

    private AudioIOFileManager generateFeed(FeedRequest feedRequest) {

        int tires = 0;
        do {
        try {
			RetrieveAudioFeeder retrieveAudioFeeder;
			IGetIndexStrategy indexStrategy;
			if( feedRequest.isRequestType(FeedRequestType.RANDOM) ) {
                indexStrategy = new GetIndexByRandomStrategy(preferredRandomSettings, new RandomNumberGenerator());
			} else {
				indexStrategy = new GetIndexByPathStrategy();
            }
			retrieveAudioFeeder= new RetrieveAudioFeeder(feedRequest.getName(), indexStrategy);
			return retrieveAudioFeeder.compute();
        } catch (GetFeedError e) {
            tires++;
			System.out.println(
					e.getMessage()
			);
        }
        } while (tires < MAX_AMOUNT_OF_TRIES);
        throw new JsonReturnError(ConstantText.ERROR_RANDOM_TRACK);
    }

}
