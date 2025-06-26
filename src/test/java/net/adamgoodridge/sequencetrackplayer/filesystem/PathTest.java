package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathTest extends AbstractSpringBootTest {
    private ConstantTextFileSystem constantTextFileSystem;
    private String slash;

    @BeforeEach
    void setUp() {
        constantTextFileSystem = ConstantTextFileSystem.getInstance();
        slash = constantTextFileSystem.getSlash();
    }

    @Test
    void testPathInitializationDefaultConstructor() {
        String folder = "folder";
        Path path = new Path();
        path.addFile(folder);
        //assertions
        assertNotNull(path);
        assertEquals(folder, path.getFileName());
        String expectedPath = constantTextFileSystem.getSharePath() + folder;        
        assertEquals(expectedPath, path.toString());
        assertEquals(expectedPath.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), path.getWindowsPath());
    }

    @Test
    void testAddFile() {
        String inputPath = constantTextFileSystem.getSharePath() + "folder";
        Path path = new Path(inputPath);

        path.addFile("subfolder");
        path.addFile("file.txt");

        assertEquals("file.txt", path.getFileName());
        assertEquals(inputPath + slash + "subfolder/file.txt", path.toString());
        assertEquals((inputPath + slash + "subfolder/file.txt").replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), path.getWindowsPath());
    }

    @Test
    void testAddFileTwoPath() {
        String inputPath = constantTextFileSystem.getSharePath() + "folder";
        Path path = new Path(inputPath);

        path.addFile("subfolder\\file.txt");

        assertEquals("file.txt", path.getFileName());
        assertEquals(inputPath + slash + "subfolder/file.txt", path.toString());
        assertEquals((inputPath + slash + "subfolder/file.txt").replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), path.getWindowsPath());
    }

    @Test
    void testWindowsAddFileTwoPath() {
        String inputPath = constantTextFileSystem.getWindowsSharePath() + "folder";
        Path path = new Path(inputPath);

        path.addFile("subfolder\\file.txt");

        assertEquals("file.txt", path.getFileName());
        assertEquals(constantTextFileSystem.getSharePath() + "folder/subfolder/file.txt", path.toString());
        assertEquals((constantTextFileSystem.getSharePath() + "folder/subfolder/file.txt").replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), path.getWindowsPath());
    }

    @Test
    void testJoinPaths() {
        String joinedPath = Path.join(
                constantTextFileSystem.getSharePath(),
                "folder",
                "subfolder",
                "file.txt"
        );

        String expectedPath = constantTextFileSystem.getSharePath() + "folder/subfolder/file.txt";
        assertEquals(expectedPath, joinedPath);
        assertEquals(expectedPath.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), new Path(joinedPath).getWindowsPath());
    }

    @Test
    void testPathValidationRemovesDoubleSlashes() {
        String inputPath = constantTextFileSystem.getSharePath() + "folder//subfolder///file.txt";
        Path path = new Path(inputPath);

        assertEquals("file.txt", path.getFileName());
        assertEquals(constantTextFileSystem.getSharePath() + "folder/subfolder/file.txt", path.toString());
        assertEquals((constantTextFileSystem.getSharePath() + "folder/subfolder/file.txt").replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), path.getWindowsPath());
    }

    @Test
    void testJoinHandlesEmptyPaths() {
        String joinedPath = Path.join("", constantTextFileSystem.getSharePath(), "folder", "");
        String expectedPath = constantTextFileSystem.getSharePath() + "folder";
        assertEquals(expectedPath, joinedPath);
        assertEquals(expectedPath.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), new Path(joinedPath).getWindowsPath());
    }

    @Test
    void testJoinHandlesTrailingSlashes() {
        String joinedPath = Path.join(constantTextFileSystem.getSharePath() + "/", "folder/", "subfolder/");
        String expectedPath = constantTextFileSystem.getSharePath() + "folder/subfolder";
        assertEquals(expectedPath, joinedPath);
        assertEquals(expectedPath.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), new Path(joinedPath).getWindowsPath());
    }

    @Test
    void testJoinHandlesNullPaths() {
        String joinedPath = Path.join(null, constantTextFileSystem.getSharePath(), "folder", null);
        String expectedPath = constantTextFileSystem.getSharePath() + "folder";
        assertEquals(expectedPath, joinedPath);
        assertEquals(expectedPath.replace(constantTextFileSystem.getSharePath(), constantTextFileSystem.getWindowsSharePath())
                .replace("/", constantTextFileSystem.getWindowsSlash()), new Path(joinedPath).getWindowsPath());
    }/*
    @Test
    void testGetNextInPath() {
        Path searchPath = new Path("folder/subfolder/demo/demotest/file.txt");
        Path currentPath = new Path("folder/subfolder");
        searchPath.getNextPathRemoveFromSearchFor(currentPath);
        String nextPath = currentPath.toString();
       searchPath.getNextPathRemoveFromSearchFor(currentPath);
        String secondPart = currentPath.toString();
        assertEquals("/mnt/path/folder/subfolder/demo", nextPath);
        assertEquals("/mnt/path/folder/subfolder/demo/demotest", secondPart);
    }*/
}

