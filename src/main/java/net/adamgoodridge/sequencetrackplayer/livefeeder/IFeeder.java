package net.adamgoodridge.sequencetrackplayer.livefeeder;

public interface IFeeder {
	String getUrl();
	
	Long getId();
	
	void setId(Long id);
	
	String getName();
	
	void setName(String name);
	
	String getLastUpdateTime();
	
	void setLastUpdateTime(String lastUpdateTime);
	
	void setUrl(String url);
	
	int getExternalId();
	
	void setExternalId(int externalId);
}
