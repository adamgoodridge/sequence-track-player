package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
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
    public String[] listSubFeeds(String pathValue) {
        Path path = new Path(pathValue);
        String[] subFiles = listSubFiles(path.toString());
        if(pathValue.isEmpty())
            return subFiles; //if path is empty, then return the root directory files
        for (int i = 0; i < Objects.requireNonNull(subFiles).length; i++) {
            subFiles[i] = pathValue + "/" + subFiles[i];
        }
        return subFiles;
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
    public AudioIOFileManager getBookmarkedTrack(String fullPath) throws GetFeedError {
        return getTrack(fullPath, FeedRequestType.BOOKMARK);
    }

    @Override
    public String logoPath(String feedName) {
        String[] split = feedName.split("/");
        Directory logosDirectory = directoryRepository.findDirectoryByNameEquals(getLogoDirectoryPath());
        String searchValue;
        if(split.length > 2) {
            searchValue = getSubFeedName(split);
        } else {
            searchValue = split[0];
        }

        return logosDirectory.containItem(searchValue) ? feedName : "";
    }

    private static String getSubFeedName(String[] split) {
        return split[0] + "-" + split[1];
    }

    private static String getLogoDirectoryPath() {
        return ConstantTextFileSystem.getInstance().getSharePath() + "exclude"
                + ConstantTextFileSystem.getInstance().getSlash() + "logos";
    }
    @Override
    public List<DateForCalendarView> listDaysInYears(final String feedPath) {
        List<DateForCalendarView> dates = new ArrayList<>();
        Directory directory = directoryRepository.findDirectoryByNameEquals(feedPath.replace("/", ConstantTextFileSystem.getInstance().getSlash() ));
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
    public AudioIOFileManager getTrack(final String fullPath, final FeedRequestType feedRequestType) throws GetFeedError {
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

    private AudioIOFileManager findFileInCurrentFile(List<DataItem> dataItems, StringBuilder currentPath, String directories) throws GetFeedError {
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

    private List<DataItem> getDataItems(FeedRequestType feedRequestType, String currentPath) throws GetFeedError {
        List<DataItem> dataItems = directoryRepository.findDirectoryByNameEquals(currentPath).subFilesMapToDataItems();
        if(dataItems.isEmpty())
            throw new GetFeedError("The folder ("+ currentPath +") is empty therefore the "+ feedRequestType.label +" couldn't be found as IO Error, folder was empty");
        return dataItems;
    }

    private DataItem findIndexAndValidSubFile(List<DataItem> dataItems, String searchValue) throws GetFeedError {
        DataItem found = dataItems.stream().filter(d -> d.getFullPath().equals(searchValue)).findFirst().orElse(null);
        if(found == null)
            throw new GetFeedError("The file wasn't in the folder.");
        return found;
    }
    //if the user wants to, this method pick track based the time select


}