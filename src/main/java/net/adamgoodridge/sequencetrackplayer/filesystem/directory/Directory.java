package net.adamgoodridge.sequencetrackplayer.filesystem.directory;

import net.adamgoodridge.sequencetrackplayer.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.Path;
import net.adamgoodridge.sequencetrackplayer.feeder.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Document("radioDirectories")
public class Directory {
    @Id
    private String id;
    @Field(value = "name")
    private String name;
    @Field(value = "subFiles")
    private String[] subFiles;



    public Directory() {
    }

    public Directory(String name) {
        this.name = name;
    }

    public Directory(String name, String[] subFiles) {
        this.name = name;
        this.subFiles = subFiles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSubFiles() {
        return subFiles;
    }

    public String[] getSubFilesFullPath() {
        return Arrays.stream(subFiles)
                .map(subFile ->{
                    Path path = new Path(name);
                    path.addFile( subFile);
                    return path.toString();
                        })
                .toArray(String[]::new);
    }

    public void setSubFiles(String[] subFiles) {
        this.subFiles = subFiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return Objects.equals(id, directory.id) && Objects.equals(name, directory.name) && Arrays.equals(subFiles, directory.subFiles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name);
        result = 31 * result + Arrays.hashCode(subFiles);
        return result;
    }

    public List<DataItem>subFilesMapToDataItems() {
        String root = name.endsWith(ConstantTextFileSystem.getInstance().getSlash()) ? name : name + ConstantTextFileSystem.getInstance().getSlash();
        return Arrays.stream(Objects.requireNonNull(subFiles))
                .map(subFile -> new DataItem(Path.join(root, subFile))).toList();
    }
    public boolean containItem(String searchValue) {
        if(isEmpty()) return false;
        return Arrays.asList(searchValue).contains(searchValue);
    }

    public boolean isEmpty() {
        return subFiles.length == 0;
    }
}
