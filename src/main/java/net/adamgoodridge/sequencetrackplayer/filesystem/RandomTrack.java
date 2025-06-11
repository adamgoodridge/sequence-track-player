package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.directory.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.utils.*;

import java.util.*;

public class RandomTrack {
	private final String slash = ConstantTextFileSystem.getInstance().getSlash();
	private final String feedName;
	private final PreferredRandomSettings preferredRandomSettings;
	private AudioIOFileManager audioIOFileManager;
	private List<DataItem> children;
	private final IDirectoryRepository directoryRepository;

	private static final Random rnd = new Random();

	public RandomTrack(String feedName, PreferredRandomSettings preferredRandomSettings) {
		this.feedName = feedName;
		this.preferredRandomSettings = preferredRandomSettings;
		this.directoryRepository = new FileSystemRepository();
	}
	public AudioIOFileManager compute() throws GetFeedException {

		String feedPath = feedName.replace("/",slash);
		feedPath = getString(feedPath);
		String currentPath = ConstantTextFileSystem.getInstance().getSharePath() + feedPath;
		while (true) {
			children = getFilesByDataItem(currentPath);
			List<DataItem> folders = filterByDirectories(children);
			if (folders.isEmpty()) {
				return getRandomTrack();
			}
			if (preferredRandomSettings.isDayRequired() && folders.get(0).getFileName().contains("day")) {
				folders = filteredFolderByPreferDay(preferredRandomSettings, folders);
				preferredRandomSettings.gottenDay();
			}
			int folderNo = rnd.nextInt(folders.size());
			currentPath = slash + folders.get(folderNo).getFullPathLocalFileSystem();
			audioIOFileManager = new AudioIOFileManager(children, folderNo, audioIOFileManager);
		}

	}

	private AudioIOFileManager getRandomTrack() {
		int trackNo = new GenerateMp3Index(children, preferredRandomSettings.getTime()).compute();
		return new AudioIOFileManager(children, trackNo, audioIOFileManager);
	}

	private static List<DataItem> filteredFolderByPreferDay(PreferredRandomSettings preferredRandomSettings, List<DataItem> folders) {
		return folders.stream().filter(r -> r.getFileName().contains(preferredRandomSettings.getDay())).toList();
	}

	private List<DataItem> getFilesByDataItem(String path) throws GetFeedException {
		// 		System.out.println(path);
		List<DataItem> children = directoryRepository.findDirectoryByNameEquals(path).subFilesMapToDataItems();
		if (children.isEmpty())
			throw new GetFeedException("A random track cannot be found in the folder: " + path);
		return children;
	}

	private String getString(String feedPath) {
		if(feedPath.startsWith(slash))
			feedPath = feedName.substring(1);
		return feedPath;
	}
	private static List<DataItem> filterByDirectories(List<DataItem> children) {
		return children
				.stream().filter(DataItem::isDirectory).toList();
	}
}
