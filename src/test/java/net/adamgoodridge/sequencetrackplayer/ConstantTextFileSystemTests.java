package net.adamgoodridge.sequencetrackplayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantTextFileSystemTests extends AbstractSpringBootTest {

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
        assertEquals("/share/path", constantTextFileSystem.getSharePath());
    }

    @Test
    void testGetWindowsSharePath() {
        assertEquals("S:\\windows\\share\\path", constantTextFileSystem.getWindowsSharePath());
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
        assertEquals("\\\\", constantTextFileSystem.getWindowsSlash());
    }
}
