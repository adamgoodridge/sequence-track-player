package net.adamgoodridge.sequencetrackplayer.directory;


import org.springframework.data.mongodb.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface DbDirectoryRepository extends MongoRepository<Directory, String>, IDirectoryRepository{

    Directory findDirectoryByNameEquals(String name);
}

