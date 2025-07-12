package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.browser.BreadCrumb;
import net.adamgoodridge.sequencetrackplayer.browser.BrowserUtils;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrowserUtilsTests {

    @Test
    void generateFileItems_WithEmptyPath_ReturnsNull() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems("");
        assertEquals(1, result.size());
        assertEquals("Home Directory", result.get(0).name());
        assertEquals("", result.get(0).path());
    }

    @Test
    void generateFileItems_WithValidPath_ReturnsCorrectItems() {
        List<BreadCrumb> result = BrowserUtils.generateFileItems("folder1/folder2/folder3");

        assertEquals(3, result.size());
        assertEquals("folder1", result.get(0).name());
        assertEquals("folder1/", result.get(0).path());
        assertEquals("folder3", result.get(2).name());
        assertEquals("folder1/folder2/folder3/", result.get(2).path());
    }

    @Test
    void getFilePathFromUrl_WithValidUrl_ReturnsCorrectPath() {
        String result = BrowserUtils.getFilePathFromUrl("http://example.com/base/path1/path2", "/base/");
        assertEquals("path1/path2/", result);
    }

    @Test
    void getFilePathFromUrl_WithUrlEndingInSlash_ReturnsCorrectPath() {
        String result = BrowserUtils.getFilePathFromUrl("http://example.com/base/path1/path2/", "/base/");
        assertEquals("path1/path2/", result);
    }

    @Test
    void fileInFolder_ReturnsNextItem() {
        List<BreadCrumb> items = BrowserUtils.generateFileItems("FEED/2021/2021-03_March/2021-03-25_Thursday");
        String result = BrowserUtils.lastBreadCrumb(items, "FEED/2021/2021-03_March/");
        assertEquals("2021-03-25_Thursday", result);
    }

    @Test
    void generateBreadCrumbs_WithEmptyPath_ReturnsNull() {
        List<BreadCrumb> result = BrowserUtils.generateBreadCrumbs("");

        assertEquals(1, result.size());
        assertEquals("Home Directory", result.get(0).name());
        assertEquals("", result.get(0).path());
    }

    @Test
    void generateBreadCrumbs_WithValidPath_IncludesHomeDirectory() {
        List<BreadCrumb> result = BrowserUtils.generateBreadCrumbs("folder1/folder2");

        assertEquals(2, result.size());
        assertEquals("folder1", result.get(0).name());
        assertEquals("folder1/", result.get(0).path());
        assertEquals("folder2", result.get(1).name());
        assertEquals("folder1/folder2/", result.get(1).path());
    }
}