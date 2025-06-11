package net.adamgoodridge.sequencetrackplayer.directory;

import net.adamgoodridge.sequencetrackplayer.*;

import java.io.*;
import java.util.*;

public class FileSystemRepository implements IDirectoryRepository {
    @Override
    public Directory findDirectoryByNameEquals(String name) {
        String fullPath = name;
        if(!name.startsWith(ConstantTextFileSystem.getInstance().getSharePath()))
           fullPath = ConstantTextFileSystem.getInstance().getSharePath() + name;
        //         System.out.println(fullPath);
        String[] files = new File(fullPath).list();
        if(files == null ||files.length == 0)
            return new Directory(fullPath, new String[0]);
        Arrays.sort(files);
        return new Directory(name, files);
    }
}
