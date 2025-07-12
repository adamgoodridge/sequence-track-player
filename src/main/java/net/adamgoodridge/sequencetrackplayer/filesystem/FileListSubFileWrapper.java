package net.adamgoodridge.sequencetrackplayer.filesystem;

import java.io.*;

public class FileListSubFileWrapper {
	public static String[] wrap(String parentPath) {
		return new File(parentPath).list();
	}
}
