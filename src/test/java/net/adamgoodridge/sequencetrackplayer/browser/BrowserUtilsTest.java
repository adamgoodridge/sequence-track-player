package net.adamgoodridge.sequencetrackplayer.browser;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrowserUtilsTest {

    @Test
    void generateFileItems_WithNullPath_ReturnsNull() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems(null);
        assertEquals(0, result.size());
    }

    @Test
    void generateFileItems_WithEmptyPath_ReturnsNull() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems("");
        assertEquals("Home Directory", result.get(0).name());
        assertEquals("", result.get(0).path());
    }

    @Test
    void generateFileItems_WithSingleFolder_ReturnsCorrectItem() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems("folder1");

        assertEquals(1, result.size());
        assertEquals("folder1", result.get(0).name());
        assertEquals("folder1/", result.get(0).path());
    }

    @Test
    void generateFileItems_WithMultipleFolders_ReturnsCorrectItems() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems("folder1/folder2/folder3");

        assertEquals(3, result.size());
        assertEquals("folder1", result.get(0).name());
        assertEquals("folder1/", result.get(0).path());
        assertEquals("folder2", result.get(1).name());
        assertEquals("folder1/folder2/", result.get(1).path());
        assertEquals("folder3", result.get(2).name());
        assertEquals("folder1/folder2/folder3/", result.get(2).path());
    }

    @Test
    void getFilePathFromUrl_WithValidUrl_ReturnsCorrectPath() {
        String result = BrowserUtils.getFilePathFromUrl("http://example.com/base/path/to/file", "/base/");
        assertEquals("path/to/file/", result);
    }

    @Test
    void getFilePathFromUrl_WithUrlEndingInSlash_ReturnsCorrectPath() {
        String result = BrowserUtils.getFilePathFromUrl("http://example.com/base/path/to/file/", "/base/");
        assertEquals("path/to/file/", result);
    }

    @Test
    void lastBreadCrumb_WithValidPath_ReturnsNextItem() {
        List<BreadCrumb> items = BrowserUtils.generateFileItems("folder1/folder2/folder3/folder4");
        String result = BrowserUtils.lastBreadCrumb(items, "folder1/folder2/folder3/");
        assertEquals("folder4", result);
    }

    @Test
    void generateBreadCrumbs_WithEmptyPath_ReturnsHomeDirectory() {
        List<BreadCrumb> result = BrowserUtils.generateBreadCrumbs("");

        assertEquals(1, result.size());
        assertEquals("Home Directory", result.get(0).name());
        assertEquals("", result.get(0).path());
    }

    @Test
    void generateBreadCrumbs_WithValidPath_ReturnsCorrectItems() {
        List<BreadCrumb> result = BrowserUtils.generateBreadCrumbs("folder1/folder2");

        assertEquals(2, result.size());
        assertEquals("folder1", result.get(0).name());
        assertEquals("folder1/", result.get(0).path());
        assertEquals("folder2", result.get(1).name());
        assertEquals("folder1/folder2/", result.get(1).path());
    }
}