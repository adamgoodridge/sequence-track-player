package net.adamgoodridge.sequencetrackplayer.directory;

public interface IDirectoryRepository {

    Directory findDirectoryByNameEquals(String name);
}
