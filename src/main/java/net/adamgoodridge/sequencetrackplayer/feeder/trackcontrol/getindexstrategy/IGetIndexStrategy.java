package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;


import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import java.util.*;

public interface IGetIndexStrategy {
	int getFolderIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedException;
	int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedException;
	void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedException;
	default int getIndexByPath(List<DataItem> folders, Path nextFolderToFind) {
		return folders.stream()
				.map(DataItem::getFullPath)
				.toList()
				.indexOf(nextFolderToFind.toString());
	}
}
