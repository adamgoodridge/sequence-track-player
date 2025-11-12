package net.adamgoodridge.sequencetrackplayer.livefeeder;


import java.io.*;
import lombok.*;


@Data
public class FeederOutput implements Serializable, IFeeder {
	private Long id;
	private String name;
	private String lastUpdateTime;
	
	private int externalId;
	private String url;
	private boolean isRecent;
	
	
	public FeederOutput() {
		//needed for spring
	}
}
