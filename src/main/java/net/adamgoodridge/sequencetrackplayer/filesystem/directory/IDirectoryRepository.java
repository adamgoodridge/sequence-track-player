package net.adamgoodridge.sequencetrackplayer.filesystem.directory;

public interface IDirectoryRepository {

    Directory findDirectoryByNameEquals(String name);
}
