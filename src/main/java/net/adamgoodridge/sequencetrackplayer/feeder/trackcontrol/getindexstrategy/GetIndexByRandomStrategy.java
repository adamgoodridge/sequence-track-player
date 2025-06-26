package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

import java.util.*;

public class GetIndexByRandomStrategy implements IGetIndexStrategy {
	private static final Random rnd = new Random();
	private final PreferredRandomSettings preferredRandomSettings;
	private RetrieveAudioFeeder retrieveAudioFeeder;
	public GetIndexByRandomStrategy(PreferredRandomSettings preferredRandomSettings) {
		this.preferredRandomSettings = preferredRandomSettings;
	}


	public int getFolderIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedException {
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
		}
		return rnd.nextInt(folders.size());
	}

	@Override
	public int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> subFiles = retrieveAudioFeeder.getSubFiles();
		if (preferredRandomSettings.getTime() != -1 && !subFiles.get(0).getFullPath().contains(" ")) {
			//find a specific time
			return findIndexByTime();
		}
		return  rnd.nextInt(this.retrieveAudioFeeder.getSubFiles().size());
	}

	@Override
	public void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedException {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		throwExceptionMessage(reason);
	}


	private void throwExceptionMessage(String reason) throws GetFeedException {
		throw new GetFeedException("Cannot find random track for " + retrieveAudioFeeder.getSearchFor() + ", Reason: " + reason);
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
		return rnd.nextInt(this.retrieveAudioFeeder.getSubFiles().size());
	}

	public int pickRandomItemPerRegex(String regex, List<DataItem> subFiles) {
		List<DataItem> matchingItems = subFiles.stream()
				.filter(item -> item.getFullPath().matches(regex))
				.toList();
		if (matchingItems.isEmpty()) {
			return -1; // No matching items found
		}
		int randomIndex = rnd.nextInt(matchingItems.size());
		DataItem match = matchingItems.get(randomIndex);// Return a random index from the matching items
		return this.retrieveAudioFeeder.getSubFiles().indexOf(match);
	}


	private int findIndexPreferDay() {
		String regex = ".*"+preferredRandomSettings.getDay()+".*";
		return pickRandomItemPerRegex(regex,this.retrieveAudioFeeder.getSubFiles());
	}
}
