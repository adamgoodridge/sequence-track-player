package net.adamgoodridge.sequencetrackplayer.livefeeder;


import java.io.*;


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
	
	@Override
	public String getUrl() {
		return url;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	
	@Override
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int getExternalId() {
		return externalId;
	}

	@Override
	public void setExternalId(int externalId) {
		this.externalId = externalId;
	}

	public boolean isRecent() {
		return isRecent;
	}
	
	public void setRecent(boolean recent) {
		isRecent = recent;
	}
}
