package net.adamgoodridge.sequencetrackplayer;

import net.adamgoodridge.sequencetrackplayer.feeder.*;
import net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol.getindexstrategy.*;
import net.adamgoodridge.sequencetrackplayer.filesystem.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;

// load class definition for testing purposes before using MockContractor otherwise it will throw NoClassDefFoundError whenever accessing the class within a class that called in MockContractor
public class LoadClassDef {

	public static void initializeComponents() {
		PreferredRandomSettings preferredRandomSettings = PreferredRandomSettings.builder().build();
		new FeedRequest.Builder()
				.feedRequestType(FeedRequestType.BOOKMARK)
				.build();
		FileUtils.removeLeadingSlashIfExist("test/path");
		new GetIndexByRandomStrategy(preferredRandomSettings, new RandomNumberGenerator());
		new Path("test");
		new GetIndexByPathStrategy();
		new FileListSubFileWrapper();
		FileUtils.removeTrailingSlash("/test/path/");
	}
}
