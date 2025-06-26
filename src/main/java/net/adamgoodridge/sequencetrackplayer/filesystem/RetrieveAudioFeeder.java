package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;

import java.util.*;

public class RetrieveAudioFeeder {
	protected final Path searchFor;
	protected Path currentPath;
	protected List<DataItem> subFiles;
	private List<DataItem> folders;
	private final IDirectoryRepository directoryRepository;
	private final IGetIndexStrategy getIndexStrategy;
	protected AudioIOFileManager audioIOFileManager;
	public RetrieveAudioFeeder(String searchFor, IGetIndexStrategy getIndexStrategy) {
		this.getIndexStrategy = getIndexStrategy;
		this.searchFor = new Path(searchFor);
		this.directoryRepository = new FileSystemRepository();
	}

	public AudioIOFileManager compute() throws GetFeedException {
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

	public List<DataItem> getSubFiles() {
		return subFiles;
	}

	public List<DataItem> getFolders() {
		return folders;
	}

	public Path getSearchFor() {
		return searchFor;
	}

	protected void getFilesByDataItemInCurrentPath() throws GetFeedException {
		subFiles = directoryRepository.findDirectoryByNameEquals(currentPath.toString()).subFilesMapToDataItems();
		if (subFiles.isEmpty())
			getIndexStrategy.throwExceptionMessage(this,"path was empty: " + currentPath);
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

	public Path getCurrentPath() {
		return currentPath;
	}

	private boolean needToGetToSearchPath() {
		String currentPathString = currentPath.toString();
		String searchForString = searchFor.toString();
		return currentPathString.length() < searchForString.length();
	}
}
