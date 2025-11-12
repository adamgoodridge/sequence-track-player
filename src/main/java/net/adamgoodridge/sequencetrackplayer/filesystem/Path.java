package net.adamgoodridge.sequencetrackplayer.filesystem;

import net.adamgoodridge.sequencetrackplayer.constanttext.*;

import java.util.*;


public class Path {
	private static final String DEFAULT_SERVER_URL = ConstantText.DEFAULT_SERVER_URL;
	private static final String SHARE_PATH = ConstantTextFileSystem.getInstance().getSharePath();
	private static final String SLASH = ConstantTextFileSystem.getInstance().getSlash();
	private static final String WINDOWS_SHARE_PATH = ConstantTextFileSystem.getInstance().getWindowsSharePath();
	private static final String WINDOWS_SLASH = ConstantTextFileSystem.getInstance().getWindowsSlash();
	private final List<String> tree;
	public Path() {
		this.tree = new ArrayList<>();
	}
	public Path(String in) {
		if (in == null || in.isEmpty()) {
			this.tree = new ArrayList<>();
			return;
		}
		in = standardizePath(in);
		this.tree = new ArrayList<>(
				List.of(getRidOfDoubleSlash(in).split(SLASH))
		);
	}

	private static String standardizePath(String in) {
		in = in.replace(ConstantTextFileSystem.getInstance().getWindowsSharePath(), "");
		in = in.replace(ConstantTextFileSystem.getInstance().getSharePath() ,"");
		in = in.replace("/", SLASH);
		in = in.replace("\\", SLASH);
		return in;
	}
	public Path getTopPath() {
		return new Path(tree.stream()
				.filter(s -> !s.isEmpty())
				.findFirst()
				.orElse(""));
	}

	public String getFileName() {
		return tree.get(tree.size() - 1);
	}

	private static String getRidOfDoubleSlash(String in) {
		return in.replaceAll(SLASH + "+", SLASH);
	}

	@Override
	public String toString() {
		String fulPath = ConstantTextFileSystem.getInstance().getSharePath() + tree.stream()
				.filter(s -> !s.isEmpty())
				.reduce((s1, s2) -> s1 + SLASH + s2)
				.orElse("");
		return FileUtils.removeTrailingSlash(fulPath);
	}

	public void addFile(String fileName) {
		tree.addAll(Arrays.asList(standardizePath(fileName).split(SLASH)));
	}
	public static String join(String... paths) {
		Path path = new Path();
		for (String p : paths) {
			if(p == null || p.isEmpty()) {
				continue; // Skip null or empty paths
			}
			path.addFile(p);
		}
		return path.toString();
	}

	public Path getNextPathRemoveFromSearchFor(Path currentPath) {
		String nextFileName = tree.get(currentPath.size());
		Path nextPath = new Path(currentPath.toString());
		nextPath.addFile(nextFileName);
		return nextPath;
	}

	private int size() {
		return tree.size();
	}


	public boolean isDirectory() {
		return !(getFileName().endsWith(".mp3") || getFileName().endsWith(".m4a"));
	}

    public String getUrl() {
        return this.toString().replace(SHARE_PATH, DEFAULT_SERVER_URL).replace(SLASH, "/");
    }

    public String getWindowsPath() {
        return this.toString().replace(SHARE_PATH, WINDOWS_SHARE_PATH)
                .replace("/", WINDOWS_SLASH);
    }
}
