package net.adamgoodridge.sequencetrackplayer.browser;

import java.util.*;

public class BrowserUtils {

    private BrowserUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<BreadCrumb>  generateFileItems(final String path) {
        List<BreadCrumb> fileItems = List.of(new BreadCrumb("Home Directory", ""));

        if(!path.isEmpty()) {
            fileItems = new ArrayList<>();
            StringBuilder pathSoFar = new StringBuilder();
            String[] folders = path.split("/");
            for (String folder : folders) {
                pathSoFar.append(folder).append("/");
                fileItems.add(new BreadCrumb(folder, pathSoFar.toString()));
            }
        }
        return fileItems;
    }
    public static String getFilePathFromUrl(final String urlFull, final String baseURL){
        String url = urlFull;
        if(!url.endsWith("/"))
            url += "/";
         int startIndex = url.indexOf(baseURL) + baseURL.length();
        return url.substring(startIndex);

    }
    // FEED/2021/2021-03_March/2021-03-25_Thursday if input FEED/2021/2021-03_March it will return 2021-03-25_Thursday if input
    public static String lastBreadCrumb(List<BreadCrumb> fileItems, String currentPath) {
        int childIndex = fileItems.stream().map(BreadCrumb::path).toList().indexOf(currentPath) + 1;
        return fileItems.get(childIndex).name();
    }
    //returns a blank list if path is ""
    public static  List<BreadCrumb> generateBreadCrumbs(final String path) {
        return generateFileItems(path);
    }
}
