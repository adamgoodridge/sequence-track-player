package net.adamgoodridge.sequencetrackplayer.feeder;


import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.util.concurrent.*;
@Service
public class AudioIOFileManagerService {
	private static final int MAX_AMOUNT_OF_TRIES = 3; // Maximum number of attempts to fetch a track

	private final SettingService settingService;
	private final RandomNumberGenerator randomNumberGenerator;
	@Autowired
	public AudioIOFileManagerService(SettingService settingService, RandomNumberGenerator randomNumberGenerator) {
		//this.constantTextProperties = constantTextProperties;
		this.settingService = settingService;
		this.randomNumberGenerator = randomNumberGenerator;
	}


	public AudioFeeder createAudioFeeder(FeedRequest feedRequest) {
        AudioFeeder audioFeeder = new AudioFeeder(feedRequest.getName());
		if (feedRequest.hasId())
			audioFeeder.setId(feedRequest.getFeedId());
		CompletableFuture<AudioIOFileManager> completableFuture = createCompletableFuture(feedRequest);
        audioFeeder.setCompletableFuture(completableFuture);
		try {
			completableFuture.join();
		} catch (CompletionException e) {
			audioFeeder.setErrorMessage(e.getMessage());
		}
		return audioFeeder;
    }

	private CompletableFuture<AudioIOFileManager> createCompletableFuture(FeedRequest feedRequest) {
		if(true)
			return CompletableFuture.supplyAsync(() -> generateFeed(feedRequest)).orTimeout(40, TimeUnit.SECONDS);
		AudioIOFileManager audioIOFileManager = generateFeed(feedRequest);
		return CompletableFuture.completedFuture(
			audioIOFileManager
		);
	}


		public AudioIOFileManager generateFeed(FeedRequest feedRequest) {
		int debug = randomNumberGenerator.getRandomNumber(2111);
		int tires = 0;
		String lastErrorMessage;
		do {
			try {
				RetrieveAudioFeeder retrieveAudioFeeder;
				if( feedRequest.isRequestType(FeedRequestType.RANDOM) ) {
					retrieveAudioFeeder = generateFeedByRandom(feedRequest);
				} else {
					retrieveAudioFeeder = generateFeedByPath(feedRequest);
				}
				return retrieveAudioFeeder.compute();
			} catch (GetFeedError e) {
				tires++;
				lastErrorMessage = e.getMessage();
			}
		} while (tires < MAX_AMOUNT_OF_TRIES);
		throw new GetFeedError(ConstantText.ERROR_RANDOM_TRACK + " The last error was: " + lastErrorMessage);
	}
	private RetrieveAudioFeeder generateFeedByRandom(FeedRequest feedRequest) {
		GetIndexByRandomStrategy indexStrategy = new GetIndexByRandomStrategy(settingService.getPreferredRandomSettings(), randomNumberGenerator);
		String path = feedRequest.getPath() != null ? feedRequest.getPath() : feedRequest.getName();
		return new RetrieveAudioFeeder(path, indexStrategy);
	}

	private RetrieveAudioFeeder generateFeedByPath(FeedRequest feedRequest) {
		GetIndexByPathStrategy indexStrategy = new GetIndexByPathStrategy();
		String path = feedRequest.getPath();
		return new RetrieveAudioFeeder(path, indexStrategy);
	}

}
