package net.adamgoodridge.sequencetrackplayer.feeder;


import lombok.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import org.springframework.data.annotation.*;

import java.io.*;
//todo remove this class, it is not used anymore combine with DataItem in the filesystem package
//blocker old data in the database
@NoArgsConstructor
public class DataItem {
    private String name;
    @Getter
    private String href;
    @Transient
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
        if(path == null) {
            path = new Path(name);
        }
        return path.toString();
    }

    public String getFileName() {
        return path.getFileName();
    }

    public boolean isDirectory() {
        return new Path(name).isDirectory();
    }
}