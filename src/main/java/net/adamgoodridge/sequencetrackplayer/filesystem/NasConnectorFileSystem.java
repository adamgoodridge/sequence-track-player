package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class NasConnectorFileSystem implements NasConnectorService {
    private final IDirectoryRepository directoryRepository;


    private static final Logger logger = LoggerFactory.getLogger(NasConnectorFileSystem.class.getName());

    @Autowired
    public NasConnectorFileSystem() {
        this.directoryRepository = new FileSystemRepository();
    }

    @Override
    public String[] listSubFiles(String path) {
        return directoryRepository.findDirectoryByNameEquals(path).getSubFiles();
    }
    @Override
    public String[] listSubFeeds(String path) {
        Directory directory = directoryRepository.findDirectoryByNameEquals(path);
        checkIfDirEmpty(directory);
        String[] names = directory.getSubFiles();
        String prefix = path.isEmpty() ? "": path + "/";
        for (int i = 0; i < Objects.requireNonNull(names).length; i++) {
            names[i] = prefix + names[i];
        }
        return names;
    }
    private void checkIfDirEmpty(Directory directory) {
        if(directory.getSubFiles() == null) {
            logger.error("no feeds cannot be found when trying to listing their names.");
            throw new NotFoundError("No feeds cannot be found.");
        }
    }

    public List<DataItem> getFiles(String url) {
        String path = url.replace(ConstantText.DEFAULT_SERVER_URL,"").replace("/", ConstantTextFileSystem.getInstance().getSlash() );
        Directory directory = directoryRepository.findDirectoryByNameEquals(path);
        checkIfDirEmpty(directory);
        return directory.subFilesMapToDataItems();
    }
    @Override
    public AudioIOFileManager getBookmarkedTrack(String fullPath) throws GetFeedException {
        return getTrack(fullPath, FeedRequestType.BOOKMARK);
    }

    @Override
    public String logoPath(String feedName) {
        String[] split = feedName.split("/");
        Directory logosDirectory = directoryRepository.findDirectoryByNameEquals(ConstantTextFileSystem.getInstance().getSharePath()  + "exclude"
                + ConstantTextFileSystem.getInstance().getSlash() + "logos" + ConstantTextFileSystem.getInstance().getSlash() + feedName +".png");

        if(split.length > 2) {
            String moreSpecificName = getSubFeedLogo(split, logosDirectory);
            if (moreSpecificName != null) return moreSpecificName;
        }

        return logosDirectory.containItem(feedName) ? feedName : "";
    }

    private static String getSubFeedLogo(String[] split, Directory logosDirectory) {
        String moreSpecificName = split[0] + "-" + split[1];
        if(logosDirectory.containItem(moreSpecificName)) {
            return moreSpecificName;
        }
        return null;
    }

    @Override
    public List<DateForCalendarView> listDaysInYears(final String feedPath) {
        List<DateForCalendarView> dates = new ArrayList<>();
        Directory directory = directoryRepository.findDirectoryByNameEquals(feedPath.replace("/",ConstantTextFileSystem.getInstance().getSlash() ));
        for(String folder: directory.getSubFilesFullPath()) {
            String[] days = directoryRepository.findDirectoryByNameEquals(folder).getSubFiles();
            assert days != null;
            for(String day: days) {
                dates.add(new DateForCalendarView(day.split("_")[0], day.replace(ConstantTextFileSystem.getInstance().getSharePath(), "")));
            }
        }
        return dates;
    }
    @Override
    public AudioIOFileManager getTrack(final String fullPath, final FeedRequestType feedRequestType) throws GetFeedException {
//substring get rid of the first slash e.g /folder/folder2 = folder/folder2
        String[] directories = getDirectoriesWithFileShare(fullPath);
        StringBuilder currentPath = new StringBuilder();
        currentPath.append(ConstantTextFileSystem.getInstance().getSharePath()).append(directories[0]);
        AudioIOFileManager audioIOFileManager = null;
        //i=1 as first element should be empty
        int i = 1;
        do {
            List<DataItem> dataItems = getDataItems(feedRequestType, currentPath + ConstantTextFileSystem.getInstance().getSlash());
            AudioIOFileManager child= findFileInCurrentFile(dataItems, currentPath, directories[i]);
            child.setParentDir(audioIOFileManager);
            audioIOFileManager = child;
            i++;
        } while (i < directories.length);
        return audioIOFileManager;
    }

    private AudioIOFileManager findFileInCurrentFile(List<DataItem> dataItems, StringBuilder currentPath, String directories) throws GetFeedException {
        currentPath.append(ConstantTextFileSystem.getInstance().getSlash()).append(directories);
        DataItem found = findIndexAndValidSubFile(dataItems, currentPath.toString());
        int folderNo = dataItems.indexOf(found);
        return new AudioIOFileManager(dataItems, folderNo);
    }

    private static String[] getDirectoriesWithFileShare(String fullPath) {
        String pathWithoutSlash = fullPath.startsWith("/") ? fullPath.substring(1): fullPath;
        String path = pathWithoutSlash.replace("%20"," ").replace(ConstantTextFileSystem.getInstance().getSharePath(),"");
        return path.split(Pattern.quote(ConstantTextFileSystem.getInstance().getSlash()));
    }

    private List<DataItem> getDataItems(FeedRequestType feedRequestType, String currentPath) throws GetFeedException {
        List<DataItem> dataItems = directoryRepository.findDirectoryByNameEquals(currentPath).subFilesMapToDataItems();
        if(dataItems.isEmpty())
            throw new GetFeedException("The folder ("+ currentPath +") is empty therefore the "+ feedRequestType.label +" couldn't be found as IO Error, folder was empty");
        return dataItems;
    }

    private DataItem findIndexAndValidSubFile(List<DataItem> dataItems, String searchValue) throws GetFeedException {
        DataItem found = dataItems.stream().filter(d -> d.getFullPathLocalFileSystem().equals(searchValue)).findFirst().orElse(null);
        if(found == null)
            throw new GetFeedException("The file wasn't in the folder.");
        return found;
    }
    //if the user wants to, this method pick track based the time select


}