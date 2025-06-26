package net.adamgoodridge.sequencetrackplayer.feeder;


import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;

import javax.persistence.*;
import java.io.*;
//todo remove this class, it is not used anymore combine with DataItem in the filesystem package
//blocker old data in the database
public class DataItem {
    private  String name;
    private String href;


    public DataItem() {
    }




    public DataItem(String name) {
        Path path = new Path(name);
        this.name = path.getWindowsPath();
        this.href = path.getUrl();
    }
    //for non dav
    public DataItem(File file) {
        this(file.getAbsolutePath());
    }
    public String getFullPath() {
        return new Path(name).toString();
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getFileName() {
        return new Path(name).getFileName();
    }

    public void setName(String name) {
        System.out.println("Setting name: " + name);
        this.name = name;
    }

    public boolean isDirectory() {
        return new Path(name).isDirectory();
    }
    
    
    public String getFullPathLocalFileSystem() {
        return new Path(name).toString();
    }
}