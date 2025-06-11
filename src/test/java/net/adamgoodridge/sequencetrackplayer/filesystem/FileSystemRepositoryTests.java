package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.AbstractSpringBootTest;
import net.adamgoodridge.sequencetrackplayer.ConstantTextFileSystem;
import net.adamgoodridge.sequencetrackplayer.directory.Directory;
import net.adamgoodridge.sequencetrackplayer.directory.FileSystemRepository;
import net.adamgoodridge.sequencetrackplayer.mock.FileSystemMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import net.adamgoodridge.sequencetrackplayer.testData.Contants;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileSystemRepositoryTests extends AbstractSpringBootTest {

    @Mock
    private ConstantTextFileSystem constantTextFileSystem;

    private FileSystemRepository fileSystemRepository;

    @BeforeEach
    void setUp() {
        try (MockedStatic<ConstantTextFileSystem> mockedStatic = mockStatic(ConstantTextFileSystem.class)) {
            mockedStatic.when(ConstantTextFileSystem::getInstance).thenReturn(constantTextFileSystem);
            fileSystemRepository = new FileSystemRepository();
        }
    }
    @Test
    void findDirectoryByName_invalidFolderName_ReturnsEmpty() {
        // Given
        String name = "testDir";
        String fullPath = Contants.TEST_ROOT + name;
        String[] expectedFiles = new String[]{};

        try (MockedConstruction<File> ignored = FileSystemMock.process(fullPath, expectedFiles)) {

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(fullPath);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedFiles, result.getSubFiles());
            assertEquals(fullPath, result.getName());
        }

    }

    @Test
    void findDirectoryByNameEquals_WithSharePath_ReturnsDirectory() {
        // Given
        String fullPath = "/mnt/path/testDir";
        String[] expectedFiles = new String[]{"file1.txt", "file2.txt"};
        try (MockedConstruction<File> mocked = FileSystemMock.process(fullPath,expectedFiles)) {

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(fullPath);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedFiles, result.getSubFiles());
        }

    }

    @Test
    void findDirectoryByNameEquals_EmptyDirectory_ReturnsEmptyArray() {
        // Given
        String name = "emptyDir";
        String fullPath = Contants.TEST_ROOT + "emptyDir";

        try (MockedConstruction<File> mocked = mockConstruction(File.class,
                (mock, context) -> when(mock.list()).thenReturn(null))) {

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(name);

            // Then
            assertNotNull(result);
            assertNotNull(result.getSubFiles());
            assertEquals(0, result.getSubFiles().length);
            assertEquals(fullPath, result.getName());
        }
    }

    @Test
    void findDirectoryByNameEquals_SortsFiles() {
        // Given
        String name = "/mnt/path/testDir";

        String[] unsortedFiles = new String[]{"c.txt", "a.txt", "b.txt"};
        String[] expectedSortedFiles = new String[]{"a.txt", "b.txt", "c.txt"};

        try (MockedConstruction<File> mocked = FileSystemMock.process(name, unsortedFiles)) {

            // When
            Directory result = fileSystemRepository.findDirectoryByNameEquals(name);

            // Then
            assertNotNull(result);
            assertArrayEquals(expectedSortedFiles, result.getSubFiles());
            assertEquals(name, result.getName());
        }
    }
}