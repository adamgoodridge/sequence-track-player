package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantTextFileSystemTest extends AbstractSpringBootTest {

    private ConstantTextFileSystem constantTextFileSystem;

    @BeforeEach
    void setUp() {
        constantTextFileSystem = ConstantTextFileSystem.getInstance();
    }

    @Test
    void testSingletonInstance() {
        ConstantTextFileSystem instance = ConstantTextFileSystem.getInstance();
        assertNotNull(instance);
        assertSame(constantTextFileSystem, instance, "ConstantTextFileSystem should be a singleton instance");
        assertEquals(constantTextFileSystem.getSlash(), instance.getSlash());
    }

    @Test
    void testGetSharePath() {
        assertEquals("/mnt/path/", constantTextFileSystem.getSharePath());
    }

    @Test
    void testGetWindowsSharePath() {
        assertEquals("P:\\AUDIOFILE\\FULLY_PROCESSED\\", constantTextFileSystem.getWindowsSharePath());
    }

    @Test
    void testGetSlash() {
        assertEquals("/", constantTextFileSystem.getSlash());
    }

    @Test
    void testGetForwardSlash() {
        assertEquals("/", constantTextFileSystem.getForwardSlash());
    }

    @Test
    void testGetWindowsSlash() {
        assertEquals("\\", constantTextFileSystem.getWindowsSlash());
    }
}
