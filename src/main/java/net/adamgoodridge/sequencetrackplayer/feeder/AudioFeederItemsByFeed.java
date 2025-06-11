package net.adamgoodridge.sequencetrackplayer.feeder;

import net.adamgoodridge.sequencetrackplayer.thymeleaf.*;

import java.util.*;
/*
groups feeds by FeedName
 */

public class AudioFeederItemsByFeed {
	private final String feedName;
	
	private final List<AudioFeederItemDTO> audioFeederItems;
	
	public AudioFeederItemsByFeed(String feedName) {
		this.feedName = feedName;
		audioFeederItems = new ArrayList<>();
	}
	
	//sorts feeds by feedName
	public static List<AudioFeederItemsByFeed> sort(List<AudioFeederItemDTO> unsortedItems) {
		//sort items by id first, then by feed name
		List<AudioFeederItemDTO> audioFeederItems = new ArrayList<>(unsortedItems);
		if (audioFeederItems.isEmpty()) {
			return List.of();
		}
		audioFeederItems.sort(Comparator.comparingLong(AudioFeederItemDTO::getId));

		List<AudioFeederItemsByFeed> sortList = new ArrayList<>();

		
		AudioFeederItemsByFeed currentGroup = new AudioFeederItemsByFeed(audioFeederItems.get(0).getFeedName());
		for(AudioFeederItemDTO feeder: audioFeederItems) {
			String feedName = feeder.getFeedName();
			if(!currentGroup.feedName.equals(feedName)) {
				sortList.add(currentGroup);
				currentGroup = new AudioFeederItemsByFeed(feedName);
			}
			currentGroup.add(feeder);
		}
		sortList.add(currentGroup);
		return sortList;
	}
	
	public void add(AudioFeederItemDTO item) {
		audioFeederItems.add(item);
	}
	//thymeleaf
	@SuppressWarnings("unused")
	public List<AudioFeederItemDTO> getAudioFeederItems() {
		return audioFeederItems;
	}
	//thymeleaf
	public String headerName() {
		return feedName + " (" +audioFeederItems.size() + ")";
	}
	
}
