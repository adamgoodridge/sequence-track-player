package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.filesystem.directory.*;
import net.adamgoodridge.sequencetrackplayer.mock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.adamgoodridge.sequencetrackplayer.testData.Contants;
import org.mockito.*;

import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
//Specific Runner for Mockito & PowerMock
class FileSystemRepositoryTests extends AbstractSpringBootTest {
    @InjectMocks
    private final FileSystemRepository fileSystemRepository = new FileSystemRepository();
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        new FileListSubFileWrapper();
    }
    @Test
    void findDirectoryByName_invalidFolderName_ReturnsEmpty() {
        // Given
        String name = "testDir";
        String fullPath = Contants.TEST_ROOT + name;
        String[] expectedFiles = new String[]{};

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(fullPath);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedFiles, result.getSubFiles());
            assertEquals(fullPath, result.getName());

    }
    @Test
    void findDirectoryByNameEquals_WithSharePath_ReturnsDirectory() {
        // Given
        String fullPath = Contants.TEST_ROOT + "testDir";
        String[] expectedFiles = new String[]{"file1.txt", "file2.txt"};
        try (MockedStatic<FileListSubFileWrapper> mockedStatic = mockStatic(FileListSubFileWrapper.class)) {
            mockFileSystem(mockedStatic);
            when(FileListSubFileWrapper.wrap(fullPath)).thenReturn(expectedFiles);
            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(fullPath);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedFiles, result.getSubFiles());
        }

    }

    public static void mockFileSystem(MockedStatic<FileListSubFileWrapper> mockedStatic) {
        String fullPath = Contants.TEST_ROOT + "testDir";
        String[] expectedFiles = new String[]{"file1.txt", "file2.txt"};
        mockedStatic.when(() -> FileListSubFileWrapper.wrap(fullPath)).thenReturn(expectedFiles);
    }
    @Test
    void findDirectoryByNameEquals_EmptyDirectory_ReturnsEmptyArray() {
        // Given
        String name = "emptyDir";
        String fullPath = Contants.TEST_ROOT + "emptyDir";


            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(name);

            // Then
            assertNotNull(result);
            assertNotNull(result.getSubFiles());
            assertEquals(0, result.getSubFiles().length);
            assertEquals(fullPath, result.getName());
    }

    @Test
    void findDirectoryByNameEquals_SortsFiles() {
        try (MockedConstruction<File> ignored = FileSystemMockConstruction.MockFromJsonFile()) {
            // Given
            String name = Contants.TEST_ROOT + "testDirUnsorted";

            String[] expectedSortedFiles = new String[]{"a.txt", "b.txt", "c.txt"};

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(name);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedSortedFiles, result.getSubFiles());
            assertEquals(name, result.getName());
        }

    }
}