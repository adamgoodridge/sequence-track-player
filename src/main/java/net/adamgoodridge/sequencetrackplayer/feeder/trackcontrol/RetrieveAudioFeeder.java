package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

import lombok.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import java.util.*;

public class RetrieveAudioFeeder {
	@Getter
	protected final Path searchFor;
	protected Path currentPath;
	@Getter
	protected List<DataItem> subFiles;
	@Getter
	protected List<DataItem> folders;
	private final IDirectoryRepository directoryRepository;
	private final IGetIndexStrategy getIndexStrategy;
	protected AudioIOFileManager audioIOFileManager;

	public RetrieveAudioFeeder(String searchFor, IGetIndexStrategy getIndexStrategy) {
		this(searchFor, getIndexStrategy, new FileSystemRepository());
	}
	public RetrieveAudioFeeder(String searchFor, IGetIndexStrategy getIndexStrategy, IDirectoryRepository directoryRepository) {
		this.getIndexStrategy = getIndexStrategy;
		this.searchFor = new Path(searchFor);
		this.directoryRepository = directoryRepository;
	}

	public AudioIOFileManager compute(){
		currentPath = this.searchFor.getTopPath();
		while (true) {
			getFilesByDataItemInCurrentPath();
			folders = filterByDirectories();
			if (folders.isEmpty()) {
				int fileIndex = getIndexStrategy.getAudioFileIndex(this);
				return new AudioIOFileManager(subFiles, fileIndex, audioIOFileManager);
			}
			int folderNo = getIndexStrategy.getFolderIndex(this);
			currentPath.addFile(folders.get(folderNo).getFileName());
			audioIOFileManager = new AudioIOFileManager(subFiles, folderNo, audioIOFileManager);
		}

	}

	protected void getFilesByDataItemInCurrentPath() {
		subFiles = directoryRepository.findDirectoryByNameEquals(currentPath.toString()).subFilesMapToDataItems();
		if (subFiles.isEmpty())
			getIndexStrategy. throwExceptionMessage(this,"path was empty: " + currentPath);
	}
	protected List<DataItem> filterByDirectories() {
		return subFiles.stream().filter(DataItem::isDirectory).toList();
	}
	public Optional<Path> getNextPathRemoveFromSearchFor() {
		if (needToGetToSearchPath()) {
			Path path = searchFor.getNextPathRemoveFromSearchFor(currentPath);
			return Optional.of(path);
		}
		return Optional.empty();

	}

	private boolean needToGetToSearchPath() {
		String currentPathString = currentPath.toString();
		String searchForString = searchFor.toString();
		return currentPathString.length() < searchForString.length();
	}
}
