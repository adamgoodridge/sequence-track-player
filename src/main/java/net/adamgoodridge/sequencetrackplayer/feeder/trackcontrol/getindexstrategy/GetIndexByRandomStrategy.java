package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.util.*;

public class GetIndexByRandomStrategy implements IGetIndexStrategy {
	private static final String CONTAINING_HOLIDAY_MONTH_REGEX =  ".*(Dec).*";
	private static final String HOLIDAY_DATE_REGEX = TimeUtils.getRegexWithHolidayDate();
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
			throw new GetRandomFeedError(retrieveAudioFeeder, "Cannot find next folder: " + nextFolderToFind.get());
		}
		if (folders.isEmpty())
			throw new GetRandomFeedError(retrieveAudioFeeder,"No folders found in path: " + retrieveAudioFeeder.getSearchFor());
		if (preferredRandomSettings.shouldFilterByDayOfWeek(folders.getFirst()))
			return findIndexPreferDay();
		if (preferredRandomSettings.shouldFilterByMonth(folders.getFirst()))
			return findIndexByDecember();
		if (preferredRandomSettings.isHolidayPeriod() && folders.getFirst().getFileName().matches(TimeUtils.getRegexWithDayDirectory()))
			return findIndexByHolidayPeriodDate();
		return rnd.getRandomNumber(folders.size());
	}

	@Override
	public int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> subFiles = retrieveAudioFeeder.getSubFiles();
		if (preferredRandomSettings.getTime() != NOT_FOUND && !subFiles.getFirst().getFullPath().contains(" ")) {
			//find a specific time
			return findIndexByTime();
		}
		int maxRandomNumber = this.retrieveAudioFeeder.getSubFiles().size();
		return rnd.getRandomNumber(maxRandomNumber);
	}


	private int findIndexByTime() {
		int time = preferredRandomSettings.getTime();
		//in case if there is no track for that exact hour
		do {
			String startTime = String.format("%02d",time);
			DataItem sampleFile = retrieveAudioFeeder.getSubFiles().getFirst();
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
		String regex = ".*(?i)"+preferredRandomSettings.getDayOfWeek()+".*";
		int index = pickRandomItemPerRegex(regex,this.retrieveAudioFeeder.getSubFiles());
		if (index == -1)
			throw new GetRandomFeedError(retrieveAudioFeeder,"Cannot find folder for day: " + preferredRandomSettings.getDayOfWeek());
		if(preferredRandomSettings.isHolidayPeriod())
			return findIndexByHolidayPeriodDate();
		return index;
	}

	private int findIndexByHolidayPeriodDate() {
		int index = pickRandomItemPerRegex(HOLIDAY_DATE_REGEX,this.retrieveAudioFeeder.getSubFiles());
		if (index == -1)
			throw new GetRandomFeedError(retrieveAudioFeeder,"Cannot find folder for date in holiday period:");
		return index;
	}

	private int findIndexByDecember() {
		int index = pickRandomItemPerRegex(CONTAINING_HOLIDAY_MONTH_REGEX,this.retrieveAudioFeeder.getSubFiles());
		if (index == -1)
			throw new GetRandomFeedError(retrieveAudioFeeder,"There is no folder for month in holiday period for the year.");
		return index;
	}
}
