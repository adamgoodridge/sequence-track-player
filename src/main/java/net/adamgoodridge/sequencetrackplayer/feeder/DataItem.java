package net.adamgoodridge.sequencetrackplayer.feeder;


import net.adamgoodridge.sequencetrackplayer.*;

import java.io.*;

public class DataItem {
    private  String name;
    private String href;

    public DataItem() {
    }


    public DataItem(String name) {
        ConstantTextFileSystem constantTextFileSystem = ConstantTextFileSystem.getInstance();
        this.name = name.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath()).replace("/", constantTextFileSystem.getWindowsSlash());
        this.href = name.replace(constantTextFileSystem.getSharePath(), ConstantText.DEFAULT_SERVER_URL).replace(constantTextFileSystem.getSlash(),"/");
    }
    //for non dav
    public DataItem(File file) {
        this(file.getAbsolutePath());
    }
    public String getFullPath() {
        return name.replace(ConstantTextFileSystem.getInstance().getWindowsSharePath(), ConstantTextFileSystem.getInstance().getSharePath()).replace("\\",ConstantTextFileSystem.getInstance().getSlash());
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getFileName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return !(name.endsWith(".mp3") || name.endsWith(".m4a"));
    }
    
    
    public String getFullPathLocalFileSystem() {
        if(name.startsWith(ConstantTextFileSystem.getInstance().getSharePath()))
            return name;

        return ConstantTextFileSystem.getInstance().getSharePath() +  name.replace(ConstantTextFileSystem.getInstance().getWindowsSharePath(), "").replace("\\",ConstantTextFileSystem.getInstance().getSlash());
    }
}