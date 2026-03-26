package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.util.*;

public class GetIndexByRandomStrategy implements IGetIndexStrategy {
	private static final int NOT_FOUND = -1;

	private final PreferredRandomSettings preferredRandomSettings;
	private final RandomNumberGenerator rnd;
	private RetrieveAudioFeeder retrieveAudioFeeder;
	public GetIndexByRandomStrategy(PreferredRandomSettings preferredRandomSettings, RandomNumberGenerator randomNumberGenerator) {
		this.preferredRandomSettings = preferredRandomSettings;
		rnd = randomNumberGenerator;
	}


	public int getFolderIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedError {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> folders = retrieveAudioFeeder.getFolders();
		Optional<Path> nextFolderToFind = retrieveAudioFeeder.getNextPathRemoveFromSearchFor();
		if (nextFolderToFind.isPresent()) {
			int index = getIndexByPath(folders, nextFolderToFind.get());
			if (index != NOT_FOUND)
				return index;
			throwExceptionMessage("Cannot find next folder: " + nextFolderToFind.get());
		}
		if (preferredRandomSettings.shouldFilterByDay(folders.get(0)))
				return findIndexPreferDay();
		return rnd.getRandomNumber(folders.size());
	}

	@Override
	public int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> subFiles = retrieveAudioFeeder.getSubFiles();
		if (preferredRandomSettings.getTime() != NOT_FOUND && !subFiles.get(0).getFullPath().contains(" ")) {
			//find a specific time
			return findIndexByTime();
		}
		int maxRandomNumber = this.retrieveAudioFeeder.getSubFiles().size();
		return rnd.getRandomNumber(maxRandomNumber);
	}

	@Override
	public void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedError {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		throwExceptionMessage(reason);
	}


	private void throwExceptionMessage(String reason) throws GetFeedError {
		throw  new GetFeedError("Cannot find random track for " + retrieveAudioFeeder.getSearchFor() + ", Reason: " + reason);
	}
	private int findIndexByTime() {
		int time = preferredRandomSettings.getTime();
		//in case if there is no track for that exact hour
		do {
			String startTime = String.format("%02d",time);
			DataItem sampleFile = retrieveAudioFeeder.getSubFiles().get(0);
			String regex = ".*_" + startTime + (sampleFile.getFileName().contains("_TO_") ? ".*": "-[0-5].*");
			int matchingIndex = pickRandomItemPerRegex(regex, retrieveAudioFeeder.getSubFiles());

			if (matchingIndex != NOT_FOUND)
				return matchingIndex;

			time = (time + 1) % 23;
		} while (time != preferredRandomSettings.getTime());

		//if not found, return a random track
		return rnd.getRandomNumber(this.retrieveAudioFeeder.getSubFiles().size());
	}

	public int pickRandomItemPerRegex(String regex, List<DataItem> subFiles) {
		List<DataItem> matchingItems = subFiles.stream()
				.filter(item -> item.getFileName().matches(regex))
				.toList();
		if (matchingItems.isEmpty())
			return NOT_FOUND;
		int randomIndex = rnd.getRandomNumber(matchingItems.size());
		DataItem match = matchingItems.get(randomIndex);// Return a random index from the matching items
		return this.retrieveAudioFeeder.getSubFiles().indexOf(match);
	}


	private int findIndexPreferDay() {
		String regex = ".*"+preferredRandomSettings.getDay()+".*";
		int randomIndex = pickRandomItemPerRegex(regex, this.retrieveAudioFeeder.getSubFiles());
		if (randomIndex != NOT_FOUND)
			return randomIndex;
		throwExceptionMessage("Cannot find folder for day: " + preferredRandomSettings.getDay());
		return NOT_FOUND; // Unreachable, but required to satisfy the compiler
	}
}
