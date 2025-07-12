package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;

import java.util.Arrays;

public class FileSystemRepository implements IDirectoryRepository {
    public FileSystemRepository() {
        // Default constructor
    }

    @Override
    public Directory findDirectoryByNameEquals(String name) {
        Path path = new Path(name);
        String directoryName = path.toString();
        String[] files = FileListSubFileWrapper.wrap(directoryName);
        if(files == null ||files.length == 0)
            return new Directory(directoryName, new String[0]);
        Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
        return new Directory(name, files);
    }
}
