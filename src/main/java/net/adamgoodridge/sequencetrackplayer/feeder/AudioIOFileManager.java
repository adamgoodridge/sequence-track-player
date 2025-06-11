package net.adamgoodridge.sequencetrackplayer.feeder;


import org.springframework.context.annotation.*;

import java.util.*;

@Primary
public class AudioIOFileManager implements IAudioIOFileManager {
    private int fileNo;
    private int currentPosition;
    private List<DataItem> files;
    private AudioIOFileManager parentDir;
    //needed for hibernate
    public AudioIOFileManager() {
    }
    public AudioIOFileManager(List<DataItem> files, int fileNo) {
        this.files = new ArrayList<>(files);
        this.fileNo = fileNo;

    }

    public AudioIOFileManager(List<DataItem> children, int folderNo, AudioIOFileManager audioIOFileManager) {
        this.files = new ArrayList<>(children);
        this.fileNo = folderNo;
        this.parentDir = audioIOFileManager;
    }


    @Override
    public String getUrl() {
        return files.get(fileNo).getHref();
    }
    @Override
    public DataItem getFile() {
        return files.get(fileNo);  }

    @Override
    public void increaseFileNo() {
        fileNo++;
        currentPosition = 0;
    }


    @Override
    public boolean isAtEnd() {
        //fileNo went over the amount of files, index
        return fileNo >= files.size();
    }

    public void setParentDir(AudioIOFileManager parentDir) {
        this.parentDir = parentDir;
    }

    @Override
    public int getFileNo() {
        return fileNo;
    }

    @Override
    public AudioIOFileManager getParentDir() {
        return parentDir;
    }

    @Override
    public boolean isDir() {
        return files.get(fileNo).isDirectory();
    }

    @Override
    public Boolean isEmpty() {
        return files.isEmpty();
    }

    @Override
    public void decreaseFileNo() {
        fileNo--;
    }

    @Override
    public String getCurrentFileName() {
        return files.get(fileNo).getFullPath();
    }

    @Override
    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<DataItem> getFiles() {
        return files;
    }

    @SuppressWarnings("unused")
    private void setFiles(List<DataItem> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "AudioIOFileManager{" +
                "fileNo=" + fileNo +
                ", files" + getFile().getFullPath() +
                ", parentDir=" + parentDir +
                '}';
    }
}
