package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;

import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import java.util.*;

public class GetIndexByPathStrategy implements IGetIndexStrategy {
	private RetrieveAudioFeeder retrieveAudioFeeder;
	public GetIndexByPathStrategy() {
		// default constructor
	}
	@Override
	public int getFolderIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedException {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		List<DataItem> folders = retrieveAudioFeeder.getSubFiles();

		Optional<Path> nextFolderToFind = retrieveAudioFeeder.getNextPathRemoveFromSearchFor();
		if (nextFolderToFind.isEmpty()) {
			throwExceptionMessage("Unexpected error: No next folder to find in path " + retrieveAudioFeeder.getSearchFor());
			return -1; // This line will never be reached, but is needed to satisfy the compiler
		}
		int index = IGetIndexStrategy.super.getIndexByPath(folders, nextFolderToFind.get());
		if (index == -1)
			throwExceptionMessage("Cannot find next folder: " + nextFolderToFind.get());

			// Return the index of the next folder if found
		return index;
		}

	@Override
	public int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedException {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		DataItem found = findIndexAndValidSubFile();
		return retrieveAudioFeeder.getSubFiles().indexOf(found);
	}

	@Override
	public void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedException {
		this.retrieveAudioFeeder = retrieveAudioFeeder;
		throwExceptionMessage(reason);
	}
	private void throwExceptionMessage(String reason) throws GetFeedException {
		throw new GetFeedException("Cannot find track for " + this.retrieveAudioFeeder.getSearchFor() + ", Reason: " + reason);
	}

	private DataItem findIndexAndValidSubFile() throws GetFeedException {
		List<DataItem> dataItems = retrieveAudioFeeder.getSubFiles();
		String searchValue = retrieveAudioFeeder.getSearchFor().toString();
		DataItem found = dataItems.stream().filter(d -> d.getFullPathLocalFileSystem().equals(searchValue)).findFirst().orElse(null);
		if(found == null)
			throw new GetFeedException(searchValue +" wasn't in the folder.");
		return found;
	}
}