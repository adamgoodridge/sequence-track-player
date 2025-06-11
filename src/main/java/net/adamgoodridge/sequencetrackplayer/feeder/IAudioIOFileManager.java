package net.adamgoodridge.sequencetrackplayer.feeder;

public interface IAudioIOFileManager {
    String getUrl();

    DataItem getFile();

    void increaseFileNo();

    boolean isAtEnd();

    int getFileNo();

    AudioIOFileManager getParentDir();

    boolean isDir();


    Boolean isEmpty();

    void decreaseFileNo();

    String getCurrentFileName();

    int getCurrentPosition();

    void setCurrentPosition(int currentPosition);

    @Override
    String toString();

}
