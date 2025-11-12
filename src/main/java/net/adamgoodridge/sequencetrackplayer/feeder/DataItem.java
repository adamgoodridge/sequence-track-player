package net.adamgoodridge.sequencetrackplayer.feeder;


import lombok.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import java.io.*;
//todo remove this class, it is not used anymore combine with DataItem in the filesystem package
//blocker old data in the database
@Data
@NoArgsConstructor
public class DataItem {
    private String name;
    private String href;
    private Path path;


    public DataItem(String name) {
        this.path = new Path(name);
        this.name = path.getWindowsPath();
        this.href = path.getUrl();
    }

    //for non dav
    public DataItem(File file) {
        this(file.getAbsolutePath());
    }

    public String getFullPath() {
        return path.toString();
    }

    public String getFileName() {
        return path.getFileName();
    }

    public boolean isDirectory() {
        return new Path(name).isDirectory();
    }
}