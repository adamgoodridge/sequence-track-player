package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy;


import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import java.util.*;

public interface IGetIndexStrategy {
	int getFolderIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedError;
	int getAudioFileIndex(RetrieveAudioFeeder retrieveAudioFeeder) throws GetFeedError;
	void throwExceptionMessage(RetrieveAudioFeeder retrieveAudioFeeder, String reason) throws GetFeedError;
	default int getIndexByPath(List<DataItem> folders, Path nextFolderToFind) {
		return folders.stream()
				.map(DataItem::getFullPath)
				.toList()
				.indexOf(nextFolderToFind.toString());
	}
}
