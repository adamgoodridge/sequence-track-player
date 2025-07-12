package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

import java.util.*;

public class GetIndexByRandomStrategy implements IGetIndexStrategy {
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

			if (index != -1) {
				// Return the index of the next folder if found
				return index;
			}
			throwExceptionMessage("Cannot find next folder: " + nextFolderToFind.get());
		}
		if (preferredRandomSettings.shouldFilterByDay(folders.get(0))) {
			int index = findIndexPreferDay();
			if(index != -1) {
				// Return a random match from found indices
				return index;
			}
			throwExceptionMessage("Cannot find folder for day: " + preferredRandomSettings.getDay());
		}
		return rnd.getRandomNumber(folders.size());
	}

	@Override
	public int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> subFiles = retrieveAudioFeeder.getSubFiles();
		if (preferredRandomSettings.getTime() != -1 && !subFiles.get(0).getFullPath().contains(" ")) {
			//find a specific time
			return findIndexByTime();
		}
		int maxRandomNumber = this.retrieveAudioFeeder.getSubFiles().size();
		System.out.println("Max random number: " + maxRandomNumber);
		int result = rnd.getRandomNumber(maxRandomNumber);
		System.out.println("Random index: " + result);
		return result;
	}

	@Override
	public void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedError {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		throwExceptionMessage(reason);
	}


	private void throwExceptionMessage(String reason) throws GetFeedError {
		throw new GetFeedError("Cannot find random track for " + retrieveAudioFeeder.getSearchFor() + ", Reason: " + reason);
	}
	private int findIndexByTime() {
		int time = preferredRandomSettings.getTime();
		//in case if there is no track for that exact hour
		do {
			String startTime = String.format("%02d",time);
			DataItem sampleFile = retrieveAudioFeeder.getSubFiles().get(0);
			String regex = ".*_" + startTime + (sampleFile.getFileName().contains("_TO_") ? ".*": "-[0-5].*");
			int matchingIndex = pickRandomItemPerRegex(regex, retrieveAudioFeeder.getSubFiles());

			if (matchingIndex != -1) {
				// Return a random match from found indices
				return matchingIndex;
			}

			time = (time + 1) % 23;
		} while (time != preferredRandomSettings.getTime());

		//if not found, return a random track
		return rnd.getRandomNumber(this.retrieveAudioFeeder.getSubFiles().size());
	}

	public int pickRandomItemPerRegex(String regex, List<DataItem> subFiles) {
		List<DataItem> matchingItems = subFiles.stream()
				.filter(item -> item.getFullPath().matches(regex))
				.toList();
		if (matchingItems.isEmpty()) {
			return -1; // No matching items found
		}
		int randomIndex = rnd.getRandomNumber(matchingItems.size());
		System.out.println(matchingItems.get(0).getFullPath());
		DataItem match = matchingItems.get(randomIndex);// Return a random index from the matching items
		return this.retrieveAudioFeeder.getSubFiles().indexOf(match);
	}


	private int findIndexPreferDay() {
		String regex = ".*"+preferredRandomSettings.getDay()+".*";
		return pickRandomItemPerRegex(regex,this.retrieveAudioFeeder.getSubFiles());
	}
}
